/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;


import dao.SearchManager;
import java.util.List;
import java.io.Serializable;
import model.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Dinah
 */
@Controller
@Scope("session")
public class SearchController implements Serializable{
    
    private String message;
    
    private List<Film> films;
    
    @Autowired
    private SearchManager searchManager;

    public String getMessage() {
        return message;
    }

    public List<Film> getFilms() {
        return films;
    } 
    
    @RequestMapping(value="search/request", method=RequestMethod.GET)
    public String searchRequest(ModelMap map) {
        map.addAttribute("searchController", this);
        return "searchResult_tile";
    }
    
    
    @RequestMapping(value="search/doSearch", method=RequestMethod.GET)
    public String doSearch(@RequestParam("text") String searchText,
            ModelMap map) {
        
        message = "Search results for '" + searchText + "' : " ;
        
        films = searchManager.searchByTitle(searchText);
        
        message += films.size();
        
        //System.out.println("SearchController:size=" + films.size());
        
        map.addAttribute("searchController", this);
        
        return "searchResult_tile";
        
    }
    
}
