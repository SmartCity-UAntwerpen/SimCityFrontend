package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.Messages.ServerMessage;
import be.uantwerpen.sc.Messages.WorkerJob;
import be.uantwerpen.sc.models.sim.*;
import be.uantwerpen.sc.services.sim.SimDispatchService;
import be.uantwerpen.sc.services.sim.SimSupervisorService;
import be.uantwerpen.sc.services.sim.SimWorkerService;
import be.uantwerpen.sc.tools.AutomaticStartingPointException;
import be.uantwerpen.sc.tools.PropertiesList;
import be.uantwerpen.sc.tools.Terminal;
import be.uantwerpen.sc.tools.TypesList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 5/05/2017.
 */
@Controller
public class BotController extends GlobalModelController{

    private static final Logger logger = LoggerFactory.getLogger(BotController.class);

    @Autowired
    private SimpMessagingTemplate template; //template for sending stomp messages

    @Autowired
    SimDispatchService dispatchService;

    @Autowired
    SimSupervisorService supervisorService;

    @Autowired
    SimWorkerService workerService;

    @Autowired
    TypesList typesList;

    private Terminal terminal;
    private boolean responded = false;
    private boolean acknowleged = false;
    private long ackID = 0L;
    private HashMap<ServerMessage, Boolean> acknowledgements;
    // Returns bot management page
    @RequestMapping(value = {"/bots"})
    @PreAuthorize("hasRole('logon')")
    public String showBotsSettings(ModelMap model, @Validated @ModelAttribute("type") String type) throws Exception {
        List<String> types = new ArrayList<String>();
        try {
            types = typesList.getTypes();
            model.addAttribute("types", types);
        } catch (Exception e) {
            types.add("No types could be loaded!");
            e.printStackTrace();
            model.addAttribute("types", types);
        }

        SimForm botForm = new SimForm();
        List<String> properties = new PropertiesList().getProperties();

        model.addAttribute("bot", botForm);
        model.addAttribute("properties", properties);

        return "protected/botManagement";
    }

    // Create bot
    @RequestMapping(value="/bots/create/{type}")
    @PreAuthorize("hasRole('logon')")
    public String createBot(@Validated @ModelAttribute("type") String type, @RequestParam(value = "autoStartPoint", defaultValue = "false") Boolean autoStartPoint, RedirectAttributes redir)
    {
        try {
            if (this.instantiateBot(type, autoStartPoint)) {
                return "redirect:/bots/?botCreatedSuccess";
            } else {
                return "redirect:/bots/?botCreatedFailed";
            }
        }
        catch (AutomaticStartingPointException e) {
            logger.warn("Error while settings starting point: "+e.getMessage());
            redir.addFlashAttribute("errormsg",e.getMessage());
            return "redirect:/bots/?botStartingPointFailed";
        }
    }

    // Deploy multiple bots of a certain type at once
    @RequestMapping(value="/bots/deploy/{type}/{amount}")
    @PreAuthorize("hasRole('logon')")
    public String deployBots(ModelMap model, @PathVariable String type, @RequestParam(value = "autoStartPoint", defaultValue = "false") Boolean autoStartPoint, @PathVariable String amount, RedirectAttributes redir)
    {

        try {
            if (this.instantiateBots(type, Integer.parseInt(amount), autoStartPoint)) {
                return "redirect:/bots/?botsDeployedSuccess";
            } else {
                return "redirect:/bots/?botsDeployedFailed";
            }
        }
        catch (AutomaticStartingPointException e) {
            logger.warn("Error while settings starting point for bots: "+e.getMessage());
            redir.addFlashAttribute("errormsg",e.getMessage());
            return "redirect:/bots/?botsStartingPointFailed";
        }
    }

    // Run bot with certain ID
    @RequestMapping(value="/bots/run/{botId}")
    @PreAuthorize("hasRole('logon')")
    public String runBot(@PathVariable int botId, ModelMap model) throws Exception
    {
        if(this.startBot(botId))
        {
            return "redirect:/bots/?botStartedSuccess";
        }
        else
        {
            return "redirect:/bots/?botStartedFailed";
        }
    }

/*
    // NOT IMPLEMENTED IN FRONTEND YET: Run bots with IDs in a certain range
    @RequestMapping(value="/bots/run/{botId1}/{botId2}")
    @PreAuthorize("hasRole('logon')")
    public String runBots(ModelMap model)
    {
        int botId1, botId2;
        botId1 = this.parseInteger(botId1);
        botId2 = this.parseInteger(botId2);

        if(this.startBots(botId1, botId2))
        {
            return "redirect:/bots/?botsStartedSuccess";
        }
        else
        {
            return "redirect:/bots/?botsStartedFailed";
        }
    }
*/

