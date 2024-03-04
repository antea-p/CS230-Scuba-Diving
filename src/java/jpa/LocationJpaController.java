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
import jpa.exceptions.NonexistentEntityException;
import jpa.exceptions.PreexistingEntityException;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author boone
 */
public class LocationJpaController implements Serializable {

    public LocationJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Location location) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (location.getBookingCollection() == null) {
            location.setBookingCollection(new ArrayList<Booking>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Booking> attachedBookingCollection = new ArrayList<Booking>();
            for (Booking bookingCollectionBookingToAttach : location.getBookingCollection()) {
                bookingCollectionBookingToAttach = em.getReference(bookingCollectionBookingToAttach.getClass(), bookingCollectionBookingToAttach.getBookingId());
                attachedBookingCollection.add(bookingCollectionBookingToAttach);
            }
            location.setBookingCollection(attachedBookingCollection);
            em.persist(location);
            for (Booking bookingCollectionBooking : location.getBookingCollection()) {
                Location oldLocationIdOfBookingCollectionBooking = bookingCollectionBooking.getLocationId();
                bookingCollectionBooking.setLocationId(location);
                bookingCollectionBooking = em.merge(bookingCollectionBooking);
                if (oldLocationIdOfBookingCollectionBooking != null) {
                    oldLocationIdOfBookingCollectionBooking.getBookingCollection().remove(bookingCollectionBooking);
                    oldLocationIdOfBookingCollectionBooking = em.merge(oldLocationIdOfBookingCollectionBooking);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findLocation(location.getLocationId()) != null) {
                throw new PreexistingEntityException("Location " + location + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Location location) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Location persistentLocation = em.find(Location.class, location.getLocationId());
            Collection<Booking> bookingCollectionOld = persistentLocation.getBookingCollection();
            Collection<Booking> bookingCollectionNew = location.getBookingCollection();
            Collection<Booking> attachedBookingCollectionNew = new ArrayList<Booking>();
            for (Booking bookingCollectionNewBookingToAttach : bookingCollectionNew) {
                bookingCollectionNewBookingToAttach = em.getReference(bookingCollectionNewBookingToAttach.getClass(), bookingCollectionNewBookingToAttach.getBookingId());
                attachedBookingCollectionNew.add(bookingCollectionNewBookingToAttach);
            }
            bookingCollectionNew = attachedBookingCollectionNew;
            location.setBookingCollection(bookingCollectionNew);
            location = em.merge(location);
            for (Booking bookingCollectionOldBooking : bookingCollectionOld) {
                if (!bookingCollectionNew.contains(bookingCollectionOldBooking)) {
                    bookingCollectionOldBooking.setLocationId(null);
                    bookingCollectionOldBooking = em.merge(bookingCollectionOldBooking);
                }
            }
            for (Booking bookingCollectionNewBooking : bookingCollectionNew) {
                if (!bookingCollectionOld.contains(bookingCollectionNewBooking)) {
                    Location oldLocationIdOfBookingCollectionNewBooking = bookingCollectionNewBooking.getLocationId();
                    bookingCollectionNewBooking.setLocationId(location);
                    bookingCollectionNewBooking = em.merge(bookingCollectionNewBooking);
                    if (oldLocationIdOfBookingCollectionNewBooking != null && !oldLocationIdOfBookingCollectionNewBooking.equals(location)) {
                        oldLocationIdOfBookingCollectionNewBooking.getBookingCollection().remove(bookingCollectionNewBooking);
                        oldLocationIdOfBookingCollectionNewBooking = em.merge(oldLocationIdOfBookingCollectionNewBooking);
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
                Integer id = location.getLocationId();
                if (findLocation(id) == null) {
                    throw new NonexistentEntityException("The location with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Location location;
            try {
                location = em.getReference(Location.class, id);
                location.getLocationId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The location with id " + id + " no longer exists.", enfe);
            }
            Collection<Booking> bookingCollection = location.getBookingCollection();
            for (Booking bookingCollectionBooking : bookingCollection) {
                bookingCollectionBooking.setLocationId(null);
                bookingCollectionBooking = em.merge(bookingCollectionBooking);
            }
            em.remove(location);
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

    public List<Location> findLocationEntities() {
        return findLocationEntities(true, -1, -1);
    }

    public List<Location> findLocationEntities(int maxResults, int firstResult) {
        return findLocationEntities(false, maxResults, firstResult);
    }

    private List<Location> findLocationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Location.class));
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

    public Location findLocation(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Location.class, id);
        } finally {
            em.close();
        }
    }

    public int getLocationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Location> rt = cq.from(Location.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
