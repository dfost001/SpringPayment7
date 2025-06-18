
package httpUtil;

import java.io.StringReader;
import java.nio.charset.CharacterCodingException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import java.io.StringWriter;

import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import com.google.gson.*;


/**
 *
 * @author Dinah3
 * Not thread safe -- construct for each use
 * To Do: Static method for return header value
 */

public class ResponseUtil {
    
        
    private Class<?> errType = null;
    private Class<?> resultType = null;
    private int respCode = -1;
    private Map<String,List<String>> headers = null;
    private byte[] response = null;
    private String jaxbPackage = "";
    private String invokingMethod = "";
    private IValidateErrorObject handlerValidateErrorObject;
    
    @SuppressWarnings("rawtypes")
	private Class[] jaxbContext = null;
    
    private String decodedEntity = "";
    
    private Boolean isXML;
    
    private com.google.gson.FieldNamingPolicy namingPolicy;
    
    public ResponseUtil(int respCode, 
            Map<String,List<String>> headers, 
            byte[] httpEntity,
            String invokingMethod){
        this.respCode = respCode;
        this.headers = headers;
        this.response = httpEntity;
        this.invokingMethod = invokingMethod;
    }

    public IValidateErrorObject getHandlerValidateErrorObject() {
        return handlerValidateErrorObject;
    }

    public void setHandlerValidateErrorObject(IValidateErrorObject handlerValidateErrorObject) {
        this.handlerValidateErrorObject = handlerValidateErrorObject;
    }
    
    

       
    public Object processResponse_XML(Class<?>responseType, Class<?> perrType,
            String pjaxbPackage) throws HttpClientException {
        this.errType = perrType;
        this.resultType = responseType;
        this.jaxbPackage = pjaxbPackage;
        this.isXML = true;
        Object o = this.processResponseCode();
        return o;
        
    }
    
    @SuppressWarnings("rawtypes")
	public Object processResponse_XML(Class<?>responseType, Class<?> perrType,
            Class[] pjaxbContext) throws HttpClientException {
        this.errType = perrType;
        this.resultType = responseType;
        this.jaxbContext = pjaxbContext;
        this.isXML = true;
        Object o = this.processResponseCode();
        return o;
        
    }
    
    public Object processResponse_JSON(Class<?>responseType, 
            Class<?>perrType,
            FieldNamingPolicy pnamingPolicy) throws HttpClientException {
        this.errType = perrType;
        this.resultType = responseType;
        this.isXML = false;
        this.namingPolicy = pnamingPolicy;
        Object o = processResponseCode();
        return o;
    }

    public void processResponse_NoContent() throws HttpClientException {
        processResponseCode();
    }
    
    public String processResponse() throws HttpClientException {
        Object o = processResponseCode();
        return (String)o;
    }
    
   private Object processResponseCode() throws HttpClientException{
       
       decodedEntity = "";
       Object err = null;
       if(response == null || response.length == 0 ) {           
                 
           decodedEntity = "";
       
       } else {
           
           this.decodeHttpEntity();  //nio.charset.Decoder, throws HttpClientException
          
       }   
       if(this.respCode == 204)
           return decodedEntity;
       
       String type = this.findContentType(); //returns content-type header
       
       String edited = "";
       
       if(this.respCode == 200 || this.respCode == 201) {
           
           System.out.println("ResponseUtil#status is OK");
           
           if(isXML && this.resultType != null) { //Boolean set by exposed method
               edited = this.normalizeXmlDoc();
               return this.unmarshall(this.resultType,edited);
           }
           else if(!isXML && this.resultType != null)  
               return this.fromJSON(this.resultType);
           
           else return decodedEntity;
       }
       else if(this.respCode >= 400 && this.respCode < 500) {  
           
           System.out.println("ResponseUtil#status is " + respCode);
           
           if(isXML && this.errType != null && type.contains("xml")) {
               err = this.unmarshall(this.errType,decodedEntity);
               throw this.initResponseEx("processResponseCode", err);
           }
           else if(this.errType != null && type.contains("json")) {
        	err = this.fromJSONErrorType(this.errType);
                throw this.initResponseEx("processResponseCode", err);
           }
           else throw(this.initResponseEx("processResponseCode", null));
       }
       else {
           throw(this.initResponseEx("processResponseCode", null));
       }
      
   }
   