    // Stop bot with certain ID
    @RequestMapping(value="/bots/stop/{botId}")
    @PreAuthorize("hasRole('logon')")
    public String stopBot(@PathVariable int botId, ModelMap model) throws Exception
    {
        if(this.stopBot(botId))
        {
            return "redirect:/bots/?botStoppedSuccess";
        }
        else
        {
            return "redirect:/bots/?botStoppedFailed";
        }
    }

    // Restart bot with certain ID
    @RequestMapping(value="/bots/restart/{botId}")
    @PreAuthorize("hasRole('logon')")
    public String restartBot(@PathVariable int botId, ModelMap model)
    {
        if(this.restartBot(botId))
        {
            return "redirect:/bots/?botRestartedSuccess";
        }
        else
        {
            return "redirect:/bots/?botRestartedFailed";
        }
    }

    // Delete bot with certain ID
    @RequestMapping(value="/bots/delete/{botId}")
    @PreAuthorize("hasRole('logon')")
    public String killBot(@PathVariable int botId, ModelMap model)
    {
        if(this.killBot(botId))
        {
            return "redirect:/bots/?botKilledSuccess";
        }
        else
        {
            return "redirect:/bots/?botKilledFailed";
        }
    }

    // Delete all bots
    @RequestMapping(value="/bots/deleteAll")
    @PreAuthorize("hasRole('logon')")
    public String killAllBots(ModelMap model)
    {
        if(this.killAllBots())
        {
            return "redirect:/bots/?botsDeletedSuccess";
        }
        else
        {
            return "redirect:/bots/?botsDeletedFailed";
        }
    }

    // Set property to value for bot with a certain ID
    @RequestMapping(value="/bots/set/{botId}/{property}/{value}")
    @PreAuthorize("hasRole('logon')")
    public String setBot(@PathVariable int botId, @PathVariable String property, @PathVariable String value, ModelMap model)
    {
        if(this.setBotProperty(botId, property, String.valueOf(value)))
        {
            return "redirect:/bots/?botEditedSuccess";
        }
        else
        {
            return "redirect:/bots/?botEditedFailed";
        }
    }

    // Create bot in worker back-end
    private boolean instantiateBot(String type, boolean autoStartPoint) throws AutomaticStartingPointException
    {
        SimBot bot;

        switch(type.toLowerCase().trim())
        {
            case "car":
                bot = dispatchService.instantiateBot(type);
                RobotJob(bot.getId(),"",WorkerJob.BOT); //send job over websocket
                break;
            case "drone":
                bot = dispatchService.instantiateBot(type);
                RobotJob(bot.getId(),"",WorkerJob.BOT); //send job over websocket
                break;
            case "f1":
                bot = dispatchService.instantiateBot(type);break;
            default:
                terminal.printTerminalInfo("Bottype: '" + type + "' is unknown!");
                terminal.printTerminalInfo("Known types: {car | drone | f1}");
                return false;
        }
        //Wait for an acknowledge from the worker
        if(!waitForResponse(bot.getId())){
            supervisorService.removeBot(bot.getId());
            return false;
        }
        if(bot != null)
        {
            logger.info("New bot of type: '" + bot.getType() + "' and name: '" + bot.getName() + "' instantiated.");
            //terminal.printTerminalInfo("New bot of type: '" + bot.getType() + "' and name: '" + bot.getName() + "' instantiated.");
            if(autoStartPoint) {
                RobotJob(bot.getId(),"startpoint auto\n",WorkerJob.SET); //send job over websocket
                    if(!waitForResponse(bot.getId())){ //Wait for an acknowledge from the worker
                    supervisorService.removeBot(bot.getId());
                    return false;
                }
                setAutoStart(bot);
            } // could throw exception if not possible
            return true;
        }
        else
        {
            terminal.printTerminalError("Could not instantiate bot of type: " + type + "!");
            return false;
        }
    }

