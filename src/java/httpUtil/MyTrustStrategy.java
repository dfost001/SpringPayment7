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
    
    private String Issuer = "ISRG Root X1";
    
    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) {
        
        boolean trusted = false;
        
         for(X509Certificate cert : chain) {
	    EhrLogger.printToConsole(MyTrustStrategy.class, "isTrusted",
            "Subject=" + cert.getSubjectX500Principal().getName());

            EhrLogger.printToConsole(MyTrustStrategy.class, "isTrusted",
            "Issuer=" + cert.getIssuerX500Principal().getName());
            
	   if(cert.getIssuerX500Principal().getName().contains(Issuer))
			   trusted =true;
	}  
         
        EhrLogger.printToConsole(MyTrustStrategy.class, "isTrusted", "trusted=" + trusted);
         
        // return trusted;
        
        return true;
    }
}
