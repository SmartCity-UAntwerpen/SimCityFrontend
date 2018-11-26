# SimCityFrontend

Frontend for the Simulation part of the SmartCity project.
To run locally: Go to the VM-options and add "-Dspring.config.name=application-dev"

Was originally part of the project SmartCityProject.  
Does NOT include the simulation runners.

## Communications protocol
The SimCity frontend communicates with the simulation runners over TCP.
Changes to the protocol are possible for each type of bot. These are the common commands. Commands **always** end with a newline ``\n``

A full description of the commands can be found on Blackboard > documentatie 2016 - 2017 > Interfaces > Simulation interfaces.  

The used id is only used between the simulation deployer en the simulation frontend. 