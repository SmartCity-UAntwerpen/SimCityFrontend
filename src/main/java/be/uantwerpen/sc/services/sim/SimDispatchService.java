package be.uantwerpen.sc.services.sim;

import be.uantwerpen.sc.models.sim.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Thomas on 5/05/2017.
 */
// Service for dispatching several bot type objects
@Service
public class SimDispatchService
{
    @Autowired
    SimSupervisorService supervisorService;

    @Autowired
    SimWorkerService workerService;

    @Value("${rc.core.ip}")
    private String robotCoreIp;

    @Value("${ds.core.ip}")
    private String droneCoreIp;

    @Value("${f1.core.ip}")
    private String f1CoreIp;

    @Value("#{new Integer(${rc.core.port})}")
    private int robotCorePort;

    @Value("#{new Integer(${ds.core.port})}")
    private int droneCorePort;

    @Value("#{new Integer(${f1.core.port})}")
    private int f1CorePort;

    public SimDispatchService()
    {

    }

    public SimBot instantiateBot(String type)
    {
        SimBot bot = this.parseBot(type);

        if(bot == null)
        {
            return null;
        }
        // Find a worker of the bot type to fill the workerId field in the bot
        long workerId = 0L;
        for(SimWorker worker : workerService.findAll())
            if(worker.getWorkerType().toString().equals(type))
            {
                workerId = worker.getId();
            }

        if(supervisorService.addNewBot(bot,workerId))
        {
            return bot;
        }
        else
        {
            return null;
        }
    }

    private SimBot parseBot(String botType)
    {
        SimBot simBot;

        switch(botType.toLowerCase().trim())
        {
            case "car":
                simBot = new SimCar();
                simBot.setServerCoreAddress(robotCoreIp, robotCorePort);
                break;
            case "drone":
                simBot = new SimDrone();
                simBot.setServerCoreAddress(droneCoreIp, droneCorePort);
                break;
            case "f1":
                simBot = new SimF1();
                simBot.setServerCoreAddress(f1CoreIp, f1CorePort);
                break;
            default:
                simBot = null;
                break;
        }

        return simBot;
    }
}

