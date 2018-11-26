package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.models.sim.messages.SimBotStatus;
import be.uantwerpen.sc.services.sockets.SimSocket;
import be.uantwerpen.sc.tools.Terminal;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Thomas on 03/04/2016.
 */
// Abstract class for each simulated bot
public abstract class SimBot implements Runnable
{
    private Thread simulationThread;
    protected boolean running;
    protected int id;
    protected String type;
    protected String name;
    public String ip;
    public int port;
    private int workerId;

    protected SimBot()
    {
        this.running = false;
        this.type = "bot";
        this.ip = "localhost";
        this.port = 1994;

        this.name = "SimBot";
    }

    public SimBot(String name)
    {
        this();

        this.name = name;
    }

    public void setServerCoreAddress(String serverIP, int serverPort)
    {
        this.ip = serverIP;
        this.port = serverPort;
    }

    public boolean create()
    {
        if(!sendCommand("create " + id + "\n"))
        {
            return false;
        }
        return true;
    }

    public boolean start()
    {
        if(this.running)
        {
            return false;
        }

        if(!sendCommand("run " + id + "\n")) {
            return false;
        }

        this.simulationThread = new Thread(this);

        this.simulationThread.start();

        this.running = true;

        return true;
    }

    public boolean restart()
    {
        if(simulationThread != null)
        {
            if(this.running)
            {
                this.running = false;
            }

            while(simulationThread.isAlive() && getType() == "car")
            {
                //Wait for thread to stop
            }
        }
        if(this.sendCommand("restart " + id + "\n"))
        {
            this.running = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean stop()
    {
        if(!this.running || this.simulationThread == null)
        {
            return false;
        }

        if(this.running)
        {
            if(sendCommand("stop " + id + "\n")) {
                this.running = false;
                return true;
            }
        }
        return false;
    }

    public boolean remove()
    {
        if(sendCommand("kill " + id + "\n"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean interrupt()
    {
        if(this.simulationThread == null)
        {
            return false;
        }

        if(this.simulationThread.isAlive())
        {
            this.simulationThread.interrupt();
            this.running = false;
        }

        return true;
    }

    public boolean isRunning()
    {
        return this.running;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getType()
    {
        return this.type;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public SimBotStatus getBotStatus()
    {
        String status;

        if(this.running)
        {
            status = "RUNNING";
        }
        else
        {
            status = "OFF";
        }

        SimBotStatus simBotStatus = new SimBotStatus(this.id, this.type, this.name, status);

        return simBotStatus;
    }

    public String getLog()
    {
        return "No logging available for this bot!";
    }

    @Override
    public void run()
    {
        while(this.running)
        {
            this.simulationProcess();
        }
    }

    public abstract int getStartPoint();

    public abstract Long getSimSpeed();

    public boolean parseProperty(String property, String value) throws Exception
    {
        switch(property.toLowerCase().trim())
        {
            case "name":
                setName(value);
                return true;
            default:
                return false;
        }
    }

    public boolean parseProperty(String property) throws Exception
    {
        switch(property.toLowerCase().trim())
        {
            case "name":
                return true;
            default:
                return false;
        }
    }

    public boolean printProperty(String property)
    {
        switch(property.toLowerCase().trim())
        {
            case "name":
                Terminal.printTerminalAppend(name + "\n");
                return true;
            default:
                return false;
        }
    }

    protected boolean sendCommand(String message)
    {
        //Create socket connection to corresponding core
        try
        {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            if(simSocket.sendMessage(message))
            {
                String response = simSocket.getMessage();
                while(response == null)
                {
                    response = simSocket.getMessage();
                }
                //Receive ACK when message is successfully received and acknowledged
                if(response.equalsIgnoreCase("ACK")) {
                    System.out.println("Acknowledge received.");
                    simSocket.close();
                    return true;
                } else if(response.equalsIgnoreCase("NACK")) {
                    System.out.println("NACK received.");
                    simSocket.close();
                    return false;
                } else {
                    System.out.println("Unknown response received.");
                    simSocket.close();
                    return false;
                }
            } else {
                System.out.println("Socket connection could not be established.");
                simSocket.close();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            return false;
        }
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    abstract protected void simulationProcess();
}