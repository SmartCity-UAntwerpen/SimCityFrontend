package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.security.User;
import be.uantwerpen.sc.services.security.UserService;
import be.uantwerpen.sc.tools.DevelopersList;
import be.uantwerpen.sc.tools.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 27/02/2016.
 */
@Controller
public class HomeController extends GlobalModelController
{
    @Autowired
    DevelopersList developersList;

    @Autowired
    UserService userService;

    // Homepage
    @RequestMapping(value = {"/"})
    @PreAuthorize("hasRole('logon')")
    public String showHomepage(ModelMap model)
    {
        return "protected/homepage";
    }

    // About page
    @RequestMapping(value = {"/about"})
    public String showAboutpage(ModelMap model)
    {
        try
        {
            List<String> supervisors = developersList.getSupervisorDevelopers();
            model.addAttribute("supervisors", supervisors);

            Map<String, List<String>> students = developersList.getStudentDevelopers();
            model.addAttribute("students", students);
        }
        catch(Exception e)
        {
            Terminal.printTerminalError(e.getMessage());
        }

        return "public/about";
    }

    // Help page
    @RequestMapping(value = {"/help"})
    @PreAuthorize("hasRole('logon')")
    public String showHelpPage()
    {
        return "protected/help";
    }

    // Active user profile page
    @RequestMapping(value = {"/profile"})
    @PreAuthorize("hasRole('logon')")
    public String showProfile(HttpServletRequest request, ModelMap model)
    {
        User currentUser = userService.getPrincipalUser();
        model.addAttribute("currentUser", currentUser);
        return "protected/profile";
    }
}