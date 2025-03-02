/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import dao.CustomerManager;
import dao.FilmManager;
import error_util.EhrLogger;
import java.util.List;
import model.customer.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import restCustomerService.ICustomerServiceRest;
import util.BeanUtil;

/**
 *
 * @author dinah
 */
@Controller
@Scope("request")
public class ExceptionExampleController {
    
    @Autowired 
    private CustomerManager customerManager;
    
    @Autowired 
    private ICustomerServiceRest customerService;
    
    @Autowired
    private PageCalculator calculator;  
    
    @Autowired
    private FilmManager filmManager;   
    
    private List<String> nullList;

    public List<String> getNullList() {
        return nullList;
    } 
    
    private String emptyString = new String();

    public String getEmptyString() {
        return emptyString;
    }
        
    @RequestMapping(value="/exception/example/view", method=RequestMethod.GET)
    public String requestExampleView() {
        
        return "exceptionExample_tile";
    }
    
    @RequestMapping(value="/exception/example/testRecoverableGet", method=RequestMethod.GET)
    public void doRecoverableDataGet() {
        
        filmManager.testRecoverableDataAccessException();
    }
    
    @RequestMapping(value="/exception/example/transient", method=RequestMethod.POST)
    public void doTransientDataException() {               
             
            throw new QueryTimeoutException (
                   EhrLogger.doError(this.getClass().getCanonicalName(),
                           "doTransientDataException", "Testing TransientDataException"));         
    }
    
    @RequestMapping(value="/exception/example/nontransient", method=RequestMethod.POST)
    public void doNonTransientDataException(ModelMap map) throws DataAccessException {
      
        
        List<City> list = customerManager.getCitiesError();
             
       
    }
    
     @RequestMapping(value="/exception/example/nullpointer", method=RequestMethod.POST)
    public void doNullPointer() {      
        
        for(String s : nullList) {
            String unreachable = "This statement does not execute";
        }
    }     
    
    @RequestMapping(value="/exception/example/invalidMethodParameter", method=RequestMethod.POST)
    public void doInvalidMethodParameter() {
        
        calculator.calcFromPageNo(-1);
    }
    @RequestMapping(value="/exception/example/uninitializedProperty", method=RequestMethod.POST)
    public void doUninitializedProperty() {
        
        BeanUtil.throwFieldsNotInitialized(this.getClass(), this, "nullList", "emptyString");
        // BeanUtil.evalNullOrEmptyFields(this.getClass(), this);//NoSuchMethod for autowired dependencies
    }
    
    @RequestMapping(value="/exception/example/missingParameter", method=RequestMethod.POST)
    public void doMissingParameter(@RequestParam("missingParameter") String param) {        
       
    }
    
  /*  @RequestMapping(value="/exception/example/testCities", method=RequestMethod.GET)
    public String testCitesByCountry() {
        
        List<City> list = this.customerManager.getCitiesByCountryId(Short.valueOf("103"));
        
        list.forEach(c -> System.out.println(c.getCityName()));
        
        return "exceptionExample_tile";
    } */
    
    @RequestMapping(value="/exception/example/testCities", method=RequestMethod.GET)
    public String testCitesByCountry() {
        
        List<Object[]> rows = this.customerService.getCitiesWithDistrictByCountry(Short.valueOf("103"));      
        
        return "exceptionExample_tile";
    } 
    
}
