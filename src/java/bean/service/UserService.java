/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.service;

import bean.exception.InvalidCredentialsException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import jpa.User;
import jpa.UserFacade;

/**
 *
 * @author boone
 */
@Stateless
public class UserService {

    @EJB
    private UserFacade userFacade;
    
    @Inject
    private UserSession userSession;

    public User register(String username, String password, String email, boolean isAdmin) {
        System.out.println("Largest userId: " + userFacade.findNextId());
        User user = new User(userFacade.findNextId() + 1);
        user.setUsername(username);
        user.setPassword(password); // TODO: hashing lozinke
        user.setEmail(email);
        user.setIsAdmin(isAdmin);
        user.setBookingCollection(new ArrayList<>());
        userFacade.create(user);
        userSession.setUser(user); 
        return user;
    }

    public User login(String username, String password) throws InvalidCredentialsException {
        try {
            User user = userFacade.findByUsernameAndPassword(username, password);
            userSession.setUser(user);
            return user;
        } catch (InvalidCredentialsException e) {
            System.out.println("ERR UserService.login: INVALID CREDENTIALS EXCEPTION!");
            throw new InvalidCredentialsException("Invalid username or password. Exception: "
                        + Arrays.toString(e.getStackTrace()));
        }
    }
    
}