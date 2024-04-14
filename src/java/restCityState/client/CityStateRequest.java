/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCityState.client;

/**
 *
 * @author dinah
 * Not used and incorrectly defined
 * Request is an array of 3 objects (CityState, Zipcode, CityStateZip)
 */
public class CityStateRequest {
    
    private String city;
    
    private String state;
    
    private String zipcode;
    
    public CityStateRequest(){}

    public CityStateRequest(String city, String state, String zipcode) {
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }  
    
    public CityStateRequest(String city, String state) {
        this.city = city;
        this.state = state;
        
    }    

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
    
    
    
}
