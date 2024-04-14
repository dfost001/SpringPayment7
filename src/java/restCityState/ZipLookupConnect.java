/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCityState;


import error_util.EhrLogger;
import httpUtil.ResponseUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.net.URLEncoder;

import org.springframework.validation.Errors;

import com.google.gson.FieldNamingPolicy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import httpUtil.ApacheConnectBean2;
import httpUtil.HttpClientException;
import httpUtil.HttpConnectException;
import httpUtil.HttpException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import restCityState.client.LookupResponse;
import restCityState.client.LookupResponse.Status;
import restCityState.client.CityStates;
import restCityState.client.Zipcodes;

/**
 *
 * @author dinah
 */
@Component

public class ZipLookupConnect {
    
    private String authId;
    
    private String authToken;
    
    private final String endpoint = "https://us-zipcode.api.smartystreets.com/lookup";
    
   /* @Autowired
    private ApacheConnectBean2 connect; */
    
    public ZipLookupConnect() {
        
        readCredentials();
    }
    
    private void readCredentials() {
        
        Properties properties = new Properties();
        
        String path = "/restCityState/client/credentials.properties" ;
        
        InputStream is = this.getClass().getResourceAsStream(path);
        
        try {
            properties.load(is);
        } catch (IOException ex) {
            
            throw new IllegalArgumentException (
                    EhrLogger.doError(this.getClass().getCanonicalName(), 
                            "readCredentials", "Unable to load credentials"),
                    ex);
        }
        
       authId = properties.getProperty("auth-id");
       
       // authId = "Dinah" ; 
        
        authToken = properties.getProperty("auth-token");
    }
    
    public void validateCityStateZip(String city, String stateCode, String zipCode, Errors errors) 
            throws HttpException, LookupInitializationException {
        
        debugPrint("validateCityStateZip", "city=" + city ) ;    
        
        String url = assignAuthParameters(this.endpoint);
        
        url = assignFieldParameters(url, city, stateCode);
        
        LookupResponse[] response = this.doGet(url);        
        
        new CityZipService().processLookup(response[0], zipCode, city, stateCode, errors);       
        
    }
   
    private String assignAuthParameters(String url) {
        
        url += "?" ;
        
        url += "auth-id=" + this.authId;
        
        url += "&" ;
        
        url += "auth-token=" + this.authToken;
        
        return url;
    } 
    
    private String assignFieldParameters(String url, String city, String stateCode) {
        
        String encodedCity = "";
        
        try {
            
           encodedCity = URLEncoder.encode(city, "UTF-8");
           
        } catch (UnsupportedEncodingException e) {
            
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        
        String encUrl = url + "&city=" + encodedCity;
        
        encUrl += "&state=" + stateCode;
        
        return encUrl;
    }
    
    private LookupResponse[] doGet(String url) 
            throws HttpConnectException, HttpClientException {
        
        ApacheConnectBean2 connect = new ApacheConnectBean2();
        
        connect.setAccept("application/json");
        
        byte[] resultBytes = connect.doConnectGet(url);
        
        ResponseUtil util = new ResponseUtil(connect.getResponseCode(),
                connect.getHeaders(), resultBytes, 
                this.getClass().getCanonicalName() + "#" + "doGet");
        
        LookupResponse[] lookupResponse =
                (LookupResponse[])util.processResponse_JSON(LookupResponse[].class, null, 
                        FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES); 
        
        evalLookupResponse(lookupResponse);
        
        return lookupResponse;
        
    }
    
      private void evalLookupResponse(LookupResponse[] lookUp) {
        
        String title = "ZipLookupConnect#debug: ";
        String err = "";
        
        if(lookUp == null || lookUp.length == 0) {
           err = "LookupResponse[] is null or empty";
           
        }
        
        LookupResponse response = lookUp[0];
        
        List<Zipcodes> zips = response.getZipcodes();
        
        List<CityStates> cityStates = response.getCityStates();
        
        Status status = response.getStatus();
        
        String statusLine = status == null ? "Success" : response.getReason();
        
        if(statusLine.equals("Success")) {
            if(zips == null || zips.isEmpty())
               err = "City-State is valid and List<Zipcodes> is null or empty. " ;
            if(cityStates == null || cityStates.isEmpty())
               err += "City-State is valid and List<CityStates> is null or empty. " ;
        }
        
        if(!err.isEmpty())
            throw new LookupInitializationException(title + err);
    }
    
    private LookupResponse[] deserializeJson(String json) {
        
        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        try {
            
          LookupResponse[] lookup = gson.fromJson(json, LookupResponse[].class);
          return lookup;
          
        } catch (JsonSyntaxException e) {
            System.out.println(e.getClass().getCanonicalName() + ": " + e.getMessage());
        }
        
        return null;
    }
    
    private void debugPrint(LookupResponse[] lookUp) {           
      
       
       if(lookUp == null || lookUp.length == 0) {
           System.out.println("ZipLookupConnect#debugPrint: response list is null");
           return;
       }
       
       LookupResponse response = lookUp[0];
        
       Status status = response.getStatus();
       String statusLine = status == null ? "Success" : response.getReason();
       System.out.println("status=" + statusLine);
       
       List<Zipcodes> list = response.getZipcodes();
       String zipcodesLine = list == null 
               ? "Zipcodes is null" : "Zipcodes = " + list.size();
       
       System.out.println(zipcodesLine);     
       
       List<CityStates> cityList = response.getCityStates();
       
       System.out.println("CityStates=" + cityList.size());    
       
       if(cityList != null && !cityList.isEmpty())
           System.out.println("City = " + cityList.get(0).getCity());
       
       if(list != null) {
         for(Zipcodes zip : list)
           System.out.println("zip=" + zip.getZipcode());
       }
    }
    
  
    
    private void debugErrors(Errors errors) {
        
        for(FieldError err : errors.getFieldErrors()) {
            
            System.out.println(err.getField() + "=" + err.getDefaultMessage());
        }
    }
    
    private void debugPrint(String method, String message) {
        
        String line = this.getClass().getCanonicalName()
                + "#" 
                + method + ": "
                + message;
        
        System.out.println(line);
    }
    
   
                
}  //end class



