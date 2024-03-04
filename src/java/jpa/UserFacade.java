/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import bean.exception.InvalidCredentialsException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author boone
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "CS230-PZ-5157-AnteaPrimoracPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }

    public int findNextId() {
        return (int) em.createNamedQuery("User.findNextId").getSingleResult();
    }

    public User findByUsernameAndPassword(String username, String password) throws InvalidCredentialsException {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password); // TODO: hashing lozinke
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.err.println("ERR UserFacade.findByUsernameAndPassword: NoResultException!");
            throw new InvalidCredentialsException("No matching user found!");
        }
    }
}
