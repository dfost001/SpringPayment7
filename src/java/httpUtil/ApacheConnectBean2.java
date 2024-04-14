/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.net.ssl.SSLException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dinah3
 */
@Component
@Scope("session")
public class ApacheConnectBean2 implements Serializable {
    
    private CloseableHttpClient client = null;    
    
    public static final String AcceptDefault = 
            "application/xml,application/json,text/plain,"
            + "text/xml,text/json,"
            + "text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2";
    
    private String accept = "";
    private String authProp = "";
    private int responseCode = -1;
    private String responseText = "";
    private Map<String, List<String>> headers = null;
    private final String SPC = Character.valueOf((char)32).toString();
    private String module = "";    
    private RequestConfig config = null;
    
    private boolean testError = false;

    /** Creates a new instance of ApacheConnectBean */
    public ApacheConnectBean2() {
        module = this.getClass().getName();
        config = RequestConfig.custom().setRedirectsEnabled(true)
                .setConnectionRequestTimeout(20000)
                .setConnectTimeout(20000)
                .setSocketTimeout(20000)
                .build();
        client = CustomHttpClient.buildCustomClient(); //Smarty Streets-Lets Encrypt bug-fix
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setBasicAuthProp(String user, String secret) {
        String toEncode = user + ":" + secret;
        byte[] bytes = toEncode.getBytes();
        byte[] encoded = Base64.encodeBase64(bytes);
        String sEncoded = new String(encoded);
        this.authProp = "Basic" + SPC + sEncoded;
    }
    
    public void setBearerAuthProp(String token){
        this.authProp = "Bearer" + SPC + token;
    }    

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
    
      private void initProperties(){
         
         responseCode = -1;
         responseText = "";
         headers = null;
         
    }
    
    public byte[] doFormUrlEncodedPost(String path, Map<String,String> pair)
                  throws HttpConnectException {          
       
        this.initProperties();
        HttpPost post = new HttpPost(path);
        post.setConfig(config);
        List<NameValuePair> nv = new ArrayList<>();
        for(String key : pair.keySet()) {
            nv.add(new BasicNameValuePair(key, pair.get(key)));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nv, "UTF-8"));
        }
        catch(UnsupportedEncodingException unex) {
           String display = "Application Exception (UnsupportedEncodingException)" ;
           throw new HttpConnectException(unex, unex.getMessage(),
                   Boolean.FALSE,
                   display, module + "#doUrlEncodedFormPost");
           
        }
        this.setRequestHeaders(post, "application/x-www-form-urlencoded");
        
