/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.Film;

/**
 *
 * @author Dinah
 */
public interface SearchManager {
    
     public List<Film> searchByTitle(String searchText);
    
}
