/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.controller;

import bean.service.OrderService;
import bean.service.ShoppingCart;
import bean.service.UserSession;
import bean.service.LocationService;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import jpa.Booking;
import jpa.BookingItem;
import jpa.Location;
import jpa.User;

/**
 *
 * @author boone
 */
@Named
@SessionScoped
public class CheckoutPageController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ShoppingCart shoppingCart;
    @Inject
    private LocationService locationService;
    @Inject
    private OrderService orderService;
    @Inject
    private UserSession userSession;

    private String firstName;
    private String surname;
    private String streetAddress;
    private String city;
    private String zipcode;
    private String country;
    private String email;
    private String phone;
    private int travelDestinationId = 1;
    private String notes;
    private Booking booking;

    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: CheckoutPageController is instantiated.");
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTravelDestinationId() {
        return travelDestinationId;
    }

    public void setTravelDestinationId(int travelDestinationId) {
        this.travelDestinationId = travelDestinationId;
        System.out.println("DEBUG setTravelDestinationId value: " + this.travelDestinationId);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getCountries() {
        String[] countryCodes = Locale.getISOCountries();
        List<String> countryNames = new ArrayList<>();

        for (String countryCode : countryCodes) {

            Locale obj = new Locale("", countryCode);
            countryNames.add(obj.getDisplayCountry());

        }
        countryNames.sort(Comparator.naturalOrder());
        return countryNames;
    }

    public double getSubtotal() {
        return shoppingCart.getSubtotal();
    }

    public double getShippingCosts() {
        return shoppingCart.getShippingCosts();
    }

    public double getGrandTotal() {
        return shoppingCart.getGrandTotal();
    }

    public Location getSelectedLocation() {
        return locationService.getLocationById(travelDestinationId);
    }

    public Location[] getTravelDestinations() {
        System.out.println("DEBUG getTravelDestinations: Getting travel destinations");
        Location[] allDestinations = locationService.getAll();
        System.out.println("DEBUG getTravelDestinations: " + Arrays.toString(allDestinations));
        return locationService.getAll();
    }

    private String formatDate(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy, HH:mm");
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .format(formatter);
    }

    public Booking getBooking() {
        return booking;
    }

    public String getArrivalDate() {
        return formatDate(booking.getArrivalDate());
    }

    public String getReturnDate() {
        return formatDate(booking.getReturnDate());
    }

    public String getPaymentDate() {
        return formatDate(booking.getPaymentDate());
    }

    public String getTotalPrice() {
        return String.format("%.2f", booking.getTotalPrice());
    }

    public String getLocationName() {
        return booking.getLocationId().getName();
    }

    public List<BookingItem> getBookingItems() {
        return new ArrayList<>(booking.getBookingItemCollection());
    }

    public String submit() {
        User user = userSession.getUser();

        if (user == null) {
            System.out.println("DEBUG: User has not logged in");
            FacesMessage message = new FacesMessage("You must be logged in to complete the checkout process.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "login?faces-redirect=true";
        }
        System.out.println("DEBUG submit: Creating order");

        Location travelDestination = locationService.getLocationById(travelDestinationId);
        System.out.println("DEBUG submit: travelDestination: " + travelDestination);
        orderService.create(user, travelDestination, shoppingCart);

        booking = orderService.getLastBookingByUser(user);
        // Više nije potreban sadržaj košarice
        System.out.println("DEBUG submit: Clearing shopping cart");
        shoppingCart.clear();

        return "thankyou?faces-redirect=true";
    }
}
