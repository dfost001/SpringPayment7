/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import model.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Dinah
 */
@Service
public class SearchManagerImpl implements SearchManager{
    
    @Autowired
    private FilmDAO filmDAO;
    
    private List<Film> found;
    
    
    @Transactional
    @Override
    public List<Film> searchByTitle(String searchText) {
       
        
        String[] words = searchText.split("\\s");
        
        found = new ArrayList<>();
        
        this.searchForward(words);
        
        return found;
    }
    
    /*
     * Search left to right
    */    
    private void searchForward(String[] words){
        
         String[] copy;
        
         for(int len = words.length; len > 0; len--){
            
            copy = Arrays.copyOfRange(words, 0, len);
            
            List<Film> temp = filmDAO.searchByTitle(copy);
            
            if(temp != null && !temp.isEmpty()) {
                removeDuplicates (temp);
                removeSequence(temp,copy);
                found.addAll(temp);                
            }
            
        }
        if(words.length == 1) 
            return;
        else searchForward(Arrays.copyOfRange(words, 1, words.length));        
       
    }
    
    private void removeDuplicates(List<Film> temp) {
        Iterator<Film> iter = temp.iterator();
        while(iter.hasNext()){
            Film f = iter.next();
            if(isDuplicate(f))
                iter.remove();
        }
    }
    
    private boolean isDuplicate(Film compare) {
        
        for(Film f : found)
            if(compare.getTitle().equals(f.getTitle()))
                return true;
        return false;
    }
    /*
     * Optional: Removes a search word within a word. So if a title 
     * found does not contain the whole word, remove from the list
     * before adding to found list.
    */
    private void removeSequence(List<Film> temp, String[] copy) {
        
       Iterator<Film>iter = temp.iterator();
        
       while (iter.hasNext()) {
           
           Film f = iter.next();
           
           String title = f.getTitle();
           
           String[] words = title.split("\\s");
           
           List<String> wordList = Arrays.asList(words);
           
           for(int i=0; i < copy.length; i++) {
               if(!wordList.contains(copy[i].toUpperCase())) {
                   iter.remove();
                   break;
               }    
           }          
       }//end while
    }
    
}//
