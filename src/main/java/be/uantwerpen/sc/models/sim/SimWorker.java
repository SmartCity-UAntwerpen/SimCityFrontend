package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.models.MyAbstractPersistable;
import be.uantwerpen.sc.services.sockets.SimSocket;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;

/**
 * Created by Thomas on 03/04/2016.
 */
// Class for simulation workers
@Entity
@Table(name = "worker", schema = "", catalog = "simcity")
public class SimWorker extends MyAbstractPersistable<Long>
{
    private long workerId;
    private String workerName;
    private String serverURL;
    private SimWorkerType workerType;
    private Date recordTime;
    private String status;

    @Transient
    private List<SimBot> bots;

    public SimWorker()
    {
        this.workerId = 0L;
        this.workerName = "";
        this.workerType = null;
        this.serverURL = null;
        this.recordTime = null;
        this.bots = null;
        this.status = "UNKNOWN";
    }

    public SimWorker(String workerName, String serverURL, SimWorkerType workerType)
    {
        this.workerId = 0L;
        this.workerName = workerName;
        this.workerType = workerType;
        this.serverURL = serverURL;
        this.recordTime = null;
        this.bots = null;
        this.status = "UNKNOWN";
    }

    public void setWorkerId(long id)
    {
        this.workerId = id;
    }

    public long getWorkerId()
    {
        return this.workerId;
    }

    public void setWorkerName(String workerName)
    {
        this.workerName = workerName;
    }

    public String getWorkerName()
    {
        return this.workerName;
    }

    public SimWorkerType getWorkerType() {
        return workerType;
    }

    public void setWorkerType(SimWorkerType workerType) {
        this.workerType = workerType;
    }

    public void setServerURL(String serverURL)
    {
        this.serverURL = serverURL;
    }

    public String getServerURL()
    {
        return this.serverURL;
    }

    public void setRecordTime(Date recordTime)
    {
        this.recordTime = recordTime;
    }

    public Date getRecordTime()
    {
        return this.recordTime;
    }

    public void setBotList(List<SimBot> bots)
    {
        this.bots = bots;
    }

    public List<SimBot> getBotList()
    {
        return this.bots;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void updateStatus() {
        // split url into server and port
        String uri = this.serverURL.split(":")[0];
        int port = Integer.parseInt(this.serverURL.split(":")[1]);

        //Create socket connection to worker to get status
        try
        {
            SimSocket simSocket = new SimSocket(new Socket(uri, port));
            simSocket.setTimeOut(500);

            System.out.println("retrieving status from worker on "+uri+" on port "+port);

            //Send data over socket
            if(simSocket.sendMessage("ping\n"))
            {
                String response = simSocket.getMessage();
                while(response == null)
                {
                    response = simSocket.getMessage();
                }
                //Receive response when message is successfully received
                if(response.equalsIgnoreCase("PONG")) {
                    System.out.println("Ping successfull");
                    simSocket.close();
                    this.status = "ONLINE";
                } else {
                    System.out.println("Unknown response received.");
                    simSocket.close();
                    this.status = "ERROR";
                }
            } else {
                System.out.println("Socket connection could not be established.");
                simSocket.close();
                this.status = "CONNECTION ERROR";
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.status = "CONNECTION ERROR";
        }
    }
}
