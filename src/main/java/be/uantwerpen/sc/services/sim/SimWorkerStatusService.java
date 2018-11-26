package be.uantwerpen.sc.services.sim;

import be.uantwerpen.sc.models.sim.SimWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SimWorkerStatusService {

    @Autowired
    SimWorkerService simWorkerService;

    @Scheduled(fixedRateString = "${worker.updaterate}")
    public void run() {

            for (SimWorker worker : simWorkerService.findAll()) {
                System.out.println("Updating status of worker: " + worker.getWorkerName());
                worker.updateStatus();
            }
    }
}
