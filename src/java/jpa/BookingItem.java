/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author boone
 */
@Entity
@Table(name = "BookingItem")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BookingItem.findAll", query = "SELECT b FROM BookingItem b"),
    @NamedQuery(name = "BookingItem.findByBookingId", query = "SELECT b FROM BookingItem b WHERE b.bookingItemPK.bookingId = :bookingId"),
    @NamedQuery(name = "BookingItem.findByItemId", query = "SELECT b FROM BookingItem b WHERE b.bookingItemPK.itemId = :itemId"),
    @NamedQuery(name = "BookingItem.findByQuantity", query = "SELECT b FROM BookingItem b WHERE b.quantity = :quantity")})
public class BookingItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected BookingItemPK bookingItemPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    @JoinColumn(name = "booking_id", referencedColumnName = "booking_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Booking booking;
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Item item;

    public BookingItem() {
    }

    public BookingItem(BookingItemPK bookingItemPK) {
        this.bookingItemPK = bookingItemPK;
    }

    public BookingItem(BookingItemPK bookingItemPK, int quantity) {
        this.bookingItemPK = bookingItemPK;
        this.quantity = quantity;
    }

    public BookingItem(int bookingId, int itemId) {
        this.bookingItemPK = new BookingItemPK(bookingId, itemId);
    }
    
    public BookingItem(int bookingId, int itemId, int quantity) {
        this.bookingItemPK = new BookingItemPK(bookingId, itemId);
        this.quantity = quantity;
    }

    public BookingItemPK getBookingItemPK() {
        return bookingItemPK;
    }

    public void setBookingItemPK(BookingItemPK bookingItemPK) {
        this.bookingItemPK = bookingItemPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookingItemPK != null ? bookingItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookingItem)) {
            return false;
        }
        BookingItem other = (BookingItem) object;
        if ((this.bookingItemPK == null && other.bookingItemPK != null) || (this.bookingItemPK != null && !this.bookingItemPK.equals(other.bookingItemPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.BookingItem[ bookingItemPK=" + bookingItemPK + " ]";
    }
    
}
