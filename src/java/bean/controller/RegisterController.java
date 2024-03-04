package bean.controller;

import bean.service.UserService;
import jpa.User;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.ejb.EJB;
import java.io.Serializable;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@ManagedBean
@SessionScoped
public class RegisterController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserService userService;

    private String username;
    private String password;
    private String repeatPassword;
    private String email;

    public String register() {
        if (!passwordsMatch()) {
            return "register";
        }
        try {
            User user = userService.register(username, password, email, false);
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            session.setAttribute("currentUser", user);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration successful!", "You have successfully registered!"));

            return null;
        } catch (ConstraintViolationException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed", e.getMessage()));
            return "register";
        }
}


    private boolean passwordsMatch() {
        if (password == null || !password.equals(repeatPassword)) {
            FacesContext.getCurrentInstance().addMessage("registerForm:repeatPassword",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match", "Passwords do not match"));
            return false;
        }
        return true;
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

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
