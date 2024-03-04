/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author boone
 */
@Stateless
public class BookingFacade extends AbstractFacade<Booking> {

    @PersistenceContext(unitName = "CS230-PZ-5157-AnteaPrimoracPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BookingFacade() {
        super(Booking.class);
    }

    public int findNextId() {
        return (int) em.createNamedQuery("Booking.findNextId").getSingleResult();
    }

    public Booking findLastByUserId(User user) {
        try {
            return em.createNamedQuery("Booking.findLastByUserId", Booking.class)
                    .setParameter("user", user)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
