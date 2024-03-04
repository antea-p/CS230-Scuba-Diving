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
public class ItemJpaController implements Serializable {

    public ItemJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Item item) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (item.getBookingCollection() == null) {
            item.setBookingCollection(new ArrayList<Booking>());
        }
        if (item.getBookingItemCollection() == null) {
            item.setBookingItemCollection(new ArrayList<BookingItem>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Category categoryId = item.getCategoryId();
            if (categoryId != null) {
                categoryId = em.getReference(categoryId.getClass(), categoryId.getCategoryId());
                item.setCategoryId(categoryId);
            }
            Collection<Booking> attachedBookingCollection = new ArrayList<Booking>();
            for (Booking bookingCollectionBookingToAttach : item.getBookingCollection()) {
                bookingCollectionBookingToAttach = em.getReference(bookingCollectionBookingToAttach.getClass(), bookingCollectionBookingToAttach.getBookingId());
                attachedBookingCollection.add(bookingCollectionBookingToAttach);
            }
            item.setBookingCollection(attachedBookingCollection);
            Collection<BookingItem> attachedBookingItemCollection = new ArrayList<BookingItem>();
            for (BookingItem bookingItemCollectionBookingItemToAttach : item.getBookingItemCollection()) {
                bookingItemCollectionBookingItemToAttach = em.getReference(bookingItemCollectionBookingItemToAttach.getClass(), bookingItemCollectionBookingItemToAttach.getBookingItemPK());
                attachedBookingItemCollection.add(bookingItemCollectionBookingItemToAttach);
            }
            item.setBookingItemCollection(attachedBookingItemCollection);
            em.persist(item);
            if (categoryId != null) {
                categoryId.getItemCollection().add(item);
                categoryId = em.merge(categoryId);
            }
            for (Booking bookingCollectionBooking : item.getBookingCollection()) {
                bookingCollectionBooking.getItemCollection().add(item);
                bookingCollectionBooking = em.merge(bookingCollectionBooking);
            }
            for (BookingItem bookingItemCollectionBookingItem : item.getBookingItemCollection()) {
                Item oldItemOfBookingItemCollectionBookingItem = bookingItemCollectionBookingItem.getItem();
                bookingItemCollectionBookingItem.setItem(item);
                bookingItemCollectionBookingItem = em.merge(bookingItemCollectionBookingItem);
                if (oldItemOfBookingItemCollectionBookingItem != null) {
                    oldItemOfBookingItemCollectionBookingItem.getBookingItemCollection().remove(bookingItemCollectionBookingItem);
                    oldItemOfBookingItemCollectionBookingItem = em.merge(oldItemOfBookingItemCollectionBookingItem);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findItem(item.getItemId()) != null) {
                throw new PreexistingEntityException("Item " + item + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Item item) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Item persistentItem = em.find(Item.class, item.getItemId());
            Category categoryIdOld = persistentItem.getCategoryId();
            Category categoryIdNew = item.getCategoryId();
            Collection<Booking> bookingCollectionOld = persistentItem.getBookingCollection();
            Collection<Booking> bookingCollectionNew = item.getBookingCollection();
            Collection<BookingItem> bookingItemCollectionOld = persistentItem.getBookingItemCollection();
            Collection<BookingItem> bookingItemCollectionNew = item.getBookingItemCollection();
            List<String> illegalOrphanMessages = null;
            for (BookingItem bookingItemCollectionOldBookingItem : bookingItemCollectionOld) {
                if (!bookingItemCollectionNew.contains(bookingItemCollectionOldBookingItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain BookingItem " + bookingItemCollectionOldBookingItem + " since its item field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (categoryIdNew != null) {
                categoryIdNew = em.getReference(categoryIdNew.getClass(), categoryIdNew.getCategoryId());
                item.setCategoryId(categoryIdNew);
            }
            Collection<Booking> attachedBookingCollectionNew = new ArrayList<Booking>();
            for (Booking bookingCollectionNewBookingToAttach : bookingCollectionNew) {
                bookingCollectionNewBookingToAttach = em.getReference(bookingCollectionNewBookingToAttach.getClass(), bookingCollectionNewBookingToAttach.getBookingId());
                attachedBookingCollectionNew.add(bookingCollectionNewBookingToAttach);
            }
            bookingCollectionNew = attachedBookingCollectionNew;
            item.setBookingCollection(bookingCollectionNew);
            Collection<BookingItem> attachedBookingItemCollectionNew = new ArrayList<BookingItem>();
            for (BookingItem bookingItemCollectionNewBookingItemToAttach : bookingItemCollectionNew) {
                bookingItemCollectionNewBookingItemToAttach = em.getReference(bookingItemCollectionNewBookingItemToAttach.getClass(), bookingItemCollectionNewBookingItemToAttach.getBookingItemPK());
                attachedBookingItemCollectionNew.add(bookingItemCollectionNewBookingItemToAttach);
            }
            bookingItemCollectionNew = attachedBookingItemCollectionNew;
            item.setBookingItemCollection(bookingItemCollectionNew);
            item = em.merge(item);
            if (categoryIdOld != null && !categoryIdOld.equals(categoryIdNew)) {
                categoryIdOld.getItemCollection().remove(item);
                categoryIdOld = em.merge(categoryIdOld);
            }
            if (categoryIdNew != null && !categoryIdNew.equals(categoryIdOld)) {
                categoryIdNew.getItemCollection().add(item);
                categoryIdNew = em.merge(categoryIdNew);
            }
            for (Booking bookingCollectionOldBooking : bookingCollectionOld) {
                if (!bookingCollectionNew.contains(bookingCollectionOldBooking)) {
                    bookingCollectionOldBooking.getItemCollection().remove(item);
                    bookingCollectionOldBooking = em.merge(bookingCollectionOldBooking);
                }
            }
            for (Booking bookingCollectionNewBooking : bookingCollectionNew) {
                if (!bookingCollectionOld.contains(bookingCollectionNewBooking)) {
                    bookingCollectionNewBooking.getItemCollection().add(item);
                    bookingCollectionNewBooking = em.merge(bookingCollectionNewBooking);
                }
            }
            for (BookingItem bookingItemCollectionNewBookingItem : bookingItemCollectionNew) {
                if (!bookingItemCollectionOld.contains(bookingItemCollectionNewBookingItem)) {
                    Item oldItemOfBookingItemCollectionNewBookingItem = bookingItemCollectionNewBookingItem.getItem();
                    bookingItemCollectionNewBookingItem.setItem(item);
                    bookingItemCollectionNewBookingItem = em.merge(bookingItemCollectionNewBookingItem);
                    if (oldItemOfBookingItemCollectionNewBookingItem != null && !oldItemOfBookingItemCollectionNewBookingItem.equals(item)) {
                        oldItemOfBookingItemCollectionNewBookingItem.getBookingItemCollection().remove(bookingItemCollectionNewBookingItem);
                        oldItemOfBookingItemCollectionNewBookingItem = em.merge(oldItemOfBookingItemCollectionNewBookingItem);
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
                Integer id = item.getItemId();
                if (findItem(id) == null) {
                    throw new NonexistentEntityException("The item with id " + id + " no longer exists.");
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
            Item item;
            try {
                item = em.getReference(Item.class, id);
                item.getItemId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The item with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<BookingItem> bookingItemCollectionOrphanCheck = item.getBookingItemCollection();
            for (BookingItem bookingItemCollectionOrphanCheckBookingItem : bookingItemCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Item (" + item + ") cannot be destroyed since the BookingItem " + bookingItemCollectionOrphanCheckBookingItem + " in its bookingItemCollection field has a non-nullable item field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Category categoryId = item.getCategoryId();
            if (categoryId != null) {
                categoryId.getItemCollection().remove(item);
                categoryId = em.merge(categoryId);
            }
            Collection<Booking> bookingCollection = item.getBookingCollection();
            for (Booking bookingCollectionBooking : bookingCollection) {
                bookingCollectionBooking.getItemCollection().remove(item);
                bookingCollectionBooking = em.merge(bookingCollectionBooking);
            }
            em.remove(item);
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

    public List<Item> findItemEntities() {
        return findItemEntities(true, -1, -1);
    }

    public List<Item> findItemEntities(int maxResults, int firstResult) {
        return findItemEntities(false, maxResults, firstResult);
    }

    private List<Item> findItemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Item.class));
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

    public Item findItem(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Item.class, id);
        } finally {
            em.close();
        }
    }

    public int getItemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Item> rt = cq.from(Item.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
