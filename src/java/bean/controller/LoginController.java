package bean.controller;

import bean.exception.InvalidCredentialsException;
import bean.service.UserService;
import jpa.User;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import javax.faces.application.FacesMessage;

@ManagedBean
@SessionScoped
public class LoginController implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private UserService userService;

    private String username;
    private String password;
    private String errorMessage;

    public String login() {
        if (!credentialsMatch()) {
            return "login";
        }
        try {
            User user = userService.login(username, password);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("currentUser", user);
            return "index?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknown error.",  ""));
            return "login";
        }
    }

    private boolean credentialsMatch() {
        try {
            userService.login(username, password);
            return true;
        } catch (InvalidCredentialsException e) {
            System.out.println("ERR LoginController.credentialsMatch: INVALID CREDENTIALS EXCEPTION!");
            FacesContext.getCurrentInstance().addMessage("loginGrid",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid username or password.", ""));
            return false;
        }
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
