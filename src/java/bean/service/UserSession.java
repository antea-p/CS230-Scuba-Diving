package bean.service;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import jpa.User;

@Named
@SessionScoped
public class UserSession implements Serializable {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