    private void setAutoStart(SimBot bot) throws AutomaticStartingPointException {
        if(bot instanceof SimVehicle) {
            SimVehicle vehicle = (SimVehicle) bot;
            //vehicle.setAutomaticStartPoint();
        }
        else {
            throw new AutomaticStartingPointException("Auto starting point is only supported for vehicles!");
        }

    }

    // Create multiple bots of a certain type in worker back-end
    private boolean instantiateBots(String type, int amount, boolean autoStartPoint) throws AutomaticStartingPointException {

        for(int i = 0; i < amount; i++)
        {
            SimBot bot = dispatchService.instantiateBot(type);
            RobotJob(bot.getId(),"",WorkerJob.BOT); //send job over websocket
            if(!waitForResponse(bot.getId())){                          //if acknowledged or timed out continue
                supervisorService.removeBot(bot.getId());
                terminal.printTerminalError("Could not instantiate bot of type: " + type + "!");
                return false;
            }
            if(bot == null)
            {
                terminal.printTerminalError("Could not instantiate bot of type: " + type + "!");
                return false;
            }
            else
            {
                if (autoStartPoint) setAutoStart(bot); // may throw exception if problems
                terminal.printTerminalInfo("New bot of type: '" + bot.getType() + "' and name: '" + bot.getName() + "' instantiated.");
            }
        }
        return true;
    }

    // Start both with certain ID in worker back-end
    private boolean startBot(int botId)
    {
        RobotJob(botId,"",WorkerJob.START);     //send job over websocket
        if(waitForResponse(botId)){                          //if acknowledged
            if(supervisorService.startBot(botId))
            {

                terminal.printTerminalInfo("Bot started with id: " + botId + ".");
                return true;
            }else
            {
                terminal.printTerminalError("Could not start bot with id: " + botId + "!");
                return false;
            }
        }else
        {
            terminal.printTerminalError("Could not start bot with id: " + botId + "!");
            return false;
        }


    }

