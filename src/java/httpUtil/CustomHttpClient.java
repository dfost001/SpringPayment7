/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;

import error_util.EhrLogger;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author dinah
 */
public class CustomHttpClient {
    
    private static String path = "C:\\Program Files\\Java\\jdk1.8.0_191\\jre\\lib\\security\\cacerts";   
    
    public static CloseableHttpClient buildCustomClient()  {
        
        CloseableHttpClient  client = null;
        
        try {
            
            KeyStore keyStore = loadKeyStore();
            
            
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(keyStore, new MyTrustStrategy())
                    .build();
            
            client = HttpClients.custom()
                    .setSslcontext(sslContext)
                    .setHostnameVerifier(SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER)
                    .build();
            
            
            
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            
            EhrLogger.throwIllegalArg("CustomHttpClient", 
                    "buildCustomClient", e.getMessage(), e);
        }        
       return client; 
    }
    
    private static KeyStore loadKeyStore() {
        
        KeyStore keystore = null;
	 
	 String password = "changeit";
	 
	    try {
	    	
	    	FileInputStream is = new FileInputStream(path);

	   	    keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	    	
		    keystore.load(is, password.toCharArray());
			
		} catch (NoSuchAlgorithmException | CertificateException
				| IOException | KeyStoreException e) {
			
			
			EhrLogger.throwIllegalArg("CustomHttpClient", 
                                "loadKeyStore", e.getMessage(), e);
		}	    

	    return keystore;
   }
   
        
    }
    

