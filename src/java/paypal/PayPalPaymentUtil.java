/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paypal;

import com.cart.Cart;
import com.google.gson.FieldNamingPolicy;
import error_util.EhrLogger;
import httpUtil.ApacheConnectBean2;
import httpUtil.HttpClientException;
import httpUtil.HttpConnectException;
import httpUtil.HttpException;
import httpUtil.ResponseUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import model.customer.Customer;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pp_payment.Link;
import pp_payment.PayPalError;
import pp_payment.Payer;
import pp_payment.Payment;
import pp_payment.Payment.PaymentState;
import pp_payment.RedirectUrls;
import pp_payment.Resource;
import pp_payment.Sale;
import pp_payment.TokenResponse;
import pp_payment.Transaction;

/**
 *
 * @author dinah
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class PayPalPaymentUtil implements Serializable{
    
    private final String PP_USER = "ARt7ym3dxD6vqMtd6KBUyAYatlyNBvcRKy95Zse8IJAw3RyvF5KP9wn5Ah9vZIzAp-0XYnVi7sRkqywJ";
    private final String PP_SECRET = "ELbyssfOB9dTF90l8r1dSvAzyWwCQicWKg2qGU-CtjMWua-WYyy9n6yGw0Km9SKd19e9V3_xhS9Q-XQO";
    private final String BAD_SECRET = "hello";
    
    private final String baseUrl = "https://api.sandbox.paypal.com/";
    private final String authPath = "v1/oauth2/token"; 
    private final String payRequestPath = "v1/payments/payment";     
    
    @Autowired
    private ApacheConnectBean2 apache;  
   
   /*
    * Abstract components are not scanned. Restriction does not apply
    * to @Bean or explicitly registered components.
    * 
    * @Lookup("paymentInitialize2")
    * protected abstract PaymentInitialize2 createPaymentInitialize();
    */
    
    private AuthCredential getAuthCredential(){
        
        AuthCredential credential = new AuthCredential();
        
        InputStream is = null;
        try {
            
            is = this.getClass().getResourceAsStream("paypal-cred.properties");
            Properties props = new Properties();
            props.load(is);
            credential.setUser(props.getProperty("user"));
            credential.setSecret(props.getProperty("secret"));
           
        } catch (IOException e) {
            String message = EhrLogger.doError(this.getClass().getCanonicalName(), 
                    "getAuthCredential", "Unable to load credential properties file");
            throw new IllegalArgumentException(message, e);
        }
        finally {
            if(is != null)
                try {
                  is.close();
                } catch (IOException e){}
        }
        return credential;
    }
    
    public TokenResponse doAuthorize() 
        throws HttpConnectException, HttpClientException, 
               PayPalExecuteException {    
       
              
        AuthCredential credential = this.getAuthCredential();

        apache.setBasicAuthProp(credential.getUser(), credential.getSecret());
      
       //apache.setAuthProp(PP_USER, BAD_SECRET); //test
        
        apache.setAccept("application/json");
        
        String path = baseUrl + this.authPath;
        
        HashMap<String,String> map = new HashMap();
        
        map.put("grant_type", "client_credentials");       
        
        byte[] jsonBytes = apache.doFormUrlEncodedPost(path, map); //throws HttpConnectException
        
        TokenResponse tmpTokenResponse = (TokenResponse)this.responseUtilEvaluate(jsonBytes, 
                TokenResponse.class, 
                PayPalError.class,
                "doAuthorize"); //throws HttpClientException
        
        this.evalTokenResponse(tmpTokenResponse, jsonBytes); //throws PayPalExecuteException
        
        return tmpTokenResponse;
      
     }//end authorize     
     
    public Payment doPaymentLogin(TokenResponse tokenResponse, 
            Cart cart, 
            PostalAddress deliveryAddress,
            RedirectUrls redirectUrls)
              throws HttpClientException, HttpConnectException, PayPalExecuteException {
        
        PaymentInitialize2 init = new PaymentInitialize2();
        
        init.setRedirectUrls(redirectUrls);
        
        Payment paymentRequest = init.initialize(Payer.PaymentMethod.paypal, deliveryAddress, cart);
        
        Payment paymentResponse = this.doPayment(paymentRequest, 
                tokenResponse, 
                "doPaymentLogin", PaymentState.created);          
        
        return paymentResponse;
        
    }
     private Payment doPayment(Payment paymentRequest, 
              TokenResponse tokenResponse,
              String method, PaymentState expectedState )
              
         throws HttpClientException, 
                HttpConnectException, 
                PayPalExecuteException {
         
         String json = ResponseUtil.toJson(paymentRequest, 
                 FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
         
         this.evalToJson(json);        
         
         String endpoint = this.baseUrl + payRequestPath;
         
         apache.setBearerAuthProp(tokenResponse.getAccessToken());
         
         apache.setAccept("application/json");
         
         byte[] jsonBytes = apache.doConnectPost(endpoint, json.getBytes(), "application/json");        
       
         Payment tmpPayment = (Payment)this.responseUtilEvaluate(jsonBytes,
                 Payment.class, PayPalError.class, method);  
         
         this.evaluatePayment(tmpPayment, expectedState, method, jsonBytes);  
         
         this.evalLinks(tmpPayment.getLinks(), "redirect");
         
         return tmpPayment;
     }  
     
    
    public Payment doFinalExecute(TokenResponse tokenResponse, 
            Payment payment, String payerId) 
            throws HttpConnectException, HttpClientException,
                   PayPalExecuteException {
        
         String endpoint = this.getExecuteEndpoint(payment);
            
         System.out.println("PayPalPaymentUtil#doFinalExecute:link=" + endpoint);

         String json = "{\"payer_id\": \"" + payerId + "\"}";
         
        apache.setAccept("application/json");
         
        apache.setBearerAuthProp(tokenResponse.getAccessToken());
        
        byte[] jsonBytes = apache.doConnectPost(endpoint, json.getBytes(), "application/json");
        
        Payment response = (Payment) this.responseUtilEvaluate(jsonBytes,
                Payment.class,
                PayPalError.class,
                this.getClass().getSimpleName() + "#doFinalExecute");
        
       // this.assignDebugValues(response); 
         
        this.evaluatePayment(response, 
                Payment.PaymentState.approved,
                "doFinalExecute",
                jsonBytes);
        
        return response;
        
    }
    
    
      private Object responseUtilEvaluate(byte[] json, 
              Class<?> responseType, Class<?> errType, String method) throws HttpClientException{
          
           ResponseUtil util = new ResponseUtil(apache.getResponseCode(),
                apache.getHeaders(),
                json,
                this.getClass().getSimpleName() + "#" + method);
           
           util.setHandlerValidateErrorObject(new HandlerEvaluateErrorObject());
           
            Object obj = util.processResponse_JSON(
                 responseType,
                 errType,
                 FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);             
            
            return obj;          
      }
      
      private void evalTokenResponse(TokenResponse tmpTokenResponse, byte[] response) throws PayPalExecuteException {
         
         String technical = "";
         
         if(tmpTokenResponse == null)
             technical = "Deserialized token object is null";
         else {
             String token = tmpTokenResponse.getAccessToken();
             if(token == null || token.isEmpty())
                 technical = "accessToken in TokenResult object is null or empty. " +
                         "May be deserialization error. Is object defined correctly?";
         }       
         
         if(!technical.isEmpty())
             this.throwPayPalExecuteException(technical, "evalTokenResponse", response);
         
         System.out.println("PayPalPaymentUtil#evalTokenResponse: success: " +
                 tmpTokenResponse.toString());
     } 
      
     private void evaluatePayment(Payment tmpPayment,
             Payment.PaymentState stateRequired, 
             String method,
             byte[] httpResponse) throws PayPalExecuteException {        
         
         String technical = "";
         
         if (tmpPayment == null) {
             technical = "Deserialized payment object is null";
         } else {
             
             Payment.PaymentState state = tmpPayment.getState();
             
             if (!state.equals(stateRequired)) {
                 technical = "Payment state is not '"
                         +  stateRequired + "' but equal to '"
                         +  state + "'. ";
             }
             
             if (tmpPayment.getId() == null || tmpPayment.getId().trim().isEmpty()) {
                 technical += "PayPal resource ID is not initialized in Payment object.";
             }
             
             if(stateRequired.equals(Payment.PaymentState.approved)) {
                 String err = this.extractTransactionId(tmpPayment);
                 if(err != null)
                     technical += err;
             }
            
         } //end else             
         
         if(!technical.isEmpty())
             this.throwPayPalExecuteException(technical,method, httpResponse);
         
     } //end evaluate       
       
     
     private void evalToJson(String json) throws PayPalExecuteException {
         
         if(json == null || json.isEmpty()) {
             
             String msg = EhrLogger.doError(this.getClass().getCanonicalName(),
                             "evalToJson", "Payment serialized to String is null or empty");
             
             throw new PayPalExecuteException(msg, "evalToJson");
         }           
             
     }

     
     private String getExecuteEndpoint(Payment payment) throws PayPalExecuteException {
         
        if(payment == null) 
            throw new IllegalArgumentException(
                    EhrLogger.doError(this.getClass().getCanonicalName(), 
                            "getExecuteEndpoint", "Payment object is null")
            );            
        
        List<Link> links = payment.getLinks();
        
        this.evalLinks(links, "Post");
        
        String endpoint = "";
        
        for(Link link : links)
            if(link.getMethod().equalsIgnoreCase("Post")){
                endpoint = link.getHref();
                break;
            }
        System.out.println("PayPalPaymentUtil#getExecuteEndpoint: " + endpoint);
        return endpoint;
    }
     
     private void assignDebugValues(Payment payment) {
         payment.setState(Payment.PaymentState.failed);
         payment.setId("");
         List<Resource> list = payment.getTransactions().get(0)
                                        .getRelatedResources();
         if(!list.isEmpty())
          list.get(0).getSale().setId("");
         
     }
     
     private String extractTransactionId(Payment payment) {
         
         //String err = "";
         
         List<Transaction> listTransaction = payment.getTransactions();
         if(listTransaction.isEmpty()) {
            String err = "Payment#ArrayList<Transaction> is empty. ";
            return err;
         }
         List<Resource> resourceList = listTransaction.get(0)
                     .getRelatedResources();
         if(resourceList.isEmpty()) {
            String err = "Transaction#ArrayList<Resource> is empty. ";
            return err;
         }
         Sale sale = resourceList.get(0).getSale();
         if(sale == null){
             String err = "Resource#Sale is null. ";
             return err;
         }
         String transId = sale.getId();
         if(transId == null || transId.trim().isEmpty()){
             String err = "Final Transaction Id at Sale#id is empty";
             return err;
         }
         return null;         
     }
     /*
      * To do: Error check link for non-null and token
      * redirectUrl=https://www.sandbox.paypal.com/cgi-bin/webscr?
      * cmd=_express-checkout&token=EC-9KA84419PW545873G  
      */ 
     private void evalLinks(List<Link> links, String method) 
          throws PayPalExecuteException {
         
         String endpoint = "";
         
         if(links == null || links.isEmpty())
             this.throwPayPalExecuteException("evalLinks",
                     "Links field of Payment is null at " + method,
                     null);
         
         for(Link link : links)
            if(link.getMethod().equalsIgnoreCase(method)){
                endpoint = link.getHref();
                break;
            }
         if(endpoint.isEmpty())  
             
             this.throwPayPalExecuteException("evalLinks",
                     "Payment#links does not contain endpoint URL for " + method,
                     null);
     }
     
     private void throwPayPalExecuteException(String method, String message, byte[] httpResponse)
         throws PayPalExecuteException {     
         
         String handler = this.getClass().getCanonicalName() + "#" + method;
         
          PayPalExecuteException e = new PayPalExecuteException(message, handler);
          
          String result = httpResponse == null ? null : new String(httpResponse) ;
          
          e.setHttpEntity(result);
          
          throw e;
     }
     
      public Payment doPaymentDirect(
            Cart cart,
            Customer customer,
            PostalAddress deliveryAddress) 
        throws HttpException, PayPalExecuteException {
        
        TokenResponse tokenResponse = this.doAuthorize();
        
        PaymentInitialize2 init = new PaymentInitialize2();
        
        init.setBillingCustomer(customer);
        
        Payment paymentRequest = init.initialize(Payer.PaymentMethod.credit_card, 
                deliveryAddress, cart);
        
        Payment paymentResponse = this.doPayment(paymentRequest, 
                tokenResponse, "doPaymentDirect", PaymentState.approved);         
       
        
        return paymentResponse;
        
    }
} //end util
