/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author boone
 */
@Embeddable
public class BookingItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "booking_id")
    private int bookingId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "item_id")
    private int itemId;

    public BookingItemPK() {
    }

    public BookingItemPK(int bookingId, int itemId) {
        this.bookingId = bookingId;
        this.itemId = itemId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) bookingId;
        hash += (int) itemId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookingItemPK)) {
            return false;
        }
        BookingItemPK other = (BookingItemPK) object;
        if (this.bookingId != other.bookingId) {
            return false;
        }
        if (this.itemId != other.itemId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.BookingItemPK[ bookingId=" + bookingId + ", itemId=" + itemId + " ]";
    }
    
}