   private void decodeHttpEntity() throws HttpClientException {     
       
       if(response == null || response.length == 0){
           this.decodedEntity = "";
           return;
       }
       try {
           decodedEntity = new DecoderUtil2().decode(response);
       }
       catch(CharacterCodingException ex) {
           throw this.initUnmarshalEx(ex, "decodeHttpEntity");
           
       }
   }
   
   private String normalizeXmlDoc(){
       String edited = "";
       int pos = decodedEntity.indexOf("<?xml");
       if(pos == -1)
           return decodedEntity;
       edited = decodedEntity.substring(pos);
       return edited;
   }
    
    
    private  String makeDebuggingMsg(){
        
        String msg = "";  
              
        if(decodedEntity == null || decodedEntity.length() == 0)
            msg = "Empty http response";
        else msg = decodedEntity;      
            
        msg = headers.get(null).get(0) + ": " + msg;
       
        return msg;        
    }
    
    private  String makeErrorMsg_ParseHtml() {
        
        String msg = "";
              
        if(decodedEntity==null || decodedEntity.length() == 0){
            return "Empty http response entity";
        }            
                
        String type = this.findContentType();
        
        if(type.contains("html"))
            msg = ResponseUtil.parseHtmlBody(decodedEntity);
        
        else if(type.contains("text"))
            msg = decodedEntity;
        
        msg = this.makeReasonPhrase() + ": Status=" 
                + this.respCode
                + ": "
                + type
                + ": "
                + msg;
        
        return msg;
    }
    
   
    private String makeErrorText() {
            
        String msg = headers.get(null).get(0) + ": ";       
        
        if(this.response == null || this.response.length == 0)
            return  msg + " Empty http response entity";
              
        if(this.decodedEntity.isEmpty()){
            return  msg + " Response could not be decoded"; //Should be initialized
        }   
                  
        return this.findContentType();
      
    }
    /*
     * End-user message
     * To do: get Retry-After if present on 503
     */
    private String makeReasonPhrase(){
        String phrase = headers.get(null).get(0);
        if(respCode >= 400 && respCode < 500)
            phrase  = "Internal application error: " + phrase;
        else if(respCode == 503) {
            phrase = "Temporary remote service error: " + phrase;
        }
        else if(respCode >= 500 && respCode < 600)
            phrase = "Internal remote service error: " + phrase;
       
        return phrase;
    }
    
      
    /*
     * To do: get list of mime types, and iterate
     * To do: return no content-type header
     * 
     */
    public  String findContentType(){
       
       
        List<String> content = headers.get("Content-Type");
        if(content != null){
            return content.get(0);
       }
        else return "no-content-header";
        
       
    }
   
      
    /*
     * To do: Scan text until body tag is found using "<" as delimiter
     * Keep scanning until </body> is found
     * 
     */
    public static String parseHtmlBody(String result){
        String body = "";
        int posStart = result.indexOf("<body>");
        int posEnd = result.indexOf("</body>");
      
        if(posStart > -1){
          
           body = result.substring(posStart + 6, posEnd );
        }
        else body = result;
        return body;
    }
    
     private Object unmarshall(Class<?> t, String entity) throws HttpClientException {
    	 
    	 
    	 Object o = null; 
       
       if(entity == null || entity.isEmpty())
            throw initResponseEx("unmarshall", null);
              
       
        StringReader sr = new StringReader(entity);     
       
        
        JAXBContext context = null;
        
        try {
        	if(this.jaxbPackage.isEmpty())
        	   context = JAXBContext.newInstance(jaxbContext);
        	else
                   context = JAXBContext.newInstance(jaxbPackage);
        	
                Unmarshaller u = context.createUnmarshaller();
           
                o =  u.unmarshal(new StreamSource(sr)); 
            
                if(o.getClass() != t)
            	   throw new ClassCastException
                        ("Entity cannot be cast to" + t.getClass().getName());
            	
        	return o;
           
        } 
        catch (ClassCastException castEx) {
        	throw initUnmarshalEx(castEx, "unmarshal");
        }
        catch(JAXBException jex) {
        	throw initUnmarshalEx(jex, "unmarshal");
        }
        
    }
     
