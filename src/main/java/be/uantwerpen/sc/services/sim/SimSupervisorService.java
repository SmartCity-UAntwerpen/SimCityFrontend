package be.uantwerpen.sc.services.sim;

import be.uantwerpen.sc.models.sim.SimBot;
import be.uantwerpen.sc.models.sim.messages.SimBotStatus;
import be.uantwerpen.sc.tools.Terminal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Thomas on 5/05/2017.
 */
// Supervisor service for different bot actions
@Service
public class SimSupervisorService
{
    private List<SimBot> bots;

    /**
     * The most recently assigned id to a bot
     */
    private AtomicInteger nextId;

    public SimSupervisorService()
    {
        this.bots = new ArrayList<SimBot>();
        this.nextId = new AtomicInteger(0);
    }

    public SimBotStatus getBotStatus(int botId)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            return bot.getBotStatus();
        }
        else
        {
            return null;
        }
    }

    public List<SimBotStatus> getAllBotStats()
    {
        List<SimBotStatus> stats = new ArrayList<SimBotStatus>();

        for(SimBot simBot : this.bots)
        {
            stats.add(simBot.getBotStatus());
        }

        return stats;
    }

    public int getNumberOfBots()
    {
        return this.bots.size();
    }

    public String getBotLog(int botId)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            return bot.getLog();
        }
        else
        {
            return null;
        }
    }

    public boolean addNewBot(SimBot bot)
    {
        boolean status;
        int nextId = this.getNextId();

        bot.setId(nextId);
        bot.setName("bot-" + nextId);

        if(bot.create())
        {
            return bots.add(bot);
        }
        return false;
    }

    public boolean removeBot(int botId)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            return this.removeBot(bot);
        }
        else
        {
            return false;
        }
    }

    public boolean removeBot(String botName)
    {
        SimBot bot = this.getBot(botName);

        return this.removeBot(bot);
    }

    public boolean startBot(int botId)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            return bot.start();
        }
        else
        {
            return false;
        }
    }

    public boolean stopBot(int botId)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            return bot.stop();
        }
        else
        {
            return false;
        }
    }

    public boolean restartBot(int botId)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            return bot.restart();
        }
        else
        {
            return false;
        }
    }

    public boolean setBotProperty(int botId, String property, String value)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            if(bot.isRunning()) {
                Terminal.printTerminalError("Bot with id: " + botId + " is still running. Stop bot first and try again!");
                return false;
            } else {
                try {
                    return bot.parseProperty(property, value);
                } catch (Exception e) {
                    Terminal.printTerminalError(e.getMessage());
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    public boolean parseBotProperty(int botId, String property)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            try
            {
                return bot.parseProperty(property);
            }
            catch(Exception e)
            {
                Terminal.printTerminalError(e.getMessage());

                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean removeBot(SimBot bot)
    {
        if(bot != null)
        {
            bot.stop();
            bot.remove();

            // The following line is commented out because we want to be able to delete SimBots, even when the corresponding cores go down
            // Otherwise, bots are kept with an ID that isn't known in the corresponding cores and can never be deleted
            //return this.bots.remove(bot);

            this.bots.remove(bot);
            return true;
        }
        else
        {
            return false;
        }
    }

    private int getNextId()
    {
        return nextId.getAndIncrement();
    }

    private SimBot getBot(int botId)
    {
        Iterator<SimBot> it = this.bots.iterator();

        while(it.hasNext())
        {
            SimBot bot = it.next();

            if(bot.getId() == botId)
            {
                return bot;
            }
        }

        return null;
    }

    private SimBot getBot(String botName)
    {
        Iterator<SimBot> it = this.bots.iterator();

        while(it.hasNext())
        {
            SimBot bot = it.next();

            if(bot.getName() == botName)
            {
                return bot;
            }
        }

        return null;
    }

    public SimBot getBotByIndex(int index)
    {
        return this.bots.get(index);
    }

    public boolean printBotProperty(int botId, String property)
    {
        SimBot bot = this.getBot(botId);

        if(bot != null)
        {
            bot.printProperty(property);
            return true;
        }
        else
        {
            return false;
        }
    }

    public List<SimBot> findAll()
    {
        return this.bots;
    }

    public int getBotWorkerID(int botId)
    {
        SimBot bot = this.getBot(botId);
        return bot.getWorkerId();
    }

    public List<SimBot> findAllByWorkerID(int workerID)
    {
        List<SimBot> workerBots = new ArrayList<>();

        for(SimBot simBot : this.bots)
        {
            if(simBot.getWorkerId() == workerID)
            workerBots.add(simBot);
        }

        return workerBots;
    }
}

