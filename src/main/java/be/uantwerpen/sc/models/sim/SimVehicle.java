package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.tools.Terminal;

/**
 * Created by Thomas on 5/05/2017.
 */
// SimVehicle subclass for SimBot
public abstract class SimVehicle extends SimBot
{
    protected int startPoint;
    protected long simSpeed;

    public SimVehicle(String name, int startPoint, long simSpeed)
    {
        super(name);

        this.type = "vehicle";

        this.startPoint = startPoint;
        this.simSpeed = simSpeed;
    }

    public void setStartPoint(int startPoint)
    {
        this.startPoint = startPoint;
    }

    @Override
    public int getStartPoint() {
        return this.startPoint;
    }

    public void setSimSpeed(int simSpeed)
    {
        this.simSpeed = simSpeed;
    }

    @Override
    public Long getSimSpeed() {
        return this.simSpeed;
    }

    @Override
    public boolean parseProperty(String property, String value) throws Exception
    {
        if(super.parseProperty(property, value))
        {
            return true;
        }

        switch(property.toLowerCase().trim())
        {
            case "speed":
                try
                {
                    int speed = parseInteger(value);

                    this.setSimSpeed(speed);

                    return true;
                }
                catch(Exception e)
                {
                    throw new Exception("Could not parse value for speed setting! " + e.getMessage());
                }
            case "startpoint":
                try
                {
                    int startPoint = parseInteger(value);

                    this.setStartPoint(startPoint);

                    return true;
                }
                catch(Exception e)
                {
                    throw new Exception("Could not parse value for start point setting! " + e.getMessage());
                }
            default:
                return false;
        }
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
            case "speed":
                return true;
            case "startpoint":
                return true;
            default:
                return false;
        }
    }

    private int parseInteger(String value) throws Exception
    {
        int parsedInt;

        try
        {
            parsedInt = Integer.parseInt(value);
        }
        catch(NumberFormatException e)
        {
            throw new Exception("'" + value + "' is not an integer value!");
        }

        return parsedInt;
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
            case "speed":
                Terminal.printTerminalAppend(String.valueOf(simSpeed) + "\n");
                return true;
            case "startpoint":
                Terminal.printTerminalAppend(String.valueOf(startPoint) + "\n");
                return true;
            default:
                return false;
        }
    }

    /**
     * Set the vehicle to an automatic startpoint by intelligently choosing the best place
     * @return If it was succesful or not
     */
    public boolean setAutomaticStartPoint() {
        System.out.println("Setting automatic starting point is not supported!");
        return false;
    }
}
