/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *
 * @author Dinah
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TextFormat {

    /*
     *  PROPER_NAME - only alpha, hyphen, no digits; removes spaces, and replaces with hyphen
     *  POSTAL_NAME - alpha, hyphen, no digits; retains spaces
     *  DEFAULT - digits, alpha, hyphen, forward-slash, ampersand, hash
     *  Note:
     *  Case enum is ignored if logical type is EMAIL
     */
    public enum Format {
        PROPER_NAME,
        POSTAL_NAME,
        EMAIL,
        ADDRESS_LINE,
        POSTAL_CODE,
        DEFAULT,
        NO_FORMAT,
        UPPER,
        LOWER,
        PROPER,
        NO_CASE;
    }
    
    Format[] value();
}
