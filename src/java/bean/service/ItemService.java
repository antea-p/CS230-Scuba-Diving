/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import jpa.Item;
import jpa.ItemFacade;

@Stateless
public class ItemService {
    
    @Inject
    private ItemFacade itemFacade;
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: ItemService is instantiated.");
    }


    public Item get(int id) {
        System.out.println("Calling itemFacade.FIND:");
        return itemFacade.find(id);
    }

}
