/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.controller;

import bean.service.ShoppingCart;
import java.io.Serializable;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import jpa.Item;

/**
 *
 * @author boone
 */
@Named
@ViewScoped
public class ShoppingCartController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ShoppingCart shoppingCart;

    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: ShoppingCartController is instantiated.");
    }
    
    public Item[] getItems() {
        Set<Item> keySet = this.shoppingCart.getCartContents().keySet();
        System.out.println("KEYSET: " + keySet);
        return keySet.toArray(new Item[0]);
    }
       
    public int getQuantity(Item item) {
        return shoppingCart.getQuantity(item);
    }
    
    public double getSubtotal() {
        return shoppingCart.getSubtotal();
    }
    
    public double getShippingCosts() {
        return shoppingCart.getShippingCosts();
    }
    
    public double getGrandTotal() {
        return shoppingCart.getGrandTotal();
    }
    
    public void increment(Item item) {
        shoppingCart.increment(item);    
    }
    
    public void decrement(Item item) {
        shoppingCart.decrement(item);
    }
     
    public void remove(Item item) {
        shoppingCart.remove(item);
    }
    
    public boolean hasItems() {
    return !shoppingCart.getCartContents().isEmpty();
}

}
