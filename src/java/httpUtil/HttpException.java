/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;
/**
 *
 * @author Dinah3
 */
public class HttpException extends Exception {
    
    private String friendly = "";   
    private String method = ""; 
    private int responseCode = -1;
   
    
    public HttpException(Throwable cause, String message, String friendly, String method){
        super(message,cause,true,true);
        this.friendly = friendly;
        this.method = method;
    }

    public String getFriendly() {
        return friendly;
    }

    public String getMethod() {
        return method;
    }

    public void setFriendly(String friendly) {
        this.friendly = friendly;
    }

    public void setMethod(String method) {
        this.method = method;
    }  

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    
} //end class
