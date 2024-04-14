/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.constraints;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Dinah
 */
@NotEmpty
@ReportAsSingleViolation
@Target({ElementType.FIELD,
    ElementType.METHOD,
    ElementType.TYPE,
    ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=PhoneFormatImpl.class)
public @interface PhoneValid {
    
    String message() default "{PhoneValid}" ;    
        
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default{};    
    
}