/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.exception;

/**
 *
 * @author dinah
 */
public class RecordNotFoundException extends Exception {
    
    public RecordNotFoundException(String message) {
        
        super(message);
        
    }
    
    public RecordNotFoundException(String entityName, 
            String criterionFld,String criterionValue) {
        
        super("Record in table " 
                + entityName 
                + " could not be found by criterionFld: '"
                + criterionValue + "'");
    }
    
}
