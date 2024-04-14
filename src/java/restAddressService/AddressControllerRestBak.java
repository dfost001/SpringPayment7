/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService;

import dao.CustomerManager;
import error_util.EhrLogger;
import httpUtil.HttpException;
import java.io.IOException;
import restAddressService.addressService.FieldEhr;
import restAddressService.addressService.SvcAnalysis;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import model.customer.AddressTypeEnum;
import model.customer.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import restAddressService.addressService.AddrSvcConstants;
import restAddressService.addressService.ServiceConnect;
import util.StringUtil;



/**
 *
 * @author Dinah
 * Revised to fill in all UI fields based on SvcAnalysis#isAllValid
 */
@Scope("request")
//@RestController
public class AddressControllerRestBak {
    
    public static final String ANALYSIS_KEY = "svcAnalysisCustomer";
    public static final String ANALYSIS_KEY_SHIP_ADDRESS = "svcAnalysisShipAddress";
    public static final String ADDRESS_TYPE_ENUM = "addressTypeEnum";
    
    private static Short countryId = 103;
    
    @Autowired
    private CustomerManager manager;
   
    @Autowired
    private AjaxAddressValidator validator; 
    
    @Autowired
    private ServiceConnect svcConnect;
    
    
    @RequestMapping(value="/resources/verifyAddress/{addressTypeEnum}", method=RequestMethod.POST,
            produces={"application/json","text/plain"}, consumes="application/json")
    public SvcAnalysis verifyAddress(@Valid @RequestBody AjaxRequest address,
            BindingResult result, 
            @PathVariable("addressTypeEnum")String addressType,
            HttpSession session) throws HttpException, IOException {
        
        System.out.println("AddressControllerRest:verifying deserializer:" + address.getStreet());
        
        SvcAnalysis analysis = new SvcAnalysis();
        
        validator.validate(address, result);
        
        if(result.hasFieldErrors() || result.hasGlobalErrors()) {
            analysis.setValid(Boolean.FALSE);
            analysis.setConfirmRequired(Boolean.FALSE);
            analysis.setAjaxRequest(address);
            initErrors(result, analysis);          
            return analysis;
        }      
        
        analysis = svcConnect.processAddress(address);
        
        analysis.setAjaxRequest(address);
        
        this.evalCity2(analysis); 
        
        this.evalConfirmRequired(analysis, address);
        
        this.setValidatedSessionKeys(session, AddressTypeEnum.valueOf(addressType),
                analysis);      
      
        return analysis;
    }
    
    @RequestMapping(value="/deserialize", method=RequestMethod.POST, 
            consumes="application/json", produces="application/json")
    private String testDeserializer(@RequestBody AjaxRequest request) {
        return request.getStreet();
    }
    
    private void initErrors(BindingResult result, SvcAnalysis postal){
        
        List<FieldError> fldErrors = result.getFieldErrors();
        for(FieldError e : fldErrors) {
            String deMsg = e.getField() + ": " + e.getDefaultMessage();
            FieldEhr ehr = new FieldEhr(e.getField(), deMsg);
            postal.addFieldEhr(ehr);
        }
        
        List<ObjectError> globErrors = result.getGlobalErrors();
        for(ObjectError o : globErrors) {
            postal.addSvcMessage(o.getDefaultMessage());
            System.out.println("AddressControllerRest#initErrors: " + o.getDefaultMessage());
        }
        
        
    }//end init
    
    private void evalCity(SvcAnalysis postal){
        
        String err = "City found by service is not in the city selection. Please review zip entry.";
        
        if(postal.getCity().isEmpty())
            return;
        
        City city = manager.findCityByName(postal.getCity());
        
        if(city == null) {
            postal.getSvcMessages().clear();
            postal.getSvcMessages().add(0,err);
            postal.setConfirmRequired(false);
            postal.setValid(false);
            return;
        }
        
        postal.setCityId(city.getCityId());
    }
    
