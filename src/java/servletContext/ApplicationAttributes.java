/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servletContext;
import dao.FilmManager;
import error_util.EhrLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import model.Category;
import model.Film;
import model.FilmCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 *
 * @author Dinah
 * 
 * setServletContext
 * Invoked after population of normal bean properties but before an init callback 
 */
@Scope("application")
@Component
public class ApplicationAttributes {    
    
    @Autowired
    private FilmManager filmManager;

   private List<Category> categories;   
   
   private Date lastUpdate;
    
   @PostConstruct
    public void init() {
        
        List<Category> list = filmManager.allCategories();
        if(list == null || list.isEmpty())
            this.throwIllegalArgumentException("init()", "filmManager#allcategories returned null. ");
        categories = list;
        
        lastUpdate = new Date();                
        
    }
    
    public List<Category> getCategories() {
        return categories;
    }

   

    public Date getLastUpdate() {
        return lastUpdate;
    }
    
    public Category findCategory (Byte categoryId) {
        
        Category category = null;
        
        if(categoryId == null)
            this.throwIllegalArgumentException("findCategory", "Parameter categoryId is null.");
        
         for(Category c : categories)
             if (c.getCategoryId().equals(categoryId)) {
                 category = c;
                 break;
             } 
        if(category == null){
            this.throwIllegalArgumentException("findCategory", 
                    "Parameter categoryId has an invalid value. Category object not found.");
        }
        return category;
    }
    
      public Film findFilmByCategoryId(Byte categoryId, Short filmId)  {
        
        Category selectedCategory = this.findCategory(categoryId);
        
        Film foundFilm = null;      
        
        Set<FilmCategory> filmCategories = selectedCategory.getFilmCategories();
        
        for(FilmCategory filmCategory : filmCategories)
            
            if(filmCategory.getFilm().getFilmId().equals(filmId))
                foundFilm = filmCategory.getFilm();
        
        if(foundFilm == null) {
           
            this.throwIllegalArgumentException("findFilmByCategoryId", 
                    "Param filmId did not equal an id in record-set");
           
        }
        
        return foundFilm;
    }  
    
     public List<Film> initSortedFilmsByTitle(Category category){
        
        Set<FilmCategory> set = category.getFilmCategories();
        
        if(set == null || set.isEmpty()) //fetch attribute must be eager
            this.throwIllegalArgumentException("initSortedFilmsByTitle", 
                    "@OneToMany filmCategory collection of Category entity is null or empty.");
        
        List<Film> list = new ArrayList<> ();
        
        for(FilmCategory fc : set)
            list.add(fc.getFilm());       
        
        Collections.sort(list,(Film f1, Film f2)-> f1.getTitle().compareTo(f2.getTitle()));
        
        return list;
    }
    
  
   
   /*
    * Returns first category only.
    */ 
   public  Category categoryByFilm(Film film) {
       
       if(film == null)
           this.throwIllegalArgumentException("categoryByFilm", "Film parameter is null");
       
       Set<FilmCategory>filmCategories = film.getFilmCategories();
       
       if(filmCategories == null || filmCategories.isEmpty())
           this.throwIllegalArgumentException("categoryByFilm",
                   "@OneToMany filmCategory collection of Film parameter is null or empty.");
       
       Iterator<FilmCategory> iterator = filmCategories.iterator();
       
       return iterator.next().getCategory();
   }
    
    private void throwIllegalArgumentException(String method, String message) {
        
        String err = EhrLogger.doError(this.getClass().getCanonicalName(), method, message);
        throw new IllegalArgumentException(err);
    }
    
} //end class
