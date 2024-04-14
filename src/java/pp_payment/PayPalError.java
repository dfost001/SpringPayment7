/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

import java.util.ArrayList;

/**
 *
 * @author Dinah3
 */
public class PayPalError {
    
    private String name;
    private String message;
    private String informationLink;
    private String debugId;
    private ArrayList<ErrDetail> details;
    
    public PayPalError(){}

    public String getDebugId() {
        return debugId;
    }

    public void setDebugId(String debugId) {
        this.debugId = debugId;
    }

    public String getInformationLink() {
        return informationLink;
    }

    public void setInformationLink(String informationLink) {
        this.informationLink = informationLink;
    }

   

    public ArrayList<ErrDetail> getDetails() {
        if(details == null)
            details = new ArrayList<ErrDetail>();
        return details;
    }

    public void setDetails(ArrayList<ErrDetail> details) {
        this.details = details;
    }

    

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
