/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author boone
 */
@Stateless
public class BookingItemFacade extends AbstractFacade<BookingItem> {

    @PersistenceContext(unitName = "CS230-PZ-5157-AnteaPrimoracPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BookingItemFacade() {
        super(BookingItem.class);
    }
    
}
