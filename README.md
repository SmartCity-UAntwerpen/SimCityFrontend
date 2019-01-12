# SimCityFrontend

Frontend for the Simulation part of the SmartCity project.
To run locally: Go to the VM-options and add ``-Dspring.config.name=application-dev``

Was originally part of the project SmartCityProject.  
Does NOT include the simulation runners, these are in separate repositories.

## Communications protocol
The SimCity frontend communicates with the simulation runners over TCP.
Changes to the protocol are possible for each type of bot. These are the common commands. Commands **always** end with a newline ``\n``

A full description of the commands can be found on Blackboard > documentatie 2016 - 2017 > Interfaces > Simulation interfaces.  
The used id is only used between the simulation deployer en the simulation frontend.  

⚠ A new addition to the protocol is the ``ping`` command. If the simulation worker is in good health, the response should always be ``PONG`` (in all caps). Any other response will mark the worker with an ``UNKOWN`` status.  

Additionally, the command ``set id startpoint`` can be given with parameter ``auto``, to select a starting point automatically. This mechanism is used for the drone and robot simulators.
Starting points for the racecar are selected through a REST-api.
