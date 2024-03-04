package bean.controller;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class LogoutController {
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("currentUser");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login?faces-redirect=true";
    }
}
