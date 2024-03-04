/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.controller;

import bean.service.CategoryService;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import jpa.Category;
import jpa.Item;

/**
 *
 * @author boone
 */
@Named
@ViewScoped
public class CategoryPageController implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Inject
    private CategoryService categoryService;
    
    private Category category;
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: CategoryPageController is instantiated.");
    }
    
    public Category getCategory() {
       if (category == null) {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            int categoryId = Integer.parseInt(params.get("id"));
            System.out.println("URL PARAM ID: " + categoryId);

            Category tempCategory = categoryService.get(categoryId);
            if (tempCategory == null) {
                System.out.println("DEBUG: categoryService.get returned null for ID " + categoryId);
            } else {
                this.category = tempCategory;
                System.out.println("GOT ITEM: " + category.toString());
            }
        }
        System.out.println("DEBUG: Category Collection<Item> : " + category.getItemCollection().toArray()[0]);
        return this.category;
    }
    
    public Item[] getItems() {
        return getCategory().getItemCollection().toArray(new Item[0]);
    }
}
