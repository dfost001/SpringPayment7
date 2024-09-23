package restAddressService.addressService;

import restAddressService.client.Candidates;
import restAddressService.client.Request;
import httpUtil.HttpException;
import httpUtil.JerseyClientUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import restAddressService.AjaxRequest;
import restAddressService.addressService.converter.AddressResponseReader;

@Scope(WebApplicationContext.SCOPE_APPLICATION)
@Component
public class ServiceConnect {
  
	
    private static final String endpoint = "https://api.smartystreets.com/street-address"; 
		
    public String authId = "";
    
    public String authToken = "";
	
    private Request request;
    
    private final WebTarget target;    
    
     private void initCredentials() throws IOException{
    	String path = "/restAddressService/addressService/credentials.properties";
        //String badPath = "/messages/credentials.properties" ;
    	InputStream is = this.getClass().getResourceAsStream(path);
    	Properties prop = new Properties();
    	try {
    	   prop.load(is);
        } catch(IOException io){
    		throw new IOException("Unable to load address credentials:" + io.getMessage(),io);
    	}
    	this.authId = prop.getProperty("auth-id");        
    	this.authToken = prop.getProperty("auth-token");
        //this.authToken = "Foster"; -- test Unauthorized
        //this.authId = "Dinah"; -- test Unauthorized
    }
     
    
    public ServiceConnect() throws IOException {  

       // ClientConfig config = new ClientConfig();	 
		 
	//Client client = ClientBuilder.newClient();
        
        Client client = MyTrustConfig.config();
		 
	client.register(AddressResponseReader.class);
        
        this.initCredentials();
		 
	target = client.target(endpoint).queryParam("auth-id", authId)
                .queryParam("auth-token", authToken)
                .queryParam("license", "us-core-cloud");
        
    }   
    
    public SvcAnalysis processAddress(AjaxRequest ajaxRequest) 
            throws HttpException {         	
    	
    
        Candidates candidates = this.doConnect(ajaxRequest);      	
    	
    	SvcAnalysis postal = new AddressSvc(request).verify(candidates);    	
    	
    	return postal;	
    }
    
    private Candidates doConnect(AjaxRequest ajaxRequest) throws HttpException {
        
        Candidates candidates = null;
        
        Response response= null;
        
        //String entity = null;
        
        Invocation.Builder builder = target.request(MediaType.APPLICATION_XML)
                .header("Content-Type", MediaType.APPLICATION_XML);

        boolean buffered = false;
        
        try {
            Invocation inv = builder.buildPost(this.getEntity(ajaxRequest));
            
            System.out.println("ServiceConnect#doConnect: invoking the request" );
            
            response = inv.invoke();
            
            System.out.println("ServiceConnect#doConnect: response obtained" );
            
            buffered = response.bufferEntity();
            
            int status = response.getStatus();
            
            System.out.println("ServiceConnect#doConnect: response status=" + status );
            
            if(status != 200 && status != 201) {                
                
		throw new WebApplicationException("Response status not OK", response);
		
	    }
            
            candidates = response.readEntity(Candidates.class); //IOExceptions are wrapped inside of ProcessingException             
           
        }  catch (ProcessingException | WebApplicationException ex) {     
            
            String entity = null;
            if(response != null && buffered) {
                entity = response.readEntity(String.class);
            }
            
            System.out.println("ServiceConnect#doConnect:exception caught:" 
                    + ex.getClass().getCanonicalName() + ": " + ex.getLocalizedMessage());
            
            new JerseyClientUtil(this.getClass(),"doConnect")
                    .handleException(ex, response, entity);
                   
        } 
        
        return candidates;

    }
    
   
    
    private Entity getEntity(AjaxRequest ajax) {
        
        Request.Address addr = new Request.Address();
        
        String city = ajax.getCity() == null ? "" : ajax.getCity();
        String state = ajax.getState() == null ? "" : ajax.getState();
        String zip = ajax.getZipcode() == null ? "" : ajax.getZipcode();
        
    	addr.setCity(city);
    	addr.setState(state);
    	addr.setZipcode(zip);
    	addr.setStreet(ajax.getStreet());
        addr.setCandidates((short)10);
        addr.setMatch("invalid");
    	
    	request = new Request();          
        	
    	request.getAddress().add(addr);
        
        Entity<Request> entity = Entity.entity(request, MediaType.APPLICATION_XML);
        
      //  debugSerialEntity(entity);
		
	return entity;
    }
    
 /*  private void debugSerialEntity(Entity<Request> serEntity) {
       
       String err = "";
       
       Request debugReq = serEntity.getEntity();
       
       if(debugReq == null)
           err = "Request object is null";
       else if(debugReq.getAddress() == null || debugReq.getAddress().isEmpty())
           err = "List field is null";
       else if(debugReq.getAddress().get(0).getStreet() == null ||
              debugReq.getAddress().get(0).getStreet().isEmpty())
           err = "Street-line is empty" ;
       if(!err.isEmpty())
          EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), "debugSerialEntity", err);
   }*/
	
}//end class
