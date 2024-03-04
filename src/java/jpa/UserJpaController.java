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
public class UserJpaController implements Serializable {

    public UserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (user.getBookingCollection() == null) {
            user.setBookingCollection(new ArrayList<Booking>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Booking> attachedBookingCollection = new ArrayList<Booking>();
            for (Booking bookingCollectionBookingToAttach : user.getBookingCollection()) {
                bookingCollectionBookingToAttach = em.getReference(bookingCollectionBookingToAttach.getClass(), bookingCollectionBookingToAttach.getBookingId());
                attachedBookingCollection.add(bookingCollectionBookingToAttach);
            }
            user.setBookingCollection(attachedBookingCollection);
            em.persist(user);
            for (Booking bookingCollectionBooking : user.getBookingCollection()) {
                User oldUserIdOfBookingCollectionBooking = bookingCollectionBooking.getUserId();
                bookingCollectionBooking.setUserId(user);
                bookingCollectionBooking = em.merge(bookingCollectionBooking);
                if (oldUserIdOfBookingCollectionBooking != null) {
                    oldUserIdOfBookingCollectionBooking.getBookingCollection().remove(bookingCollectionBooking);
                    oldUserIdOfBookingCollectionBooking = em.merge(oldUserIdOfBookingCollectionBooking);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUser(user.getUserId()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User persistentUser = em.find(User.class, user.getUserId());
            Collection<Booking> bookingCollectionOld = persistentUser.getBookingCollection();
            Collection<Booking> bookingCollectionNew = user.getBookingCollection();
            Collection<Booking> attachedBookingCollectionNew = new ArrayList<Booking>();
            for (Booking bookingCollectionNewBookingToAttach : bookingCollectionNew) {
                bookingCollectionNewBookingToAttach = em.getReference(bookingCollectionNewBookingToAttach.getClass(), bookingCollectionNewBookingToAttach.getBookingId());
                attachedBookingCollectionNew.add(bookingCollectionNewBookingToAttach);
            }
            bookingCollectionNew = attachedBookingCollectionNew;
            user.setBookingCollection(bookingCollectionNew);
            user = em.merge(user);
            for (Booking bookingCollectionOldBooking : bookingCollectionOld) {
                if (!bookingCollectionNew.contains(bookingCollectionOldBooking)) {
                    bookingCollectionOldBooking.setUserId(null);
                    bookingCollectionOldBooking = em.merge(bookingCollectionOldBooking);
                }
            }
            for (Booking bookingCollectionNewBooking : bookingCollectionNew) {
                if (!bookingCollectionOld.contains(bookingCollectionNewBooking)) {
                    User oldUserIdOfBookingCollectionNewBooking = bookingCollectionNewBooking.getUserId();
                    bookingCollectionNewBooking.setUserId(user);
                    bookingCollectionNewBooking = em.merge(bookingCollectionNewBooking);
                    if (oldUserIdOfBookingCollectionNewBooking != null && !oldUserIdOfBookingCollectionNewBooking.equals(user)) {
                        oldUserIdOfBookingCollectionNewBooking.getBookingCollection().remove(bookingCollectionNewBooking);
                        oldUserIdOfBookingCollectionNewBooking = em.merge(oldUserIdOfBookingCollectionNewBooking);
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
                Integer id = user.getUserId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
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
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getUserId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Collection<Booking> bookingCollection = user.getBookingCollection();
            for (Booking bookingCollectionBooking : bookingCollection) {
                bookingCollectionBooking.setUserId(null);
                bookingCollectionBooking = em.merge(bookingCollectionBooking);
            }
            em.remove(user);
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

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
