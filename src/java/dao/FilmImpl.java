/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import error_util.EhrLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.Category;
import model.Film;
import model.FilmCategory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Dinah
 */
@Repository
public class FilmImpl implements FilmDAO{

    @Autowired
    private SessionFactory sessionFactory;
    
    private boolean testException = false;
    
    @Override
    public List<Film> getFilmList(int start, int end) throws DataAccessException {
        
        List<Film> filmList = null;      
        
        String sql = "from Film where filmId between " + start + " and " 
                + end
                + " order by filmId";
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        filmList = (List<Film>)q.list();      
        return filmList;
    } 
    
    /*
     * @param start  1-based record number
     * @param end  last record number inclusive
     */
    @Override
    public List<Film> getFilmRange(int start, int end) throws DataAccessException {
        
        if(testException) {
            testException = false;
            throw new RecoverableDataAccessException("FilmImpl#getFilmRange: testing recoverable exception");
        }
        
        String sql = "from Film f order by f.filmId";
        
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        
        q.setMaxResults(end - start + 1);
        
        q.setFirstResult(start - 1); //0-based index
        
        List<Film> list = q.list();
        
        return list;
        
    }
    
    @Override
    public Map<Short,List<String>> categoryListByFilm(int start, int end) {
              
        List<Film> filmList = null;
        Map<Short, List<String>> map = new HashMap<>();
        String sql = "from Film where filmId between " + start + " and " + end;
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        filmList = (List<Film>)q.list();
        for(Film f : filmList){
            List<String> c = categoriesByFilm(f);
            map.put(f.getFilmId(), c);
        }
        return map;
    }
    
    private List<String> categoriesByFilm(Film f){
        List<String> list = new ArrayList();
        Set<FilmCategory> set = f.getFilmCategories();
        Iterator<FilmCategory> it = set.iterator();
        while(it.hasNext())
            list.add(it.next().getCategory().getName());
        return list;
    }
    
    @Override
    public List<Category> allCategories()  {
        List<Category> list = new ArrayList();
        String sql = "from Category";
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        list = (List<Category>)q.list();                
        return list;       
    }
    
    @Override
    public Integer getRecordCount()  {
        
        String sql = "Select count(*) from Film";
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        Long cnt = (long) q.uniqueResult();
        return Integer.valueOf(cnt.toString());
    }

    @Override
    public Film filmById(Short id)  {
        
       
        String sql = "from Film as f where f.filmId = " + id;
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        List<Film> list = (List<Film>) q.list();
        return list.get(0);
    }  
    
    /*
     * Not used
     * Not tested
     */
    @Override
    public List<Film> filmListByCategory(String id) {
        
        String sql = "from Category c where c.categoryId = :cid";
        
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        
        byte bid = new Byte(id).byteValue();
        
        q.setByte("cid", bid);
        
        Category category = (Category)q.list().get(0);
        
        Set<FilmCategory> set = category.getFilmCategories();
        
        List<Film> list = new ArrayList<> ();
        
        for(FilmCategory fc : set)
            list.add(fc.getFilm());
        
        return list;      
    }
    
    @Override
    public List<Film> searchByTitle(String[] words) {
        
        String formatted = "%";
        
        for(String s : words)
            formatted += s + "%";
        
        String sql = "Select f from Film f where lower(f.title) like '" +
                formatted.toLowerCase() + "'";
        
        Query query = sessionFactory.getCurrentSession().createQuery(sql);
        
        List<Film> found = query.list();
        
        return found;
        
    }
    
    @Override
     public Integer testRecoverableDataAccessException(boolean test) {
         
         if (test)
           throw new RecoverableDataAccessException(EhrLogger.doError(
                   this.getClass().getCanonicalName(), 
                   "testRecoverableDataAccessException", "Testing Exception"));
                
         return this.getRecordCount();
     } 
     
}
