/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;


/**
 *
 * @author Dinah
 */
@Documented
@NotEmpty
@Size(max=50)
@ReportAsSingleViolation //do not run validator if @Size is violated
@Constraint(validatedBy = EmailValidValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailValid {
    
  
   String message() default "{EmailValid}"; //resource bundle key
    
   //String message() default "Please enter your email between 8 and 50 characters." ;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
