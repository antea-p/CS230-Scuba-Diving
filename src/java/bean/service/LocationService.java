/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.service;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import jpa.Category;
import jpa.CategoryFacade;
import jpa.Location;
import jpa.LocationFacade;

/**
 *
 * @author boone
 */
@Stateless
public class LocationService {
    
    @Inject
    private LocationFacade locationFacade;
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("DEBUG: LocationService is instantiated.");
    }
    
    public Location[] getAll() {
        System.out.println("DEBUG: Calling locationFacade.findAll:");
        return locationFacade.findAll().toArray(new Location[0]);
    }
    
    public Location getLocationById(int id) {
       System.out.println("DEBUG: Calling locationFacade.find(id):");
       Location location = locationFacade.find(id);
       System.out.println("DEBUG: locationFacade.find(id) location: " + location);
       return location;
    }
    
}
