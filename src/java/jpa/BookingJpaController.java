/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import jpa.exceptions.IllegalOrphanException;
import jpa.exceptions.NonexistentEntityException;
import jpa.exceptions.PreexistingEntityException;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author boone
 */
public class BookingJpaController implements Serializable {

    public BookingJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Booking booking) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (booking.getItemCollection() == null) {
            booking.setItemCollection(new ArrayList<Item>());
        }
        if (booking.getBookingItemCollection() == null) {
            booking.setBookingItemCollection(new ArrayList<BookingItem>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User userId = booking.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getUserId());
                booking.setUserId(userId);
            }
            Location locationId = booking.getLocationId();
            if (locationId != null) {
                locationId = em.getReference(locationId.getClass(), locationId.getLocationId());
                booking.setLocationId(locationId);
            }
            Collection<Item> attachedItemCollection = new ArrayList<Item>();
            for (Item itemCollectionItemToAttach : booking.getItemCollection()) {
                itemCollectionItemToAttach = em.getReference(itemCollectionItemToAttach.getClass(), itemCollectionItemToAttach.getItemId());
                attachedItemCollection.add(itemCollectionItemToAttach);
            }
            booking.setItemCollection(attachedItemCollection);
            Collection<BookingItem> attachedBookingItemCollection = new ArrayList<BookingItem>();
            for (BookingItem bookingItemCollectionBookingItemToAttach : booking.getBookingItemCollection()) {
                bookingItemCollectionBookingItemToAttach = em.getReference(bookingItemCollectionBookingItemToAttach.getClass(), bookingItemCollectionBookingItemToAttach.getBookingItemPK());
                attachedBookingItemCollection.add(bookingItemCollectionBookingItemToAttach);
            }
            booking.setBookingItemCollection(attachedBookingItemCollection);
            em.persist(booking);
            if (userId != null) {
                userId.getBookingCollection().add(booking);
                userId = em.merge(userId);
            }
            if (locationId != null) {
                locationId.getBookingCollection().add(booking);
                locationId = em.merge(locationId);
            }
            for (Item itemCollectionItem : booking.getItemCollection()) {
                itemCollectionItem.getBookingCollection().add(booking);
                itemCollectionItem = em.merge(itemCollectionItem);
            }
            for (BookingItem bookingItemCollectionBookingItem : booking.getBookingItemCollection()) {
                Booking oldBookingOfBookingItemCollectionBookingItem = bookingItemCollectionBookingItem.getBooking();
                bookingItemCollectionBookingItem.setBooking(booking);
                bookingItemCollectionBookingItem = em.merge(bookingItemCollectionBookingItem);
                if (oldBookingOfBookingItemCollectionBookingItem != null) {
                    oldBookingOfBookingItemCollectionBookingItem.getBookingItemCollection().remove(bookingItemCollectionBookingItem);
                    oldBookingOfBookingItemCollectionBookingItem = em.merge(oldBookingOfBookingItemCollectionBookingItem);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findBooking(booking.getBookingId()) != null) {
                throw new PreexistingEntityException("Booking " + booking + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Booking booking) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Booking persistentBooking = em.find(Booking.class, booking.getBookingId());
            User userIdOld = persistentBooking.getUserId();
            User userIdNew = booking.getUserId();
            Location locationIdOld = persistentBooking.getLocationId();
            Location locationIdNew = booking.getLocationId();
            Collection<Item> itemCollectionOld = persistentBooking.getItemCollection();
            Collection<Item> itemCollectionNew = booking.getItemCollection();
            Collection<BookingItem> bookingItemCollectionOld = persistentBooking.getBookingItemCollection();
            Collection<BookingItem> bookingItemCollectionNew = booking.getBookingItemCollection();
            List<String> illegalOrphanMessages = null;
            for (BookingItem bookingItemCollectionOldBookingItem : bookingItemCollectionOld) {
                if (!bookingItemCollectionNew.contains(bookingItemCollectionOldBookingItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain BookingItem " + bookingItemCollectionOldBookingItem + " since its booking field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getUserId());
                booking.setUserId(userIdNew);
            }
            if (locationIdNew != null) {
                locationIdNew = em.getReference(locationIdNew.getClass(), locationIdNew.getLocationId());
                booking.setLocationId(locationIdNew);
            }
            Collection<Item> attachedItemCollectionNew = new ArrayList<Item>();
            for (Item itemCollectionNewItemToAttach : itemCollectionNew) {
                itemCollectionNewItemToAttach = em.getReference(itemCollectionNewItemToAttach.getClass(), itemCollectionNewItemToAttach.getItemId());
                attachedItemCollectionNew.add(itemCollectionNewItemToAttach);
            }
            itemCollectionNew = attachedItemCollectionNew;
            booking.setItemCollection(itemCollectionNew);
            Collection<BookingItem> attachedBookingItemCollectionNew = new ArrayList<BookingItem>();
            for (BookingItem bookingItemCollectionNewBookingItemToAttach : bookingItemCollectionNew) {
                bookingItemCollectionNewBookingItemToAttach = em.getReference(bookingItemCollectionNewBookingItemToAttach.getClass(), bookingItemCollectionNewBookingItemToAttach.getBookingItemPK());
                attachedBookingItemCollectionNew.add(bookingItemCollectionNewBookingItemToAttach);
            }
            bookingItemCollectionNew = attachedBookingItemCollectionNew;
            booking.setBookingItemCollection(bookingItemCollectionNew);
            booking = em.merge(booking);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getBookingCollection().remove(booking);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getBookingCollection().add(booking);
                userIdNew = em.merge(userIdNew);
            }
            if (locationIdOld != null && !locationIdOld.equals(locationIdNew)) {
                locationIdOld.getBookingCollection().remove(booking);
                locationIdOld = em.merge(locationIdOld);
            }
            if (locationIdNew != null && !locationIdNew.equals(locationIdOld)) {
                locationIdNew.getBookingCollection().add(booking);
                locationIdNew = em.merge(locationIdNew);
            }
            for (Item itemCollectionOldItem : itemCollectionOld) {
                if (!itemCollectionNew.contains(itemCollectionOldItem)) {
                    itemCollectionOldItem.getBookingCollection().remove(booking);
                    itemCollectionOldItem = em.merge(itemCollectionOldItem);
                }
            }
            for (Item itemCollectionNewItem : itemCollectionNew) {
                if (!itemCollectionOld.contains(itemCollectionNewItem)) {
                    itemCollectionNewItem.getBookingCollection().add(booking);
                    itemCollectionNewItem = em.merge(itemCollectionNewItem);
                }
            }
            for (BookingItem bookingItemCollectionNewBookingItem : bookingItemCollectionNew) {
                if (!bookingItemCollectionOld.contains(bookingItemCollectionNewBookingItem)) {
                    Booking oldBookingOfBookingItemCollectionNewBookingItem = bookingItemCollectionNewBookingItem.getBooking();
                    bookingItemCollectionNewBookingItem.setBooking(booking);
                    bookingItemCollectionNewBookingItem = em.merge(bookingItemCollectionNewBookingItem);
                    if (oldBookingOfBookingItemCollectionNewBookingItem != null && !oldBookingOfBookingItemCollectionNewBookingItem.equals(booking)) {
                        oldBookingOfBookingItemCollectionNewBookingItem.getBookingItemCollection().remove(bookingItemCollectionNewBookingItem);
                        oldBookingOfBookingItemCollectionNewBookingItem = em.merge(oldBookingOfBookingItemCollectionNewBookingItem);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = booking.getBookingId();
                if (findBooking(id) == null) {
                    throw new NonexistentEntityException("The booking with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Booking booking;
            try {
                booking = em.getReference(Booking.class, id);
                booking.getBookingId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The booking with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<BookingItem> bookingItemCollectionOrphanCheck = booking.getBookingItemCollection();
            for (BookingItem bookingItemCollectionOrphanCheckBookingItem : bookingItemCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Booking (" + booking + ") cannot be destroyed since the BookingItem " + bookingItemCollectionOrphanCheckBookingItem + " in its bookingItemCollection field has a non-nullable booking field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            User userId = booking.getUserId();
            if (userId != null) {
                userId.getBookingCollection().remove(booking);
                userId = em.merge(userId);
            }
            Location locationId = booking.getLocationId();
            if (locationId != null) {
                locationId.getBookingCollection().remove(booking);
                locationId = em.merge(locationId);
            }
            Collection<Item> itemCollection = booking.getItemCollection();
            for (Item itemCollectionItem : itemCollection) {
                itemCollectionItem.getBookingCollection().remove(booking);
                itemCollectionItem = em.merge(itemCollectionItem);
            }
            em.remove(booking);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Booking> findBookingEntities() {
        return findBookingEntities(true, -1, -1);
    }

    public List<Booking> findBookingEntities(int maxResults, int firstResult) {
        return findBookingEntities(false, maxResults, firstResult);
    }

    private List<Booking> findBookingEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Booking.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Booking findBooking(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Booking.class, id);
        } finally {
            em.close();
        }
    }

    public int getBookingCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Booking> rt = cq.from(Booking.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
