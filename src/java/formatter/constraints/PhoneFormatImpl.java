/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.constraints;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import formatter.MaskUtil;

/**
 *
 * @author Dinah
 */
public class PhoneFormatImpl implements ConstraintValidator<PhoneValid, String>{
    
   
    @Override
    public void initialize(PhoneValid constraint) {
       
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        
        String message = MaskUtil.validateByContent(value, "(###) ###-####");
        
        if(message.isEmpty())
            return true;
        
        this.buildMessage(context, message);
        
        return false;
        
    }
    
    private void buildMessage(ConstraintValidatorContext ctx, String message) {      
        
        ctx.disableDefaultConstraintViolation();
        
        ctx.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        
    }
    
} //end class
