/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author dinah
 */
public class NonCurrentUpdateRequest extends Exception {
    
    private static final String MESSAGE = "You have requested an expired update form." ;
    
    private static final String TECHNICAL = "Edit Form request parameter ADDRESS_TIME rendered in "
            + "Customer/ShipAddress JSP is not equal to current session property 'addressTime' "
            + "in CustomerAttributes";
    
    public NonCurrentUpdateRequest() {
    
         super(MESSAGE);
    }
    
    public String getTechnical() {
        
        return TECHNICAL;
    }
}
