package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.security.User;
import be.uantwerpen.sc.services.security.RoleService;
import be.uantwerpen.sc.services.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Thomas on 03/04/2016.
 */
@Controller
public class UserController extends GlobalModelController
{
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    // Allowed user fields
    @InitBinder
    private void allowFields(WebDataBinder webDataBinder)
    {
        webDataBinder.setAllowedFields("username", "firstName", "lastName", "password", "roles");
    }

    // Edit user information
    @RequestMapping(value="/users/{id}", method=RequestMethod.POST)
    @PreAuthorize("hasRole('logon')")
    public String editUser(@Validated @ModelAttribute("user") User user, BindingResult result, HttpServletRequest request, SessionStatus sessionStatus, ModelMap model)
    {
        if(result.hasErrors())
        {
            return "protected/settings/usersSettings";
        }

        String[] path = request.getServletPath().split("/");
        Long id = Long.parseLong(path[2]);

        User retrievedUser = userService.findByUsername(user.getUsername());
        if(retrievedUser != null && retrievedUser.getId() != user.getId())
        {
            return "redirect:/settings/users?errorAlreadyExists";
        }
        else
        {
            retrievedUser = userService.findOne(id);
            retrievedUser.setFirstName(user.getFirstName());
            retrievedUser.setLastName(user.getLastName());
            retrievedUser.setUsername(user.getUsername());
            retrievedUser.setPassword(user.getPassword());
            retrievedUser.setRoles(user.getRoles());
            userService.save(retrievedUser);
            return "redirect:/settings/users?userEdited";
        }
    }

    // Create new user
    @RequestMapping(value="/user/create", method=RequestMethod.POST)
    @PreAuthorize("hasRole('logon')")
    public String createUser(@Validated @ModelAttribute("user") User user, BindingResult result, HttpServletRequest request, SessionStatus sessionStatus, ModelMap model)
    {
        if(result.hasErrors())
        {
            return "protected/settings/users";
        }

        if(userService.add(user))
        {
            return "redirect:/settings/users?userAdded";
        }
        else
        {
            return "redirect:/settings/users?errorAlreadyExists";
        }
    }

    // Delete user
    @RequestMapping(value="/users/{id}/delete")
    @PreAuthorize("hasRole('logon')")
    public String deleteUser(@PathVariable String id, HttpServletRequest request, ModelMap model)
    {
        if(!id.isEmpty() && id.matches("^\\d+$")) {
            Long userId = Long.parseLong(id);
            if (!userService.getPrincipalUser().getId().equals(userId)) {
                userService.delete(userId);
                model.clear();
                return "redirect:/settings/users?userRemoved";
            } else {
                return "redirect:/settings/users?errorUserActive";
            }
        }
        else
        {
            return "redirect:/settings/users?errorUserRemove";
        }
    }
}
