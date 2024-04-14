/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;


import error_util.EhrLogger;
import java.util.List;
import java.util.Map;
import model.Category;
import model.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class FilmManagerImpl implements FilmManager {
    
    @Autowired
    private FilmDAO filmDAO;
    
    
    @Override
    @Transactional
    public List<Film> getFilmList(int start, int end) {
        return filmDAO.getFilmList(start, end);
    }
    
    @Override
    @Transactional
    public List<Film> getFilmRange(int start, int end) {
       List<Film> list = filmDAO.getFilmRange(start, end);
       nullCheck(list, "getFilmRange");
       return list;        
    }
    
    @Override
    @Transactional
    public Integer getRecordCount(){
        return filmDAO.getRecordCount();
    }
    
    @Override
    @Transactional
   public Map<Short,List<String>> categoryListByFilm(int start, int end){
         return filmDAO.categoryListByFilm(start, end);
   }
   
   @Transactional
   @Override
   public List<Category> allCategories(){
       List<Category> list = filmDAO.allCategories();
       nullCheck(list, "allCategories");
       return list;
   }
   
   @Transactional
   @Override
   public Film getFilmById(Short id){
        boolean testTransient = false;
        if(testTransient)
            throw new RecoverableDataAccessException("Testing TransientDataAccessException");
       return filmDAO.filmById(id);
   }
    @Transactional
    @Override
   public Integer testRecoverableDataAccessException() {
       
      return filmDAO.testRecoverableDataAccessException(true);
   } 
   
   /*
    * Not tested.
    */
   private void nullCheck(Object obj, String method) {
       
       String err = obj.getClass().getSimpleName() + " is null.";
       
       if(obj == null) {
           
           err = EhrLogger.doError(this.getClass().getCanonicalName(), method, err);
           
           throw new DataRetrievalFailureException(err);
       }
           
   }
} //end class
