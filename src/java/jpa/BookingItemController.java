package jpa;

import jpa.util.JsfUtil;
import jpa.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("bookingItemController")
@SessionScoped
public class BookingItemController implements Serializable {

    @EJB
    private jpa.BookingItemFacade ejbFacade;
    private List<BookingItem> items = null;
    private BookingItem selected;

    public BookingItemController() {
    }

    public BookingItem getSelected() {
        return selected;
    }

    public void setSelected(BookingItem selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getBookingItemPK().setBookingId(selected.getBooking().getBookingId());
        selected.getBookingItemPK().setItemId(selected.getItem().getItemId());
    }

    protected void initializeEmbeddableKey() {
        selected.setBookingItemPK(new jpa.BookingItemPK());
    }

    private BookingItemFacade getFacade() {
        return ejbFacade;
    }

    public BookingItem prepareCreate() {
        selected = new BookingItem();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("BookingItemCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("BookingItemUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("BookingItemDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<BookingItem> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public BookingItem getBookingItem(jpa.BookingItemPK id) {
        return getFacade().find(id);
    }

    public List<BookingItem> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<BookingItem> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = BookingItem.class)
    public static class BookingItemControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BookingItemController controller = (BookingItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bookingItemController");
            return controller.getBookingItem(getKey(value));
        }

        jpa.BookingItemPK getKey(String value) {
            jpa.BookingItemPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new jpa.BookingItemPK();
            key.setBookingId(Integer.parseInt(values[0]));
            key.setItemId(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(jpa.BookingItemPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getBookingId());
            sb.append(SEPARATOR);
            sb.append(value.getItemId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof BookingItem) {
                BookingItem o = (BookingItem) object;
                return getStringKey(o.getBookingItemPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), BookingItem.class.getName()});
                return null;
            }
        }

    }

}
