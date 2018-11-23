package be.uantwerpen.sc.data;

import be.uantwerpen.sc.models.security.Permission;
import be.uantwerpen.sc.models.security.Role;
import be.uantwerpen.sc.models.security.User;
import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.models.sim.SimWorkerType;
import be.uantwerpen.sc.repositories.security.PermissionRepository;
import be.uantwerpen.sc.repositories.security.RoleRepository;
import be.uantwerpen.sc.repositories.security.UserRepository;
import be.uantwerpen.sc.repositories.sim.SimWorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 04/04/2016.
 */
// Database loader
@Service
@Profile({"default","standalone"})
public class DatabaseLoader
{
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SimWorkerRepository simWorkerRepository;

    @Autowired
    public DatabaseLoader(PermissionRepository permissionRepos, RoleRepository roleRepos, UserRepository userRepos, SimWorkerRepository simWorkerRepos)
    {
        this.permissionRepository = permissionRepos;
        this.roleRepository = roleRepos;
        this.userRepository = userRepos;
        this.simWorkerRepository = simWorkerRepos;
    }

    // Initialise different DBs
    @PostConstruct
    private void initDatabase()
    {
        //Check if tables are initialised or empty
        if(permissionRepository.findAll().iterator().hasNext())
        {
            //Tables are initialised, no need to refill database
            return;
        }

        //Initialise user database
        initUserDatabase();

        //Initialise worker database
        initWorkerDatabase();
    }

    // Configure initial users
    private void initUserDatabase()
    {
        List<Permission> allPermissions = new ArrayList<Permission>();
        List<Role> roles;

        //Default permissions
        allPermissions.add(new Permission("logon"));

        //Save permission to database
        Iterator<Permission> it = allPermissions.iterator();
        while(it.hasNext())
        {
            permissionRepository.save(it.next());
        }

        //Default roles
        Role administrator = new Role("Administrator");

        //Set administrator
        administrator.setPermissions(allPermissions);

        //Save roles to database
        roleRepository.save(administrator);

        //Default users
        User adminUser = new User("Admin", "", "admin", "admin");

        //Set adminUser
        roles = new ArrayList<Role>();
        roles.add(administrator);
        adminUser.setRoles(roles);

        //Save users to database
        userRepository.save(adminUser);
    }

    // Configure initial workers
    private void initWorkerDatabase()
    {
        //Test worker
        SimWorker worker1 = new SimWorker("worker-1", "localhost", SimWorkerType.SmartCar);

        //Save workers to database
        simWorkerRepository.save(worker1);
    }
}