    private Object fromJSON (Class<?> t) throws HttpClientException{
        Object o = null;
        if(decodedEntity.isEmpty())
            throw new ClassCastException
                        ("Http entity is empty and cannot be cast to " + t.getCanonicalName());
        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(this.namingPolicy).create();
        try {
            o = gson.fromJson(decodedEntity, t);
            if (o == null) {
                throw new ClassCastException("Http entity cannot be deserialized to " + t.getCanonicalName());
            }
            t.cast(o);
        } catch (JsonSyntaxException | ClassCastException jex) {
            throw this.initUnmarshalEx(jex, "fromJSON");
        }
        return o;
    } 
    
    private Object fromJSONErrorType(Class<?> t) throws HttpClientException {
               
        Object o = null; 
        
        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(this.namingPolicy).create();
        try {
            if(!decodedEntity.isEmpty())
                o = gson.fromJson(decodedEntity, t);            
           
        } catch (JsonSyntaxException e) {
            throw initUnmarshalEx(e, "fromJSONErrorType");
        } 
        //If decodedEntity is empty or the deserialized object is null
        //or deserialization throws no error
        if(this.handlerValidateErrorObject != null)
            return this.processValidateErrorObject(o);
        try {
           if(o == null || decodedEntity.isEmpty())
              throw new ClassCastException("Http entity cannot be deserialized to " + t.getCanonicalName());    
        
           t.cast(o);
        } catch (ClassCastException e){
            
            throw initUnmarshalEx(e, "fromJSONErrorType");
        }
        
       return o;
    }
    
    private Object processValidateErrorObject(Object errObject)
            throws HttpClientException, RuntimeException{         
        
        
       String err = handlerValidateErrorObject.validate(
                    errObject, this.respCode, this.decodedEntity, this.invokingMethod);
        
        if (err != null && !err.isEmpty()) {
            
           DeserializationException ex = new DeserializationException(
                this.handlerValidateErrorObject.getClass().getCanonicalName() +
                        " returned with message: " + err);
           
           throw this.initUnmarshalEx(ex, "processValidateErrorObject");
        }
        return errObject;
    }
    
    public static String  toJson(Object o, FieldNamingPolicy policy)  {
       
       Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(policy).create();
       String json = gson.toJson(o);
       return json;
    }
    
    public static String marshal(JAXBElement<?> el, String context, Class<?>[] ctxArr) throws JAXBException{
        
        String xml = "";
        StringWriter sw = new StringWriter();
        JAXBContext ctx = null;
       
       try {
    	    if(ctxArr == null)
              ctx = JAXBContext.newInstance(context);
    	    else
    	    	ctx = JAXBContext.newInstance(ctxArr);
            Marshaller m = ctx.createMarshaller();
            m.marshal(el,
                    new StreamResult(sw));
            xml = sw.toString();
            return xml;
       } catch (JAXBException jaxbex){
           throw jaxbex;
       }

    }
     /*
     * JAXB or Json syntax error, ClassCast or Decode error
     */
     private HttpClientException initUnmarshalEx(Exception e, String method){         
         
         method = this.invokingMethod + " -> ResponseUtil#" + method;
         
         String type = e.getClass().getCanonicalName();
         
         String message =  type +  ":" + e.getMessage() ;
         
         String friendly = "Application Error";
         
         String debug = this.makeDebuggingMsg(); //Reason-Phrase + raw entity
         
         String text = this.makeErrorText(); //Content-Type
         
         HttpClientException ex = new HttpClientException(e,message,friendly, method);  
         
         ex.setDebug(debug);         
         ex.setTextMessage(text);
         ex.setResponseCode(respCode);
         
         return ex;
     }
     
     /* 
      * Note: text will duplicate message, if content is not html
      */
   private HttpClientException initResponseEx(String method,Object o)  {
       
        method = this.invokingMethod + " -> ResponseUtil#" + method;
        
        String friendly = this.makeReasonPhrase();
        
        String text = this.makeErrorText(); //Content-Type
        
        String message = this.makeErrorMsg_ParseHtml(); //Same as makeErrorText + html body
        
        String debug = this.makeDebuggingMsg(); //Raw response  
        
        HttpClientException ex = new HttpClientException(null,message,friendly,method);
        
        ex.setDebug(debug);
        ex.setTextMessage(text);
        ex.setErrObj(o);
        ex.setResponseCode(this.respCode);
        return ex;
   }
    
    
}//end class
