/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;
import com.cart.Cart;
import view.attributes.ConstantUtil;
import dao.FilmManager;
import error_util.EhrLogger;
import java.io.Serializable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import model.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import servletContext.ApplicationAttributes;
import view.attributes.CustomerAttributes;
import view.attributes.PaymentAttributes;
//import servletContext.DebugApplicationContext;

/**
 *
 * Can eliminate initialization check at navigateHome
 * by retrieving filmList in constructor
 */

@Controller
@Scope("session") //eliminates database retrieval to assign filmList for each request
public class SpringDbController implements Serializable{   
    
    public static final String HOME_URL = "/";        
    
    private final FilmManager filmManager;         
    
    private final PageCalculator calculator;  
    
    @Autowired
    private ConstantUtil constants;
    
    @Autowired
    private Cart cart;          
    
    @Autowired
    private ApplicationAttributes applicationAttributes; 
    
    @Autowired
    private CustomerAttributes customerAttrs;    
    
    private List<Film> filmList = null;        
    
    public List<Film> getFilmList() {
        return this.filmList;
    }     
    
    @Autowired
    public SpringDbController(FilmManager filmManager, PageCalculator calculator){
        
        this.filmManager = filmManager;
        
        this.calculator = calculator;        
        
        int recordCount = filmManager.getRecordCount();
        
        calculator.initialize(recordCount);
        
        this.assignFilmList();
    }
    
    private void handleTestDb() {        
            filmManager.testRecoverableDataAccessException();       
    }
    
    /*
     * Note: If this method is invoked from /home, current indices as held 
     * in calculator properties will be used. 
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printHello(ModelMap model, HttpServletRequest request){    
        
        EhrLogger.printToConsole(this.getClass(), "printHello",
          "servletPath=" + request.getServletPath() );      
        
        this.addViewAttributes(model);
        
        return "index_tile";
   }   
  
   /*
    * Alias for printHello
    */
   @RequestMapping(value="/home", method=RequestMethod.GET)
   public String navigateHome(ModelMap model)  {          
       
        this.addViewAttributes(model);
        
        return "index_tile";
       
   }  
    /*
     * Rendered as a link on transactionErr.jsp
     * If there is a recoverable exception at assignList, recordset needs to be re-retrieved.
     * Note: If there is an error at Constructor retrieving the record-count, instantiation
     * failed. Binding to this method will re-execute the constructor.
     */ 
   @RequestMapping(value="/spring/homeOnDbError", method=RequestMethod.GET)
   public String homeOnDbError(ModelMap model) {
       
       this.assignFilmList(); //Possible Recoverable DataAccessException on this component
       
       this.addViewAttributes(model);
        
       return "index_tile";      
   }
   
   @RequestMapping(value="/spring/next", method=RequestMethod.GET)
   public String next(ModelMap model){              
        
           calculator.calcNext();           
           
           assignFilmList();
          
           this.addViewAttributes(model);      
     
       return "index_tile";
   }
   
  
   @RequestMapping(value="/spring/previous", method=RequestMethod.GET)
    public String previous(ModelMap model){
        
        calculator.calcPrevious();
        
        assignFilmList();
        
        this.addViewAttributes(model);
        
        return "index_tile";
       
   } 
   
   @RequestMapping(value="/spring/page/{pageNo}", method=RequestMethod.GET)  
   public String page(@PathVariable("pageNo") Integer pageNo,
           ModelMap model) {         
     
       calculator.calcFromPageNo(pageNo);
       
       assignFilmList(); 
       
       this.addViewAttributes(model);
       
       return "index_tile";
   } 
   
   private void assignFilmList(){      
      
      this.filmList = this.filmManager.getFilmRange(calculator.getCurrentStart(), 
              calculator.getCurrentEnd());   //See docs for start-end values        
             
     
   }
   
   
   private void addViewAttributes(ModelMap model){
      
      // Adding to request model is optional 
      model.addAttribute("cart", cart); //cart widget  
      
       // Adding to request model is optional
      model.addAttribute("applicationAttributes", applicationAttributes); //sidebar categoryList   
     
      constants.setCurrentUrl(HOME_URL); //Extracted by CustomerController on user-entry error
      
      model.addAttribute("customerAttributes", customerAttrs); //loginTime
      
      model.addAttribute("constantUtil",constants);
       
      model.addAttribute("springDbController", this); //this.filmList
      
      model.addAttribute("pageCalculator", calculator);       
    } 
}
