/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.modelattribute;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Dinah
 */
@Component
public class BirthDateValidator implements Validator{
    
    

    @Override
    public boolean supports(Class<?> type) {
        
        return type.equals(BindModel.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        System.out.println("Inside BirthDateValidator#validate");
        if(errors.getFieldError("birthDate") != null)
           return;
        BindModel model = (BindModel)o;
        GregorianCalendar dt = getCalendar(model.getBirthDate());
        GregorianCalendar current = getCalendar(new Date());
        current.roll(Calendar.YEAR, -18);
        if(dt.compareTo(current) > 0) //entered birth date is after 18 year date       
            errors.rejectValue("birthDate", "date.invalid.age", "Invalid age");
    }
    
    private GregorianCalendar getCalendar(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
        String fmt = sdf.format(dt);
        String[] parts = fmt.split("/");
        GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(parts[2]), 
                Integer.parseInt(parts[0]), 
                Integer.parseInt(parts[1]));
        return cal;
    }
    
}
