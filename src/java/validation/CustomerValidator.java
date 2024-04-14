/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;


import java.util.Iterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Path.Node;
import org.springframework.validation.Errors;
import javax.validation.groups.Default;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;


/**
 * @Autowired javax.validation.AddressValidator will obtain JSR validator
 * ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
 * Validator validator = factory.getValidator();
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class CustomerValidator {
    
    @Autowired
    private AddressValidator addrValidator ; 
    
    @Autowired
    private javax.validation.Validator validator;      
   
    public void validate(Object o, Errors errors) {          
        
        PostalAddress address  = (PostalAddress)o;     
        
        Set<ConstraintViolation<Object>> set = validator.validate(address, Default.class);
        
        if(!set.isEmpty()) {
            addErrors(set, errors);             
        }   
        
       addrValidator.validate(address,errors);              
        
    }
    
  
    
    private void addErrors(Set<ConstraintViolation<Object>> set, Errors errors){
        
        for(ConstraintViolation<?> v : set) {
            String field = this.getName(v.getPropertyPath());
            errors.rejectValue(field, "",  v.getMessage());
           // System.out.println(field + "=" + v.getMessage()); --messages will not compare correctly
        }
        
    }
    
    private String getName(Path path){
		String name = "";
		Iterator<Node> node = path.iterator();
		while(node.hasNext())
			name +=  node.next().getName() + ".";
                
                /*if(name.contains("addressId"))
                    this.addressOK = Boolean.FALSE;*/
                
               	return name.substring(0, name.length() - 1);
	}    
} //end class
