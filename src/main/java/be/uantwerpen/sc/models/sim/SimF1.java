package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.models.sim.messages.F1WayPoint;
import be.uantwerpen.sc.services.sockets.SimSocket;
import be.uantwerpen.sc.services.vehicleBackends.F1Backend;
import be.uantwerpen.sc.tools.AutomaticStartingPointException;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Thomas on 5/05/2017.
 */
// Class for simulated F1-car
public class SimF1 extends SimVehicle
{

    private F1Backend f1Backend;

    public SimF1()
    {
        super("bot", 0, 90);

        this.type = "f1";
    }

    public SimF1(String name, int startPoint, long simSpeed)
    {
        super(name, startPoint, simSpeed);

        this.type = "f1";
    }

    public SimF1(F1Backend backend) {
        super("bot", 0, 90);
        this.f1Backend = backend;

        this.type = "f1";
    }

    @Override
    protected void simulationProcess() {
    }

    @Override
    public boolean parseProperty(String property) throws Exception
    {
        if(super.parseProperty(property))
        {
            return true;
        }

        switch(property.toLowerCase().trim())
        {
            default:
                return false;
        }
    }

    @Override
    public boolean parseProperty(String property, String value) throws Exception
    {
        if(super.parseProperty(property, value)) {
            //Create socket connection to core
            try {
                SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
                simSocket.setTimeOut(500);

                //Send data over socket
                if (simSocket.sendMessage("set " + id + " " + property + " " + value + "\n")) {
                    String response = simSocket.getMessage();
                    while (response == null) {
                        response = simSocket.getMessage();
                    }
                    //Receive ACK when property is successfully set in the core
                    if (response.equalsIgnoreCase("ACK")) {
                        System.out.println("Set acknowledge received.");
                        simSocket.close();
                        return true;
                    } else if (response.equalsIgnoreCase("NACK")) {
                        System.out.println("NACK received. Property could not be set in core.");
                        simSocket.close();
                        return false;
                    } else {
                        System.out.println("Unknown response received. Bot was stopped.");
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

        switch(property.toLowerCase().trim())
        {
            default:
                return false;
        }
    }

    @Override
    public boolean printProperty(String property)
    {
        if(super.printProperty(property))
        {
            return true;
        }

        switch(property.toLowerCase().trim())
        {
            default:
                return false;
        }
    }

    @Override
    public void setAutomaticStartPoint() throws AutomaticStartingPointException {
        Map<Long, F1WayPoint> waypoints = f1Backend.getWayPoints();
        Object[] ids =  waypoints.keySet().toArray();

        // Choose random waypoint from the set
        Random rand = new Random();
        Long randomId = (Long) ids[rand.nextInt(ids.length)];

        try {
            this.parseProperty("startpoint",randomId.toString());
        } catch (Exception e) {
           AutomaticStartingPointException startingPointException = new AutomaticStartingPointException(e.getMessage());
           startingPointException.setStackTrace(e.getStackTrace()); // copy stacktrace for debugging
           throw startingPointException;
        }
    }
}

