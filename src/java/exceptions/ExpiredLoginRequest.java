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
public class ExpiredLoginRequest extends Exception {
    
    private static final String FRIENDLY = "Checkout request from browser cache or history" ;
    
    private  String technical = "Request parameter not equal to session time. " ;
    
    public ExpiredLoginRequest() {
        
        super(FRIENDLY);
    }
    
    public ExpiredLoginRequest(String issue) {
        
        super(FRIENDLY);
        
        technical += issue;
        
    }

    public String getTechnical() {
        return technical;
    }
    
    
    
}
