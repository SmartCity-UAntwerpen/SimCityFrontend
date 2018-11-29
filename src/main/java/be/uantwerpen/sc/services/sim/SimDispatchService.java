package be.uantwerpen.sc.services.sim;

import be.uantwerpen.sc.models.sim.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        if(supervisorService.addNewBot(bot))
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

        // Find a worker of the bot type to fill the workerId, ip and port field of the new bot
        List<SimWorker> botTypeWorkers = workerService.findWorkersByType(botType);
        SimWorker lowestAmountBotsWorker = botTypeWorkers.get(0);

        for(SimWorker worker : botTypeWorkers)
        {
            if(worker.getStatus().equals("ONLINE"))
            {
                // Check workers of the botType and put the bot on the worker with least amount of bots
                if(supervisorService.amountBotsWorker(worker.getId()) <= supervisorService.amountBotsWorker(lowestAmountBotsWorker.getId()))
                {
                    lowestAmountBotsWorker = worker;
                }
            }
        }

        long workerId = lowestAmountBotsWorker.getId();
        String workerServerURL = lowestAmountBotsWorker.getServerURL();
        String[] ipPort = workerServerURL.split(":");

        switch(botType.toLowerCase().trim())
        {
            case "car":
                simBot = new SimCar();
                simBot.setServerCoreAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                simBot.setWorkerId(workerId);
                break;
            case "drone":
                simBot = new SimDrone();
                simBot.setServerCoreAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                simBot.setWorkerId(workerId);
                break;
            case "f1":
                simBot = new SimF1();
                simBot.setServerCoreAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                simBot.setWorkerId(workerId);
                break;
            default:
                simBot = null;
                break;
        }

        return simBot;
    }
}

