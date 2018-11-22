package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.sim.SimBot;
import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.services.sim.SimSupervisorService;
import be.uantwerpen.sc.services.sim.SimWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.net.*;
import java.util.*;

/**
 * Created by Thomas on 04/04/2016.
 */
// Add general model attributes
@Controller
public class GlobalModelController
{
    @Autowired
    private SimWorkerService simWorkerService;

    @Autowired
    private SimSupervisorService simSupervisorService;

    // Return worker count
    @ModelAttribute("numOfWorkers")
    public int getNumberOfWorkers()
    {
        return simWorkerService.getNumberOfWorkers();
    }

    // Return list constaining all workers
    @ModelAttribute("allWorkers")
    public Iterable<SimWorker> getAllWorkers()
    {
        return simWorkerService.findAll();
    }

    // Return bot count
    @ModelAttribute("numOfBots")
    public int getNumberOfBots()
    {
        return simSupervisorService.getNumberOfBots();
    }

    // Return list containing all bots
    @ModelAttribute("allBots")
    public Iterable<SimBot> getAllBots()
    {
        return simSupervisorService.findAll();
    }

    // Return front-end server name
    @ModelAttribute("serverName")
    public String getServerName()
    {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Server machine name could not be retrieved.";
        }
    }

    // Return front-end server first IPv4-address
    @ModelAttribute("serverIp")
    public String getServerIp()
    {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "No IP address assigned to this server machine";
        }
    }

    // Return all front-end server's IPv4-addresses
    @ModelAttribute("allServerIps")
    public List<String> getAllServerIps()
    {
        List<String> addresses = new ArrayList<String>();
        Enumeration interfaceList = null;
        try {
            interfaceList = NetworkInterface.getNetworkInterfaces();
            while(interfaceList.hasMoreElements())
            {
                NetworkInterface networkInterface = (NetworkInterface) interfaceList.nextElement();
                Enumeration addressesList = networkInterface.getInetAddresses();
                while (addressesList.hasMoreElements())
                {
                    InetAddress address = (InetAddress) addressesList.nextElement();

                    if(address instanceof Inet4Address && !address.isLoopbackAddress())
                    {
                        addresses.add(addresses.size(), address.getHostAddress().toString());
                    }
                }
            }
        } catch (SocketException se) {
            se.printStackTrace();
            addresses.add(addresses.size(), "No IP addresses assigned to this server machine");
        }
        return addresses;
    }
}
