package be.uantwerpen.sc.Messages;

import be.uantwerpen.sc.models.sim.SimWorkerType;
/**
 * Added By Andreas on 16/12/2019
 * Template of the STOMP Message (needs to be same on workers)
 */
public class WorkerMessage {

    private long workerID;
    private SimWorkerType workerType;
    private int status;
    private int botamount;

    public WorkerMessage() {
    }

    public WorkerMessage(long workerID, SimWorkerType workerType, int status, int botamount) {
        this.workerID = workerID;
        this.workerType = workerType;
        this.status = status;
        this.botamount = botamount;
    }

    public long getWorkerID() {
        return workerID;
    }

    public SimWorkerType getWorkerType() {
        return workerType;
    }

    public int getStatus() {
        return status;
    }

    public int getBotamount() {
        return botamount;
    }
}
