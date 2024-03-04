/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.service;

import bean.service.ShoppingCart;
import static java.lang.Math.log;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import jpa.Booking;
import jpa.BookingFacade;
import jpa.BookingItem;
import jpa.BookingItemFacade;
import jpa.BookingItemPK;
import jpa.BookingJpaController;
import jpa.Item;
import jpa.Location;
import jpa.User;

/**
 *
 * @author boone
 */
@Stateless
public class OrderService {

    @Inject
    private BookingFacade bookingFacade;
    @Inject
    private BookingItemFacade bookingItemFacade;
    @Inject
    private ShoppingCart shoppingCart;

    private Random random;

    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: OrderService is instantiated.");
    }

    private List<BookingItem> getBookingItems(Integer bookingId) {
        Map<Item, Integer> cartContents = shoppingCart.getCartContents();
        List<BookingItem> bookingItems = new ArrayList<>();
        System.out.println("DEBUG: cartContents as entry set: " + cartContents.entrySet().toString());
        cartContents.entrySet().stream()
                .map(entry -> new BookingItem(bookingId, entry.getKey().getItemId(), entry.getValue()))
                .forEachOrdered(bookingItem -> {
                    bookingItems.add(bookingItem);
                });
        return bookingItems;
    }

    public void create(User user, Location location, ShoppingCart shoppingCart) {
        System.out.println("Largest bookingId: " + bookingFacade.findNextId());
        Booking booking = new Booking(bookingFacade.findNextId() + 1);

        Date paymentDate = Date.from(Instant.now());
        booking.setPaymentDate(paymentDate);
        LocalDate paymentLocalDate = paymentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Dodaj 1-14 dana na datum plaćanja
        LocalDate arrivalLocalDate = paymentLocalDate.plusDays(ThreadLocalRandom.current().nextInt(1, 15));
        booking.setArrivalDate(Date.from(arrivalLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Dodaj 1-14 dana na datum dolaska
        LocalDate returnLocalDate = arrivalLocalDate.plusDays(ThreadLocalRandom.current().nextInt(1, 15));
        booking.setReturnDate(Date.from(returnLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        booking.setTotalPrice(new BigDecimal(shoppingCart.getGrandTotal()));
        booking.setUserId(user);
        booking.setLocationId(location);
        booking.setItemCollection(new ArrayList<>());
        booking.setBookingItemCollection(new ArrayList<>());

        System.out.println("DEBUG: Booking data");
        System.out.println("DEBUG: Booking ID: " + booking.getBookingId());
        System.out.println("DEBUG: Booking payment date: " + booking.getPaymentDate());
        System.out.println("DEBUG: Booking arrival date: " + booking.getArrivalDate());
        System.out.println("DEBUG: Booking return date: " + booking.getReturnDate());
        System.out.println("DEBUG: Booking total price: " + booking.getTotalPrice());
        System.out.println("DEBUG: Booking user: " + booking.getUserId());
        System.out.println("DEBUG: Booking location: " + booking.getLocationId());
        System.out.println("DEBUG: Booking itemCollection: " + booking.getItemCollection());
        System.out.println("DEBUG: Booking bookingItemCollection: " + booking.getBookingItemCollection());

        // TODO: ekstrahirati
        try {
            bookingFacade.create(booking);

            System.out.println("DEBUG: Setting bookingItemCollection... ");
            List<BookingItem> bookingItems = getBookingItems(booking.getBookingId());
            System.out.println("DEBUG: bookingItems: " + bookingItems);

            System.out.println("DEBUG: Persisting bookingItems contents...");
            bookingItems.forEach(item -> {
                bookingItemFacade.create(item);
            });

            booking.setBookingItemCollection(bookingItems);

            bookingFacade.edit(booking);

        } catch (ConstraintViolationException e) {
            Set<javax.validation.ConstraintViolation<?>> violations = e.getConstraintViolations();
            for (Iterator<javax.validation.ConstraintViolation<?>> it = violations.iterator(); it.hasNext();) {
                javax.validation.ConstraintViolation<?> violation = it.next();
                System.out.println("DEBUG: Violation:" + violation.getPropertyPath() + violation.getMessage() + violation.getInvalidValue());
            }
            throw e;
        }

    }

    public Booking getLastBookingByUser(User user) {
        return bookingFacade.findLastByUserId(user);
    }
}
