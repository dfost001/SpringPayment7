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
public interface FilmManager {
    public Map<Short,List<String>> categoryListByFilm(int start, int end);
    public List<Film> getFilmList(int start, int end);
    public List<Film> getFilmRange(int start, int end);
    public Integer getRecordCount();
    public List<Category> allCategories(); 
    public Film getFilmById(Short filmId); 
    public Integer testRecoverableDataAccessException();
}
