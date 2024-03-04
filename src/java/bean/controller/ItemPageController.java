/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.controller;

import bean.service.ShoppingCart;
import bean.service.ItemService;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
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
public class ItemPageController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ItemService itemService;
    
    @Inject
    private ShoppingCart shoppingCart;

    private Item item;
    private int quantity = 1;

    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: ItemPageController is instantiated.");
    }
    
    public int getQuantity() {
        System.out.println("ItemPageController CURRENT QUANTITY: " + quantity + " FOR ITEM: " + item);
        return quantity;
    }

    public void setQuantity(int quantity) {
        System.out.println("ItemPageController SETTING QUANTITY TO: " + quantity);
        this.quantity = quantity;
    }

    public Item getItem() {
        if (item == null) {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            int itemId = Integer.parseInt(params.get("id"));
            System.out.println("URL PARAM ID: " + itemId);

            Item tempItem = itemService.get(itemId);
            if (tempItem == null) {
                System.out.println("DEBUG: itemService.get returned null for ID " + itemId);
            } else {
                this.item = tempItem;
                System.out.println("GOT ITEM: " + item.toString());
            }
        }
        return this.item;
    }

  public String addToCart() {
        if (this.item == null) {
            getItem();
        }
        System.out.println("ItemPageController: ADDING TO CART: " + item.toString() + "WITH AMOUNT: " + quantity);
        shoppingCart.add(item, quantity);
        return "shoppingCart?faces-redirect=true";
    }

    @Override
    public String toString() {
        return "ItemPageController{" + "itemService=" + itemService + ", shoppingCart=" + shoppingCart + ", item=" + item + ", quantity=" + quantity + '}';
    }
}