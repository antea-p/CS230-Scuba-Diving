/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import jpa.Category;
import jpa.CategoryFacade;

/**
 *
 * @author boone
 */

@Stateless
public class CategoryService {
    
    @Inject
    private CategoryFacade categoryFacade;
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: CategoryService is instantiated.");
    }
    
    public Category get(int id) {
        System.out.println("Calling categoryFacade.FIND:");
        return categoryFacade.find(id);
    }
    
    
}
