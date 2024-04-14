/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;

/**
 *
 * @author Dinah3
 */
public class HttpClientException extends HttpException{
    
   
    private String debug = "";
    private String textMessage = "";
    private Object errObj = null;
    
    public HttpClientException(Throwable cause, String message, String friendly, String method){
        
        super(cause, message, friendly, method);
       
        
    }
    
    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public Object getErrObj() {
        return errObj;
    }

    public void setErrObj(Object errObj) {
        this.errObj = errObj;
    }
    
} //end class
