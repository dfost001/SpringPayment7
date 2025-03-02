package restAddressService.addressService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import restAddressService.AjaxRequest;


public class SvcAnalysis implements Serializable{
	  private List<FieldEhr> errors = null; 
          private Boolean valid = false;
	  private String zip = ""; //reformatted postalCode
	  private String zipPlus4 = "";
	  private String city = "";
          private String deliveryLine = ""; //user-entered street-line
          private String validatedStreetLineFormat = ""; //composed street-line
	  private String stateAbbrev = "";
          private boolean confirmRequired = false;
          private Short cityId = null;
          private boolean cityInsertionRequired = false;	  
          private Boolean continueOnInvalid = false;   
          private String validWarningMessage = "";
          private List<String> svcMessages;        
          private AjaxRequest ajaxRequest = null;               
          private String componentsFoundLine = "";
         // private String streetMinusSecondary = "";
	  private String matchCode = "";
          private String debugMatchCode = "";          
          private String confirmPrompt = "";          
          private Integer numberFound = 0;
          
          private List<String> svcDetails;  //Initialized, but not used
          
          /* Used by RestController and JavaScript */
          private boolean allValidMessage;
	
      public SvcAnalysis() {
    	  
      }      

    public boolean isAllValidMessage() {
        return allValidMessage;
    }

    public void setAllValidMessage(boolean allValid) {
        this.allValidMessage = allValid;
    }   

    public AjaxRequest getAjaxRequest() {
        return ajaxRequest;
    }

    public void setAjaxRequest(AjaxRequest ajaxRequest) {
        this.ajaxRequest = ajaxRequest;
    }
    
    public boolean isConfirmRequired() {
        return confirmRequired;
    }

    public void setConfirmRequired(boolean confirmRequired) {
        this.confirmRequired = confirmRequired;
    }     
        
	public String getMatchCode() {
		return matchCode;
	}

	public void setMatchCode(String matchCode) {
		this.matchCode = matchCode;
	}

	public String getDeliveryLine() {
		return deliveryLine;
	}

	public void setDeliveryLine(String deliveryLine) {
		this.deliveryLine = deliveryLine;
	}

        public String getValidatedStreetLineFormat() {
           return validatedStreetLineFormat;
        }

        public void setValidatedStreetLineFormat(String validatedStreetLineFormat) {
           this.validatedStreetLineFormat = validatedStreetLineFormat;
        }

	public String getZip() {
		return zip;
	}
        
	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getZipPlus4() {
		return zipPlus4;
	}

	public void setZipPlus4(String zipPlus4) {
		this.zipPlus4 = zipPlus4;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStateAbbrev() {
		return stateAbbrev;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

        public Boolean getContinueOnInvalid() {
           return continueOnInvalid;
        }

        public void setContinueOnInvalid(Boolean continueOnInvalid) {
           this.continueOnInvalid = continueOnInvalid;
        }

        public String getValidWarningMessage() {
           return validWarningMessage;
        }

        public void setValidWarningMessage(String validWarningMessage) {
            this.validWarningMessage = validWarningMessage;
       }    

	public void setStateAbbrev(String stateAbbrev) {
		this.stateAbbrev = stateAbbrev;
	}

    public String getDebugMatchCode() {
        return debugMatchCode;
    }

    public void setDebugMatchCode(String debugMatchCode) {
        this.debugMatchCode = debugMatchCode;
    }
        
        

    public Short getCityId() {
        return cityId;
    }

    public void setCityId(Short cityId) {
        this.cityId = cityId;
    } 

    public boolean isCityInsertionRequired() {
        return cityInsertionRequired;
    }

    public void setCityInsertionRequired(boolean cityInsertionRequired) {
        this.cityInsertionRequired = cityInsertionRequired;
    }   

    public List<FieldEhr> getErrors() {
            if(this.errors == null)
                errors = new ArrayList<>();
             return errors;
    }

    public void setErrors(List<FieldEhr> errors) {
        this.errors = errors;
    }

    public List<String> getSvcMessages() {
        if(this.svcMessages == null)
            svcMessages = new ArrayList<>();
        return svcMessages;
    }

    public void setSvcMessages(List<String> svcMessages) {
        this.svcMessages = svcMessages;
    }
    
    public void addFieldEhr(FieldEhr ehr){
        if(this.errors == null)
            errors = new ArrayList<>();
        errors.add(ehr);
    }
    
    public void addSvcMessage(String message){
        if(this.svcMessages == null)
            svcMessages = new ArrayList<>();
        svcMessages.add(message);
    }

    public List<String> getSvcDetails() {
        if(this.svcDetails == null)
            svcDetails = new ArrayList<>();
        return svcDetails;
    }

    public void setSvcDetails(List<String> svcDetails) {
        this.svcDetails = svcDetails;
    } 

    public String getConfirmPrompt() {
        return confirmPrompt;
    }

    public void setConfirmPrompt(String confirmPrompt) {
        this.confirmPrompt = confirmPrompt;
    } 

    public String getComponentsFoundLine() {
        return componentsFoundLine;
    }

    public void setComponentsFoundLine(String componentsFoundLine) {
        this.componentsFoundLine = componentsFoundLine;
    }
    
    public Integer getNumberFound() {
        return numberFound;
    }

    public void setNumberFound(Integer numberFound) {
        this.numberFound = numberFound;
    }  
      
} //end class
