/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCityState.client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dinah
 */
public class LookupResponse {
    
    public enum Status {blank,invalid_state, invalid_city, invalid_zipcode, conflict}
    
    private Integer inputIndex 	;
    private Integer inputId 	;
    private List<CityStates> cityStates; 	
    private List<Zipcodes> zipcodes;	
    private Status status ;	
    private String reason;

    public Integer getInputIndex() {
        return inputIndex;
    }

    public void setInputIndex(Integer inputIndex) {
        this.inputIndex = inputIndex;
    }

    public Integer getInputId() {
        return inputId;
    }

    public void setInputId(Integer inputId) {
        this.inputId = inputId;
    }

    public List<CityStates> getCityStates() {
        if(cityStates == null)
            cityStates = new ArrayList<>();
        return cityStates;
    }

    public void setCityStates(List<CityStates> cityStates) {
        this.cityStates = cityStates;
    }

    public List<Zipcodes> getZipcodes() {
        if(zipcodes == null)
            zipcodes = new ArrayList<>();
        return zipcodes;
    }

    public void setZipcodes(List<Zipcodes> zipcodes) {
        this.zipcodes = zipcodes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
    
    
}
