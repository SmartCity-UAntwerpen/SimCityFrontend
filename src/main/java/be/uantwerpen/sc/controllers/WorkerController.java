package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.Messages.ServerMessage;
import be.uantwerpen.sc.Messages.WorkerJob;
import be.uantwerpen.sc.Messages.WorkerMessage;
import be.uantwerpen.sc.models.sim.SimBot;
import be.uantwerpen.sc.models.sim.SimForm;
import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.models.sim.SimWorkerType;
import be.uantwerpen.sc.services.sim.SimSupervisorService;
import be.uantwerpen.sc.services.sim.SimWorkerService;
import be.uantwerpen.sc.tools.PropertiesList;
import be.uantwerpen.sc.tools.TypesList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas on 03/04/2016.
 */
@Controller
public class WorkerController extends GlobalModelController
{
    @Autowired
    private SimpMessagingTemplate template; //template for sending stomp messages

    @Autowired
    private SimWorkerService workerService;

    @Autowired
    private SimSupervisorService supervisorService;

    //added on 16/12/2019
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Show worker page
    @RequestMapping(value = "/workers")
    @PreAuthorize("hasRole('logon')")
    public String showWorkerpage(ModelMap model)
    {
        return "protected/workerpage";
    }

    // Delete worker with certain ID
    @RequestMapping(value="/workers/{id}/delete")
    @PreAuthorize("hasRole('logon')")
    public String deleteWorker(@Validated @ModelAttribute("worker") SimWorker worker, ModelMap model)
    {
        long workerId = worker.getId();
        // Check if no bots are using this worker
        List<SimBot> botList = supervisorService.findAllBotsByWorkerID(workerId);

        // Don't delete if it still has bots assigned
        if(botList.size() > 0) {
            return "redirect:/settings/workers?errorWorkerHasBots";
        }
        else if(workerService.delete(workerId))
        {
            shutdownWorker(workerId);
            model.clear();
            return "redirect:/settings/workers?workerRemoved";
        }
        else
        {
            return "redirect:/settings/workers?errorWorkerRemove";
        }
    }

    // Add new worker
    @RequestMapping(value="/workers/add", method= RequestMethod.POST)
    @PreAuthorize("hasRole('logon')")
    public String addWorker(@Validated @ModelAttribute("worker") SimWorker worker, BindingResult result, HttpServletRequest request, SessionStatus sessionStatus, ModelMap model)
    {
        if(result.hasErrors())
        {
            return "protected/settings/workers";
        }

        if(workerService.add(worker))
        {
            return "redirect:/settings/workers?workerAdded";
        }
        else
        {
            return "redirect:/settings/workers?errorAlreadyExists";
        }
    }

    // Edit worker with certain ID
    @RequestMapping(value="/workers/{workerId}", method= RequestMethod.POST)
    @PreAuthorize("hasRole('logon')")
    public String editWorker(@Validated @ModelAttribute("worker") SimWorker worker, BindingResult result, HttpServletRequest request, SessionStatus sessionStatus, ModelMap model)
    {
        String[] path = request.getServletPath().split("/");
        worker.setId(Long.parseLong(path[2]));

        if(result.hasErrors())
        {
            return "protected/settings/workers";
        }

        SimWorker w = workerService.findById(worker.getId());
        w.setWorkerName(worker.getWorkerName());
        w.setServerURL(worker.getServerURL());
        w.setWorkerType(worker.getWorkerType());

        if(workerService.save(w))
        {
            return "redirect:/settings/workers?workerEdited";
        }
        else
        {
            return "redirect:/settings/workers";
        }
    }

    // Show worker management page with bot actions
    @RequestMapping(value="/workers/{workerId}/management/", method= RequestMethod.GET)
    public String manageWorker(ModelMap model, @PathVariable String workerId) throws Exception
    {
        List<SimBot> workerBots = supervisorService.findAllBotsByWorkerID(Long.parseLong(workerId));
        model.addAttribute("allWorkerBots", workerBots);

        SimWorker w = workerService.findById(Long.parseLong(workerId));
        model.addAttribute("worker", w);

        return "protected/workerManagement";
    }

    //Created on 16/12/2019
    //!!!!This method is in use: it receives messages from the workers
    //It gets invoked by the websockets.
    @MessageMapping("/worker/{topic}")
    @SendToUser("/queue/worker")
    public ServerMessage receive(WorkerMessage message) throws Exception{
        SimWorker w = new SimWorker();
        w.setStatus("ONLINE");
        String temp = SimWorkerType.TypeToString(message.getWorkerType());
        long workerID = 0L;
        w.setWorkerName("TEMP");
        w.setWorkerType(message.getWorkerType());
        workerService.add(w);
        for(SimWorker wTemp: workerService.findAll()){
            if(wTemp.getId() > workerID){
                workerID = wTemp.getId();
            }
        }
        SimWorker tempWorker = workerService.findById(workerID);
        tempWorker.setWorkerName(temp + ":" + workerID);
        workerService.save(tempWorker);
        return new ServerMessage(workerID, WorkerJob.CONNECTION,0," ");
    }

    @MessageMapping("/worker")
    public void shutdownWorker(long workerID){
        System.out.println("shutting down worker " + workerID);
        ServerMessage message = new ServerMessage(workerID,WorkerJob.CONNECTION,0,"SHUTDOWN");
        this.template.convertAndSend("/topic/shutdown",message);
    }
}
