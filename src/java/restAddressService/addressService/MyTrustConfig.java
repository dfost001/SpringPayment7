/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService.addressService;

import error_util.EhrLogger;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 *
 * @author dinah
 */
public class MyTrustConfig {
    
    public static Client config() {
        
        TrustManager[] trustManager = getTrustManager();
        
        SSLContext ctx = null;
        
        try {
            
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustManager, new SecureRandom());
            
        } catch (java.security.GeneralSecurityException e) {
           
           EhrLogger.throwIllegalArg(MyTrustConfig.class.getCanonicalName(), "config",
                   e.getMessage(), e);
        }
        
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        
        HostnameVerifier nameVerifier = getHostnameVerifier();
        
        ClientBuilder builder = ClientBuilder.newBuilder();
        
        builder.hostnameVerifier(nameVerifier).sslContext(ctx);
        
        Client client = builder.build();
        
        return client;
        
    }          
 
    
    private static TrustManager[] getTrustManager() {
        
        TrustManager[] manager = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        
        return manager;
    }
    
    private static HostnameVerifier getHostnameVerifier() {
        
        HostnameVerifier verifier = new HostnameVerifier() {
            
            @Override
            public boolean verify(String string, SSLSession ssls) {
                return true; 
            }
            
        };
        return verifier;
    }
    
} //end class
