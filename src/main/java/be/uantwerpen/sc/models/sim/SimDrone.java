package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.services.sockets.SimSocket;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Thomas on 5/05/2017.
 */
// Class for simulated drones
public class SimDrone extends SimVehicle
{
    public SimDrone()
    {
        super("bot", 0, 90);

        this.type = "drone";
    }

    public SimDrone(String name, int startPoint, long simSpeed)
    {
        super(name, startPoint, simSpeed);

        this.type = "drone";
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
}