    /*
     * Note: confirmRequired will be set by service since a city not in list
     * cannot be uploaded
    */
     private void evalCity2(SvcAnalysis postal){       
        
        if(postal.getCity().isEmpty())
            return;
        
        City city = manager.findCityByName(postal.getCity());
        
        if(city == null) {
            
           city = manager.insertCity(postal.getCity(), countryId);
           
           postal.setCityInsertionRequired(true); 
        }
        
        postal.setCityId(city.getCityId());
    }
    /*
     * If valid and confirm not required, JavaScript assigns zipPlus4 and addressLine
     * If uploaded postal fields are equal to address service returns, the formatted address-line
     * and full zip can be assigned
     */ 
    private void evalConfirmRequired(SvcAnalysis postal, AjaxRequest request) {
        
        if(!postal.getValid())
            return;        
        
      /*  String entryZip5 = StringUtil.isNullOrEmpty(request.getZipcode()) ?
                "" : request.getZipcode().substring(0, 5);
        
       boolean svcZipEqualsEntry = entryZip5.isEmpty() || postal.getZip().equals(entryZip5);
        
        if( svcZipEqualsEntry  
                && postal.getCity().equals(request.getCity())
                && postal.getStateAbbrev().equals(request.getState())) {
            
            postal.setConfirmRequired(false); 
        } */
        if(postal.getCity().equals(request.getCity())
                && postal.getStateAbbrev().equals(request.getState())) {
           postal.setConfirmRequired(false);
        }
            
        doReformattedMessages2(postal, request) ;            
          
    }
    /*
     * Messages for JavaScript revisions
    */
    private void doReformattedMessages(SvcAnalysis postal, AjaxRequest request) {
        
        if(postal.isConfirmRequired())
            return;
        
        String reformatted = "";
        
         if(!request.getStreet().equals(postal.getValidatedStreetLineFormat())) {
                
               reformatted = "'" + request.getStreet() + "'"
                    + " has been reformatted. Please review.";
               
                postal.addSvcMessage(reformatted);
        
         }
         
         if(!request.getZipcode().equals(postal.getZipPlus4())){
             
              reformatted = "'" + request.getZipcode() + "'"
                    + " has been reformatted. Please review.";
               
                postal.addSvcMessage(reformatted);
         }
        
    }
    
    private void doReformattedMessages2(SvcAnalysis postal, AjaxRequest request) {
        
        if(postal.isConfirmRequired())
            return;
        
        postal.getErrors().clear();
        
        if(!request.getStreet().equals(postal.getValidatedStreetLineFormat())) {
            String msg = "Reformatted: " + request.getStreet();
            String fld = AddrSvcConstants.fldStreet;
            FieldEhr ehr = new FieldEhr(fld,msg);
            postal.addFieldEhr(ehr);
        }
        
        if(!request.getZipcode().equals(postal.getZipPlus4())){
            String msg = "Reformatted: " + request.getZipcode();
            String fld = AddrSvcConstants.fldZip;
            FieldEhr ehr = new FieldEhr(fld,msg);
            postal.addFieldEhr(ehr);
        }
        
    }
    
    private void setValidatedSessionKeys(HttpSession session, AddressTypeEnum type,
            SvcAnalysis analysis) {
        
       if(type.equals(AddressTypeEnum.Customer))
            session.setAttribute(ANALYSIS_KEY, analysis);
       
       else if(type.equals(AddressTypeEnum.ShipAddress))
            session.setAttribute(ANALYSIS_KEY_SHIP_ADDRESS, analysis);
       
       else {
           throw new  IllegalArgumentException(
           EhrLogger.doError(this.getClass().getCanonicalName(),
               "verifyAddress",
               "JavaScript path variable must be set to "
                + "model.customer.AddressTypeEnum.Customer or ShipAddress"));
       }
        
    }
    
}//end class
