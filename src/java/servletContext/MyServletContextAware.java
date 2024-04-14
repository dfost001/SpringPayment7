/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servletContext;
import dao.FilmManager;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.ServletContext;
import model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

/**
 *
 * @author Dinah
 * 
 * setServletContext
 * Invoked after population of normal bean properties but before an init callback 
 */
public class MyServletContextAware implements ServletContextAware{
    
    private ServletContext servletContext;
    
    @Autowired
    private FilmManager filmManager;

    @Override
    public void setServletContext(ServletContext sc) {
        
        servletContext = sc;
    }
        
    public void init() {
        
        List<Category> categories = filmManager.allCategories();
        servletContext.setAttribute("categories", categories);
        servletContext.setAttribute("currentDate", new Date());
        servletContext.setAttribute("lastUpdate",
                new GregorianCalendar(2017, 10, 6).getTime()); //months are zero-based
        
    }
    
    public List<Category> getCategories() {
        return (List<Category>)servletContext.getAttribute("categories");
    }
        
    
}
