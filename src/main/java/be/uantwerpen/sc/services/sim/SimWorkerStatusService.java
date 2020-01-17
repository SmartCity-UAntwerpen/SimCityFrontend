package be.uantwerpen.sc.services.sim;

import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.models.sim.SimWorkerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SimWorkerStatusService {

    private static final Logger logger = LoggerFactory.getLogger(SimWorkerStatusService.class);

    @Autowired
    SimWorkerService simWorkerService;

    @Scheduled(fixedRateString = "${worker.updaterate}")
    public void run() {

            for (SimWorker worker : simWorkerService.findAll()) {
                logger.info("Updating status of worker: " +  worker.getWorkerName());
                if(worker.getWorkerType() == SimWorkerType.f1){
                    worker.updateStatus();
                }
                simWorkerService.save(worker);
            }
    }
}
