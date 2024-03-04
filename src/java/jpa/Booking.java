/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author boone
 */
@Entity
@Table(name = "Booking")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Booking.findAll", query = "SELECT b FROM Booking b"),
    @NamedQuery(name = "Booking.findNextId", query = "SELECT MAX(b.bookingId) FROM Booking b"),
    @NamedQuery(name = "Booking.findByBookingId", query = "SELECT b FROM Booking b WHERE b.bookingId = :bookingId"),
    @NamedQuery(name = "Booking.findByArrivalDate", query = "SELECT b FROM Booking b WHERE b.arrivalDate = :arrivalDate"),
    @NamedQuery(name = "Booking.findByReturnDate", query = "SELECT b FROM Booking b WHERE b.returnDate = :returnDate"),
    @NamedQuery(name = "Booking.findByPaymentDate", query = "SELECT b FROM Booking b WHERE b.paymentDate = :paymentDate"),
    @NamedQuery(name = "Booking.findByTotalPrice", query = "SELECT b FROM Booking b WHERE b.totalPrice = :totalPrice"),
    @NamedQuery(name = "Booking.findLastByUserId", query = "SELECT b FROM Booking b WHERE b.userId = :user ORDER BY b.bookingId DESC")
})
public class Booking implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    private Collection<BookingItem> bookingItemCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "booking_id")
    private Integer bookingId;
    @Column(name = "arrival_date")
    @Temporal(TemporalType.DATE)
    private Date arrivalDate;
    @Column(name = "return_date")
    @Temporal(TemporalType.DATE)
    private Date returnDate;
    @Column(name = "payment_date")
    @Temporal(TemporalType.DATE)
    private Date paymentDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @JoinTable(name = "BookingItem", joinColumns = {
        @JoinColumn(name = "booking_id", referencedColumnName = "booking_id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "item_id")})
    @ManyToMany
    private Collection<Item> itemCollection;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    // TODO: change name
    private User userId;
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    @ManyToOne
    // TODO: change name
    private Location locationId;

    public Booking() {
    }

    public Booking(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @XmlTransient
    public Collection<Item> getItemCollection() {
        return itemCollection;
    }

    public void setItemCollection(Collection<Item> itemCollection) {
        this.itemCollection = itemCollection;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Location getLocationId() {
        return locationId;
    }

    public void setLocationId(Location locationId) {
        this.locationId = locationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookingId != null ? bookingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Booking)) {
            return false;
        }
        Booking other = (Booking) object;
        if ((this.bookingId == null && other.bookingId != null) || (this.bookingId != null && !this.bookingId.equals(other.bookingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.Booking[ bookingId=" + bookingId + " ]";
    }

    @XmlTransient
    public Collection<BookingItem> getBookingItemCollection() {
        return bookingItemCollection;
    }

    public void setBookingItemCollection(Collection<BookingItem> bookingItemCollection) {
        this.bookingItemCollection = bookingItemCollection;
    }

}