        return this.doExecute(post, "doUrlEncodedFormPost"); 
        
        
    }
    
    public byte[] doConnectPost(String path, byte[] entityBody, String contentType)
            throws HttpConnectException {      
       
        this.initProperties(); //responseCode, httpHeaders are reset
        HttpPost post = new HttpPost(path); 
        post.setConfig(config);//redirect enabled, connect/socket time-outs set
        HttpEntity entity = EntityBuilder.create().setBinary(entityBody).build();
        post.setEntity(entity);  
        this.setRequestHeaders(post, contentType);   
        return this.doExecute(post,"doConnectPost");       
        
    }
    
      public byte[] doConnectDelete(String path) throws HttpConnectException {   
      
        this.initProperties();
        HttpDelete delete = new HttpDelete(path);
        this.setRequestHeaders(delete, null);   
        return this.doExecute(delete,"doConnectDelete");       
        
    }
    
    public byte[] doConnectGet(String path) throws HttpConnectException {        
        
        this.initProperties();
        HttpGet get = new HttpGet(path);
        get.setConfig(config);
        this.setRequestHeaders(get, null);   
        return this.doExecute(get,"doConnectGet");
       
    }  
   
   private void setRequestHeaders(HttpRequestBase request, String contentType){
        if(!this.authProp.isEmpty())
            request.setHeader("Authorization", authProp);
        if(this.accept.isEmpty())
            this.accept = AcceptDefault;
        request.setHeader("Accept", this.accept);
        if(contentType != null && !contentType.isEmpty())
            request.setHeader("Content-Type",contentType);
    }
   
   /*
    * Note: This method will throw an error if CloseableHttpResponse is null
    */
   private byte[] doExecute
           (HttpUriRequest request, String method ) throws HttpConnectException {
               
        byte[] bytResult = null;      
      
               
       /* if(this.client == null)
            client = HttpClients.createDefault();  */
       
       if(this.client ==null)
           client = CustomHttpClient.buildCustomClient();
       
        CloseableHttpResponse resp = null;        
       
        try {           
            
            resp = client.execute(request);
            
            if(resp == null)
                throw new IOException("client.execute returned a null CloseableHttpResponse");
            
            this.responseCode = resp.getStatusLine().getStatusCode();
            
            this.responseText = resp.getStatusLine().getReasonPhrase();
            
            this.getHeaderMap2(resp);
            
            bytResult = this.getBinaryResult(resp);
        }
         catch(ConnectTimeoutException timeEx){
            String display = "Temporary  error: Please retry. (ConnectTimeoutException)";
            client = null;
            throw new HttpConnectException(timeEx,
                    timeEx.getMessage(),
                    Boolean.TRUE,
                    display,
                    this.module + "#" + method);
        }
        catch(UnknownHostException hostEx){ //java.net
            String display = "May be possible to retry. (UnknownHostException)";
            client = null;
            throw new HttpConnectException(hostEx,
                    hostEx.getMessage(),
                    Boolean.TRUE,
                    display, 
                    this.module + "#" + method);
        }
        catch(SocketException socketEx) {
            String display = "Temporary  error: Please retry. (SocketException)";
            client = null;
            throw new HttpConnectException(socketEx,
                    "(See Bind, Connect, NoRoute, PortUnreachable) : " + socketEx.getMessage(),
                    Boolean.FALSE,
                    display,
                    this.module + "#" + method);
        }
        catch(SocketTimeoutException timeEx ){
            String display = "Temporary  error: Please retry. (SocketTimeoutException)";
            client = null;
            throw new HttpConnectException(timeEx,
                    timeEx.getMessage(),
                    Boolean.FALSE,
                    display,
                    this.module + "#" + method);
        }
       
        catch(SSLException ssl){ //java.net
            String display = "Application Error (SSLException)";
            client = null;
            throw new HttpConnectException(ssl,
                    ssl.getMessage(),
                    Boolean.FALSE,
                    display, 
                    this.module + "#" + method);
        }
        catch(ClientProtocolException protEx){
            String display = "Application Error (Http usage:ClientProtocolException)";
            client = null;
            throw new HttpConnectException(protEx,
                    protEx.getMessage(),
                    Boolean.FALSE,
                    display, 
                    this.module + "#" + method);
        }
       catch(IOException ioEx){
            String display = "Processing Exception: (IOException)";
            client = null;
            throw new HttpConnectException(ioEx,
                    ioEx.getMessage(),
                    Boolean.FALSE,
                    display, 
                   this.module + "#" + method);
        }
        catch (Exception ex){
            String display = "Application Error (Possibly: CloseableHttpResponse is null)";
            client = null;
            throw new HttpConnectException(ex,
                    ex.getMessage(),
                    Boolean.FALSE,
                    display, 
                    this.module + "#" + method);
        }
        
        finally {
           try { 
            if(resp != null)
                resp.close();
           } catch (IOException e){
                 client = null;
             }
        } //end finally
        
        
        return bytResult;
   }
   
   private byte[] getBinaryResult(CloseableHttpResponse resp) throws IOException {
       byte[]result = null;
       HttpEntity entity = resp.getEntity();
       if(entity == null)
           throw new IOException("CloseableHttpResponse#getEntity returned a null HttpEntity");
       try {
           result = EntityUtils.toByteArray(entity);
           EntityUtils.consume(entity);
       }
       catch(IOException ioex){           
           String message = this.getClass().getSimpleName() + "#getBinaryResult: "
                   + "Unable to convert entity to byte array." ;
           throw new IOException(message, ioex);
           
       }
       return result;
   }
   
   private void getHeaderMap2(HttpResponse resp){
        this.headers = new HashMap<>();
        Header[] clientHeaders = resp.getAllHeaders();
        for (Header h : clientHeaders){
           ArrayList<String> list = new ArrayList<>();
           String name = h.getName();
           String value = h.getValue();
           list.add(value);
           headers.put(name,list);
        }
        ArrayList<String> status = new ArrayList();
        status.add(resp.getStatusLine().getStatusCode() + ":" 
                + resp.getStatusLine().getReasonPhrase());
        headers.put(null, status);
        
    }
}
