package be.uantwerpen.sc.services.sim;

import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.models.sim.SimWorkerType;
import be.uantwerpen.sc.repositories.sim.SimWorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Thomas on 03/04/2016.
 */
// Service for SimWorker actions
@Service
public class SimWorkerService
{
    @Autowired
    private SimWorkerRepository simWorkerRepository;

    public Iterable<SimWorker> findAll()
    {
        return this.simWorkerRepository.findAll();
    }

    public boolean delete(String workerName)
    {
        SimWorker w = findByWorkerName(workerName);

        if(w != null)
        {
            this.simWorkerRepository.delete(w.getId());

            return true;
        }

        return false;
    }

    public boolean delete(Long id)
    {
        SimWorker w = findById(id);

        if(w != null)
        {
            this.simWorkerRepository.delete(id);

            return true;
        }

        return false;
    }

    public boolean add(final SimWorker worker)
    {
        SimWorker w;
        Long i = 0L;
        w = findByWorkerId(i);

        while(w != null && i <= this.getNumberOfWorkers() + 1)
        {
            w = findByWorkerId(i);
            i++;
        }
        if(i <= this.getNumberOfWorkers() + 1)
        {
            if (i == 0L)
            {
                worker.setWorkerId(0L);
                this.simWorkerRepository.save(worker);
            }
            else
            {
                worker.setWorkerId(i-1);
                this.simWorkerRepository.save(worker);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean save(SimWorker worker)
    {
        for(SimWorker w : findAll())
        {
            if(w.getId().equals(worker.getId()))
            {
                w.setWorkerName(worker.getWorkerName());
                w.setWorkerType(worker.getWorkerType());
                w.setServerURL(worker.getServerURL());
                w.setRecordTime(worker.getRecordTime());
                w.setBotList(worker.getBotList());
                w.setStatus(worker.getStatus());

                    if(simWorkerRepository.save(w) != null)
                    {
                        return true;
                    }
            }
        }
        return false;
    }

    public int getNumberOfWorkers()
    {
        return this.simWorkerRepository.findAll().size();
    }

    public SimWorker findByWorkerName(String workerName)
    {
        return simWorkerRepository.findByWorkerName(workerName);
    }

    public SimWorker findByWorkerType(SimWorkerType workerType)
    {
        return simWorkerRepository.findByWorkerType(workerType);
    }

    public SimWorker findByWorkerId(Long workerId)
    {
        return simWorkerRepository.findByWorkerId(workerId);
    }

    public SimWorker findById(Long id)
    {
        return simWorkerRepository.findById(id);
    }
}