    // Start bots with IDs in a certain range in worker back-end
    private boolean startBots(int botId1, int botId2)
    {
        boolean success = true;
        int i = botId1;
        while(success && i <= botId2)
        {
            success = supervisorService.startBot(i);
            if(success)
            {
                terminal.printTerminalInfo("Bot started with id: " + i + ".");
            }
            else
            {
                terminal.printTerminalError("Could not start bot with id: " + i + "!");
            }
            i++;
        }
        if(success)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Stop bot with certain ID in worker back-end
    private boolean stopBot(int botId)
    {
        RobotJob(botId,"",WorkerJob.STOP); //send job over websocket
        if(waitForResponse(botId)){                      //if acknowledged continue
            if(supervisorService.stopBot(botId))
            {

                terminal.printTerminalInfo("Bot stopped with id: " + botId + ".");
                return true;
            }
            else
            {
                terminal.printTerminalError("Could not stop bot with id: " + botId + "!");
                return false;
            }
        }else
        {
            terminal.printTerminalError("Could not stop bot with id: " + botId + "!");
            return false;
        }
    }

    // Restart bot with certain ID in worker back-end
    private boolean restartBot(int botId)
    {
        RobotJob(botId,"",WorkerJob.RESTART); //send job over websocket
        if(waitForResponse(botId)){                         //if acknowledged continue
            if(supervisorService.restartBot(botId))
            {
                terminal.printTerminalInfo("Bot restarted with id: " + botId + ".");
                return true;
            }
            else
            {
                terminal.printTerminalError("Could not restart bot with id: " + botId + "!");
                return false;
            }
        }else
        {
            terminal.printTerminalError("Could not restart bot with id: " + botId + "!");
            return false;
        }
    }

    // Kill bot with certain ID in worker back-end
    private boolean killBot(int botId)
    {
        RobotJob(botId,"",WorkerJob.KILL); //send job over websocket
        if(waitForResponse(botId)){                      //if acknowledged continue
            if(supervisorService.removeBot(botId))
            {

                terminal.printTerminalInfo("Bot killed with id: " + botId + ".");
                return true;
            }
            else
            {
                terminal.printTerminalError("Could not kill bot with id: " + botId + "!");
                return false;
            }
        }else
        {
            terminal.printTerminalError("Could not kill bot with id: " + botId + "!");
            return false;
        }
    }

    // Kill all bots in worker back-end
    private boolean killAllBots()
    {
        SimBot bot;
        int i = 0;
        boolean success = true;
        List<SimBot> botList = supervisorService.findAll();

        while(i < botList.size() && success)
        {
            bot = botList.get(i);
            if(bot != null)
            {
                success = supervisorService.removeBot(bot.getId());
                if(success)
                {
                    terminal.printTerminalInfo("Bot killed with id: " + bot.getId() + ".");
                    i--;
                }
                else
                {
                    terminal.printTerminalInfo("Could not kill bot with id: " + bot.getId() + "!");
                }
            }
            i++;
        }
        return success;
    }

    // Set property to value for bot with certain ID in worker back-end
    private boolean setBotProperty(int botId, String property, String value)
    {
        RobotJob(botId,property,WorkerJob.SET); //send job over websocket
        if(waitForResponse(botId)){                  //if acknowledged continue
            if(supervisorService.setBotProperty(botId, property, value))
            {

                terminal.printTerminalInfo("Property set for bot with id: " + botId + ".");
                return true;
            }
            else
            {
                terminal.printTerminalError("Could not set property for bot with id: " + botId + "!");
                return false;
            }
        }
        else
        {
            terminal.printTerminalError("Could not set property for bot with id: " + botId + "!");
            return false;
        }
    }

    // Convert string to int
    private int parseInteger(String value) throws Exception
    {
        int parsedInt;

        try
        {
            parsedInt = Integer.parseInt(value);
        }
        catch(NumberFormatException e)
        {
            throw new Exception("'" + value + "' is not an integer value!");
        }

        return parsedInt;
    }
    //Created on 16/12/2019
    //Send a job to a robot worker over websockets
    @MessageMapping("/Robot")
    public void RobotJob(int botID, String property, WorkerJob job)
    {
        System.out.println("testing robot");
        long ID = supervisorService.getBotWorkerID(botID);
        ServerMessage message = new ServerMessage(ID,job,botID,property);
        logger.info("Sending job: " + job + " to worker: " + ID);
        this.template.convertAndSend("/topic/messages",message);

    }
    //Receiver for answers to the sent jobs
    //!!! this method is used even though Intellij says not
    //it just listens and acts if message is received
    @MessageMapping("/Robot/topic/answers")
    public void AnswerReceived(ServerMessage message) throws Exception{
        this.acknowleged = false;
        if(message.getArguments().equalsIgnoreCase("OK")){
            logger.info("Acknowledge received from worker: " + message.getWorkerID() + " Task = " + message.getJob().toString());
            this.acknowleged = true;
        }else{
            logger.info("Acknowledge received from worker: " + message.getWorkerID() + " Task = " + message.getJob().toString());
            this.acknowleged = false;
        }
        this.ackID = message.getBotID(); //!!! needs to be set before result otherwise concurrency issue
        this.responded = true;           //as soon as this is set waitForResponse() activates leave last !!!
    }
    //Created on 16/12/2019
    //Send a job to a Drone worker over websockets
    @MessageMapping("/Drone/")
    public void DroneJob(int botID, String property, WorkerJob job)
    {
        System.out.println("testing Drone");
        long ID = supervisorService.getBotWorkerID(botID);
        ServerMessage message = new ServerMessage(ID,job,botID,property);
        this.template.convertAndSend("/topic/messages",message);
    }

    //This method is currently used as a buffer for awaiting response
    //#TODO Could be solved using ACK in STOMP header but not yet implemented
    private boolean waitForResponse(long botID){
        long t = System.currentTimeMillis();
        long end = t+20000;
        while(!this.responded && System.currentTimeMillis() < end){
            //wait
        }
        boolean temp = this.acknowleged;
        if(this.responded && botID != this.ackID){
            temp =  false;
        }
        //Set connection error when the worker could not be reached
        //#TODO gracefull shutdown of worker on frontend if persists
        //#TODO otherwise wait and retry????
        if(!this.responded){
            SimWorker tempWorker = workerService.findById(supervisorService.getBotWorkerID((int)botID));
            tempWorker.setStatus("CONNECTION ERROR");
            workerService.save(tempWorker);
        }
        this.responded = false;
        this.acknowleged = false;
        this.ackID = 0L;

        return true;
    }
}




