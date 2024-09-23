/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;

import java.io.IOException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author dinah
 */
public class JerseyClientUtil {
    
    private final Class<?> invClass;
    private final String invMethod;
    private String contentType;
    private String entity;
    
    public JerseyClientUtil(Class<?> invokingClass, String invokingMethod) {
        this.invClass = invokingClass;
        this.invMethod = invokingMethod;
    }
    
    public void handleException(Exception ex, Response response, String entity) 
            throws HttpException {   
        
        this.entity = entity;
       
        if(response != null) {
                 
            this.contentType = response.getHeaderString("Content-Type");
            contentType = contentType == null ? "Content-Type header is empty" : contentType;
        }
        else contentType = "Response is null";        
        
        if(WebApplicationException.class.isAssignableFrom(ex.getClass()))
            
             initWebEx((WebApplicationException)ex);
        
        else if(ProcessingException.class.isAssignableFrom(ex.getClass()))
            
             initProcessingException((ProcessingException)ex, response);            
    }
    /*
     * To do: evaluate type of response.getEntity() with Content-Type header
     */
    private void initWebEx(WebApplicationException webex) 
           throws HttpClientException {
              
        String status = webex.getResponse().getStatusInfo().getReasonPhrase();
        
        String message = status + ":" + webex.getMessage();
        
        HttpClientException httpClient = this.initClientException(webex, 
                message, webex.getResponse().getStatus());
        
        throw httpClient;
    }
    /*
     * To do: else clause should look for nested IOException
     * JerseyClient will wrap IOExceptions thrown from MessageBodyReader
     * in ProcessingException
     */
    private void initProcessingException(ProcessingException ex, Response response)
        throws HttpConnectException, HttpClientException {     
        
      if(response == null) {          
          if(ex.getCause() != null) {          
             evalRecoverableConnection(ex);
          }
          else {
              throw initConnectException(ex, ex.getMessage(), Boolean.FALSE);
          }            
      } else if(response != null) {           
          if(ex.getCause() != null) {
              evalReaderException(ex, response.getStatus());
          }
          else throw initClientException(ex, ex.getMessage(), response.getStatus());   
      }
    }
    
    private void evalRecoverableConnection(ProcessingException ex) 
            throws HttpConnectException {
        String issue = "";
        String message = "";
        
         if(IllegalStateException.class.isAssignableFrom(ex.getCause().getClass())
                && ex.getMessage().toLowerCase().contains("already connected")) {
            
            message =  ex.getMessage() + ": " + 
                    "'Already connected' indicates a possible server connection problem.";
            throw initConnectException(ex, message, Boolean.TRUE);
          }
          else {
              issue = "Nested exception: " + ex.getCause().getClass().getCanonicalName();
              message = ex.getMessage() + ":" + issue;
              throw initConnectException(ex, message, Boolean.FALSE);
          }            
    }
    
    private void evalReaderException(ProcessingException ex, int responseCode)
                  throws HttpClientException {

       String issue = "";
       String message = "";
       boolean isIOException = IOException.class.isAssignableFrom(ex.getCause().getClass());

        if (isIOException) {
                issue =  "Most likely an error converting stream to object. ";
                message = issue + ex.getMessage();
                throw initClientException(ex, message, responseCode);
        }
        else throw initClientException(ex, ex.getMessage(), responseCode);
    }
    
    private HttpConnectException  initConnectException(Throwable cause, 
            String message, Boolean recoverable) {
        
        String friendly =  "A server-side connection problem has occurred. " ;
        
        friendly = recoverable == true ? friendly + "A retry may be possible. "
                : friendly + "Please contact support. ";
        
        return new HttpConnectException(cause, message, recoverable, friendly,
            this.invClass.getCanonicalName() + "#" + this.invMethod);
    }
        
    private HttpClientException initClientException(Throwable cause, 
            String message, int responseCode) {
        
        String friendly = "A server-side problem has occurred. Please contact support. ";
        
        String debug = entity == null ? "Entity is null or consumed with error" : entity;
        
        message += " entity = " + debug + " content = " + contentType;
        
        HttpClientException client =  new HttpClientException(cause, message, friendly, 
                this.invClass.getCanonicalName() + "#" + this.invMethod );
        
        client.setResponseCode(responseCode);
        client.setTextMessage(this.contentType);
       
        client.setDebug(debug);
        return client;
    }
    
}
