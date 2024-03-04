/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import jpa.Item;

@Named
@SessionScoped
@Stateful
public class ShoppingCart {

    private Map<Item, Integer> cartContents = new HashMap<>();
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: ShoppingCart is instantiated.");
    }

    public Map<Item, Integer> getCartContents() {
        return cartContents;
    }
            
    public int getQuantity(Item item) {
        if (cartContents.containsKey(item)) {
            System.out.println("ShoppingCart CURRENT QUANTITY: " + cartContents.get(item));
            return cartContents.get(item);
        }
        return 0;
    }
    
    public void setQuantity(Item item, int quantity) {
        if (quantity > 0 && cartContents.containsKey(item)) {
            cartContents.put(item, quantity);
            System.out.println("ShoppingCart NEW QUANTITY: " + cartContents.get(item));
        } else if (quantity <= 0) {
            cartContents.remove(item);
            System.out.println("ShoppingCart NEW QUANTITY: 0");
        }
    }
    
    public void add(Item item, int quantity) {
        if (quantity > 0) {
            int currentQuantity = cartContents.getOrDefault(item, 0);
            cartContents.put(item, currentQuantity + quantity);
        }
        System.out.println("ShoppingCart QUANTITY: " + getQuantity(item));
    }

    public void remove(Item item) {
        if (cartContents.containsKey(item)) {
            cartContents.remove(item);
            System.out.println("ShoppingCart REMOVED ITEM: " + item);
        }
    }

    public void increment(Item item) {
        if (cartContents.containsKey(item)) {
            int currentQuantity = cartContents.get(item);
            cartContents.put(item, currentQuantity + 1);
            System.out.println("ShoppingCart INCREMENTED: " + item);
        }
    }

    public void decrement(Item item) {
        if (cartContents.containsKey(item)) {
            int currentQuantity = cartContents.get(item);
            if (currentQuantity > 1) {
                cartContents.put(item, currentQuantity - 1);
                System.out.println("ShoppingCart DECREMENTED: " + item);
            } else {
                cartContents.remove(item);
                System.out.println("ShoppingCart REMOVED: " + item);
            }
        }
    }

    public double getSubtotal() {
        return cartContents.entrySet().stream().map(
                entry -> entry.getValue() * entry.getKey().getCost().doubleValue())
                .map(itemCost -> itemCost)
                .collect(Collectors.summingDouble(Double::doubleValue));
    }
    
    private boolean hasShippingCost(Item item) {
        int categoryId = item.getCategoryId().getCategoryId();
        return (categoryId == 2 || categoryId == 4);
    }
    
    public double getShippingCosts() {
        // 9.99 predstavlja fiksni trošak dostave.
        long itemCount = cartContents.entrySet().stream()
                .filter(e -> hasShippingCost(e.getKey()))
                .count();
        return (itemCount > 0)? 9.99 : 0;
    }
    
    public double getGrandTotal() {
        return getSubtotal() + getShippingCosts();
    }
    
    public void clear() {
        cartContents.clear();
        getGrandTotal();
    }

    @Override
    public String toString() {
        return "ShoppingCart{" + "cartContents=" + cartContents + '}';
    }

}
