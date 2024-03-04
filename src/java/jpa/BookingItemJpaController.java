/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import jpa.exceptions.NonexistentEntityException;
import jpa.exceptions.PreexistingEntityException;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author boone
 */
public class BookingItemJpaController implements Serializable {

    public BookingItemJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(BookingItem bookingItem) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (bookingItem.getBookingItemPK() == null) {
            bookingItem.setBookingItemPK(new BookingItemPK());
        }
        bookingItem.getBookingItemPK().setBookingId(bookingItem.getBooking().getBookingId());
        bookingItem.getBookingItemPK().setItemId(bookingItem.getItem().getItemId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Booking booking = bookingItem.getBooking();
            if (booking != null) {
                booking = em.getReference(booking.getClass(), booking.getBookingId());
                bookingItem.setBooking(booking);
            }
            Item item = bookingItem.getItem();
            if (item != null) {
                item = em.getReference(item.getClass(), item.getItemId());
                bookingItem.setItem(item);
            }
            em.persist(bookingItem);
            if (booking != null) {
                booking.getBookingItemCollection().add(bookingItem);
                booking = em.merge(booking);
            }
            if (item != null) {
                item.getBookingItemCollection().add(bookingItem);
                item = em.merge(item);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findBookingItem(bookingItem.getBookingItemPK()) != null) {
                throw new PreexistingEntityException("BookingItem " + bookingItem + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(BookingItem bookingItem) throws NonexistentEntityException, RollbackFailureException, Exception {
        bookingItem.getBookingItemPK().setBookingId(bookingItem.getBooking().getBookingId());
        bookingItem.getBookingItemPK().setItemId(bookingItem.getItem().getItemId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            BookingItem persistentBookingItem = em.find(BookingItem.class, bookingItem.getBookingItemPK());
            Booking bookingOld = persistentBookingItem.getBooking();
            Booking bookingNew = bookingItem.getBooking();
            Item itemOld = persistentBookingItem.getItem();
            Item itemNew = bookingItem.getItem();
            if (bookingNew != null) {
                bookingNew = em.getReference(bookingNew.getClass(), bookingNew.getBookingId());
                bookingItem.setBooking(bookingNew);
            }
            if (itemNew != null) {
                itemNew = em.getReference(itemNew.getClass(), itemNew.getItemId());
                bookingItem.setItem(itemNew);
            }
            bookingItem = em.merge(bookingItem);
            if (bookingOld != null && !bookingOld.equals(bookingNew)) {
                bookingOld.getBookingItemCollection().remove(bookingItem);
                bookingOld = em.merge(bookingOld);
            }
            if (bookingNew != null && !bookingNew.equals(bookingOld)) {
                bookingNew.getBookingItemCollection().add(bookingItem);
                bookingNew = em.merge(bookingNew);
            }
            if (itemOld != null && !itemOld.equals(itemNew)) {
                itemOld.getBookingItemCollection().remove(bookingItem);
                itemOld = em.merge(itemOld);
            }
            if (itemNew != null && !itemNew.equals(itemOld)) {
                itemNew.getBookingItemCollection().add(bookingItem);
                itemNew = em.merge(itemNew);
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
                BookingItemPK id = bookingItem.getBookingItemPK();
                if (findBookingItem(id) == null) {
                    throw new NonexistentEntityException("The bookingItem with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BookingItemPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            BookingItem bookingItem;
            try {
                bookingItem = em.getReference(BookingItem.class, id);
                bookingItem.getBookingItemPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bookingItem with id " + id + " no longer exists.", enfe);
            }
            Booking booking = bookingItem.getBooking();
            if (booking != null) {
                booking.getBookingItemCollection().remove(bookingItem);
                booking = em.merge(booking);
            }
            Item item = bookingItem.getItem();
            if (item != null) {
                item.getBookingItemCollection().remove(bookingItem);
                item = em.merge(item);
            }
            em.remove(bookingItem);
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

    public List<BookingItem> findBookingItemEntities() {
        return findBookingItemEntities(true, -1, -1);
    }

    public List<BookingItem> findBookingItemEntities(int maxResults, int firstResult) {
        return findBookingItemEntities(false, maxResults, firstResult);
    }

    private List<BookingItem> findBookingItemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(BookingItem.class));
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

    public BookingItem findBookingItem(BookingItemPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(BookingItem.class, id);
        } finally {
            em.close();
        }
    }

    public int getBookingItemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<BookingItem> rt = cq.from(BookingItem.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
