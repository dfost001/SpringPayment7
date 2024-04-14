/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;
/**
 *
 * @author Dinah3
 */
public class HttpConnectException extends HttpException{
    
    private final Boolean recoverable;
    
    public HttpConnectException(Throwable cause, String message,
            Boolean recoverable,
            String friendly, String method){
        
        super(cause,  message, friendly,  method);
        this.recoverable = recoverable;
       
    }  

    public Boolean getRecoverable() {
        return recoverable;
    } 
    
    
} //end class
