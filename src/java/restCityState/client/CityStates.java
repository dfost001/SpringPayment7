/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCityState.client;

/**
 *
 * @author dinah
 */
public class CityStates {

    private String city;
    private String stateAbbreviation;
    private String state;
    private Boolean mailableCity;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateAbbreviation() {
        return stateAbbreviation;
    }

    public void setStateAbbreviation(String stateAbbreviation) {
        this.stateAbbreviation = stateAbbreviation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getMailableCity() {
        return mailableCity;
    }

    public void setMailableCity(Boolean mailableCity) {
        this.mailableCity = mailableCity;
    }
    
    

}
