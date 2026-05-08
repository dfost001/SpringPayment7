/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;

import error_util.EhrLogger;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.TrustStrategy;

/**
 *
 * @author dinah
 */
public class MyTrustStrategy implements TrustStrategy{
    
   // private String Issuer = "ISRG Root X1";
    
    private String errSubject = "smartystreets" ;
    
    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) {       
        
       /*  for(X509Certificate cert : chain) {
             
	    EhrLogger.printToConsole(MyTrustStrategy.class, "isTrusted",
            "Subject=" + cert.getSubjectX500Principal().getName());

            EhrLogger.printToConsole(MyTrustStrategy.class, "isTrusted",
            "Issuer=" + cert.getIssuerX500Principal().getName());
            
         }*/
       
       
       
      // Fixed by setting -Djavax.net.ssl.trustStore=
      //C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot\jre\lib\security\cacerts
       
       /* if (chain != null && chain.length > 0) {
            X509Certificate leaf = chain[0];   
            
            String subject = leaf.getSubjectX500Principal().getName();

            EhrLogger.printToConsole(MyTrustStrategy.class, "isTrusted",
                "leaf subject=" + subject);

            if (subject.contains(errSubject)) 
                return true;
        } */
       
        debugTrustStore();
        return false;          // use default Java trust validation 
    }   
    
     private void debugTrustStore() {
	   System.out.println("ApacheConnectBean2 > doExecute > debugTrustStore");
	   
	   System.out.println("java.home="
		        + System.getProperty("java.home"));

		System.out.println("javax.net.ssl.trustStore="
		        + System.getProperty("javax.net.ssl.trustStore"));

		System.out.println("javax.net.ssl.trustStoreType="
		        + System.getProperty("javax.net.ssl.trustStoreType"));

		System.out.println("javax.net.ssl.trustStorePassword="
		        + System.getProperty("javax.net.ssl.trustStorePassword"));
   }
   
}//end class