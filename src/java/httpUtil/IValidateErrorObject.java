/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;

/**
 *
 * @author dinah
 */
public interface IValidateErrorObject {
    
    public String validate(Object deserializedObject,
            Integer responseCode,
            String httpEntity,
            String invokingMethod)
            throws RuntimeException ;
    
}
