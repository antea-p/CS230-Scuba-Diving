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
public class CategoryJpaController implements Serializable {

    public CategoryJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Category category) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (category.getItemCollection() == null) {
            category.setItemCollection(new ArrayList<Item>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Item> attachedItemCollection = new ArrayList<Item>();
            for (Item itemCollectionItemToAttach : category.getItemCollection()) {
                itemCollectionItemToAttach = em.getReference(itemCollectionItemToAttach.getClass(), itemCollectionItemToAttach.getItemId());
                attachedItemCollection.add(itemCollectionItemToAttach);
            }
            category.setItemCollection(attachedItemCollection);
            em.persist(category);
            for (Item itemCollectionItem : category.getItemCollection()) {
                Category oldCategoryIdOfItemCollectionItem = itemCollectionItem.getCategoryId();
                itemCollectionItem.setCategoryId(category);
                itemCollectionItem = em.merge(itemCollectionItem);
                if (oldCategoryIdOfItemCollectionItem != null) {
                    oldCategoryIdOfItemCollectionItem.getItemCollection().remove(itemCollectionItem);
                    oldCategoryIdOfItemCollectionItem = em.merge(oldCategoryIdOfItemCollectionItem);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCategory(category.getCategoryId()) != null) {
                throw new PreexistingEntityException("Category " + category + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Category category) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Category persistentCategory = em.find(Category.class, category.getCategoryId());
            Collection<Item> itemCollectionOld = persistentCategory.getItemCollection();
            Collection<Item> itemCollectionNew = category.getItemCollection();
            Collection<Item> attachedItemCollectionNew = new ArrayList<Item>();
            for (Item itemCollectionNewItemToAttach : itemCollectionNew) {
                itemCollectionNewItemToAttach = em.getReference(itemCollectionNewItemToAttach.getClass(), itemCollectionNewItemToAttach.getItemId());
                attachedItemCollectionNew.add(itemCollectionNewItemToAttach);
            }
            itemCollectionNew = attachedItemCollectionNew;
            category.setItemCollection(itemCollectionNew);
            category = em.merge(category);
            for (Item itemCollectionOldItem : itemCollectionOld) {
                if (!itemCollectionNew.contains(itemCollectionOldItem)) {
                    itemCollectionOldItem.setCategoryId(null);
                    itemCollectionOldItem = em.merge(itemCollectionOldItem);
                }
            }
            for (Item itemCollectionNewItem : itemCollectionNew) {
                if (!itemCollectionOld.contains(itemCollectionNewItem)) {
                    Category oldCategoryIdOfItemCollectionNewItem = itemCollectionNewItem.getCategoryId();
                    itemCollectionNewItem.setCategoryId(category);
                    itemCollectionNewItem = em.merge(itemCollectionNewItem);
                    if (oldCategoryIdOfItemCollectionNewItem != null && !oldCategoryIdOfItemCollectionNewItem.equals(category)) {
                        oldCategoryIdOfItemCollectionNewItem.getItemCollection().remove(itemCollectionNewItem);
                        oldCategoryIdOfItemCollectionNewItem = em.merge(oldCategoryIdOfItemCollectionNewItem);
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
                Integer id = category.getCategoryId();
                if (findCategory(id) == null) {
                    throw new NonexistentEntityException("The category with id " + id + " no longer exists.");
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
            Category category;
            try {
                category = em.getReference(Category.class, id);
                category.getCategoryId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The category with id " + id + " no longer exists.", enfe);
            }
            Collection<Item> itemCollection = category.getItemCollection();
            for (Item itemCollectionItem : itemCollection) {
                itemCollectionItem.setCategoryId(null);
                itemCollectionItem = em.merge(itemCollectionItem);
            }
            em.remove(category);
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

    public List<Category> findCategoryEntities() {
        return findCategoryEntities(true, -1, -1);
    }

    public List<Category> findCategoryEntities(int maxResults, int firstResult) {
        return findCategoryEntities(false, maxResults, firstResult);
    }

    private List<Category> findCategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Category.class));
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

    public Category findCategory(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Category> rt = cq.from(Category.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
