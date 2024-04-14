/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import java.util.Map;
import model.Category;
import model.Film;


/**
 *
 * @author Dinah
 */
public interface FilmDAO {
    public List<Film> getFilmList(int start, int end)  ;
    public List<Film> getFilmRange(int start, int end) ;
    public Integer getRecordCount() ;
    public Map<Short,List<String>> categoryListByFilm(int start, int end) ;
    public List<Category> allCategories() ;
    public Film filmById(Short id) ;   
    public List<Film> filmListByCategory(String id);
    public List<Film> searchByTitle(String[] words);
    public Integer testRecoverableDataAccessException(boolean test);
}
