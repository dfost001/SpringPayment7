/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService.addressService;

import error_util.EhrLogger;
import restAddressService.client.Candidates;
import restAddressService.client.Request;
import java.util.List;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import restAddressService.client.Candidates.Candidate;

import util.StringUtil;

/**
 *
 * @author Dinah
 */

public class AddressSvc {    
   
    /*
     * Fields initialized by service and returned in analysis
     */
    private String deliveryLine = "";
    private String city = "";
    private String state = "";
    private String zipcode = "";
    private String plus4 = ""; 
    private String matchCode = null;
    private String dpvFootnotes = "";
    private String footnotes = "";
    /*
     *Additional fields initialized by this component and returned in analysis
    */
   
    private String validatedStreetLineFormat = "";
    private String componentsFoundLine = "";
    private boolean isValidatedMatch = false;    
    private boolean continueOnInvalid = false; 
    private boolean validWarning = false;
    private String debugMatch = "";
    
    /*
     * Used Internally
     */
    private boolean isCompassError = false; //Initialized at assignErrorFootNote. Read at assignCompass.
    private boolean isAllValidMessage = false; //Used internally by REST Controller  
   
    private ArrayList<String> orderedDpvNotes = null;
    private ArrayList<String> detailsDpv = null;
    
    private static final String CONFIRM_PROMPT = "Press \"Confirm\" to assign revisions. " ;           
   
    private static final String NO_INFO = "Invalid Error: No information returned. ";
              
    private static final String NO_INFO_DETAIL= "Street-Line not found in city-state or zip." ;
    
    private static final String VALID_WARNING_MSG = "A zip plus 4 has not yet been assigned to this address." ;   
    
    private final String MATCH_FOUND = "Y";
    private final String MATCH_NOT_FOUND = "N";
    private final String MATCH_SECONDARY_INVALID = "S";
    private final String MATCH_SECONDARY_REQUIRED = "D";
    private final String MATCH_NULL = null;   
    
    private final SvcAnalysis postal;
    
    private final Request request;      
       
    private Candidates.Candidate candidate;
    
    AddrSvcConstants constants = null;      
    
    private final String SPACE = Character.valueOf((char)32).toString();
   
    public AddressSvc(Request request) {
      
        orderedDpvNotes = new ArrayList<>();
        this.detailsDpv = new ArrayList<>();
        constants = new AddrSvcConstants();
        postal = new SvcAnalysis();
        postal.setValid(false);
        this.request = request;
    }     
/*  Note the order in which messages are added to orderedDpvMessages
 *  Errors are inserted before info   
 */
public SvcAnalysis verify(Candidates candidates) {         
    	
        List<Candidates.Candidate> listCandidates = candidates.getCandidate();         
        
        if (listCandidates == null || listCandidates.isEmpty()) { // <candidates/>           
            
            System.out.println("AddressSvc#verify: Candidates is null");
            
            this.orderedDpvNotes.add(NO_INFO);
            this.orderedDpvNotes.add(NO_INFO_DETAIL);
            return initSvcAnalysis(0);            
        }        
        
        this.candidate = listCandidates.get(0);
        
        Candidates.Candidate.Analysis analysis = candidate.getAnalysis();
        
        Candidates.Candidate.Components components = candidate.getComponents();
        
        throwNullAnalysisComponents(analysis, components);    
        
        deliveryLine = StringUtil.getValueOrEmpty(candidate.getDeliveryLine1());
        
        throwEmptyDeliveryLine();   
        
        city = StringUtil.getValueOrEmpty(components.getCityName());
        state = StringUtil.getValueOrEmpty(components.getStateAbbreviation());
        zipcode = StringUtil.getValueOrEmpty(components.getZipcode());
        plus4 = StringUtil.getValueOrEmpty(components.getPlus4Code());   
        
        this.matchCode = analysis.getDpvMatchCode(); // either N, Y, S, D or null
        
        this.isValidatedMatch = this.assignValidity();     
        
        this.assignDebugMatch();
        
        dpvFootnotes = StringUtil.getValueOrEmpty(analysis.getDpvFootnotes()); //2 character codes see constants
        
        footnotes = StringUtil.getValueOrEmpty(analysis.getFootnotes());//single character codes delimited by # see constants         
        
        throwEmptyDpvNotes(dpvFootnotes);             
              
        this.debugPrint();           
        
        this.assignErrorFootNote(footnotes); //Do first, since list may be cleared
        
        this.assignDpv(dpvFootnotes); //Insert these errors at the beginning 
        
        this.continueOnInvalid = this.assignContinueOnInvalid();
        
        this.validatedStreetLineFormat = getValidatedStreetFormat(); //Not assigned if match == null      
        
        this.assignAddressRevisedNote();  
        
        this.assignCompassFoundNote(); //isCompassError initialized by footnotes
        
        this.assignAddressFoundNote();
                   
        initSvcAnalysis(listCandidates.size());      
        
        return postal;
    }

       private String initZipPlusFour() {
            if(!this.zipcode.isEmpty() && !this.plus4.isEmpty())
    	      return this.zipcode + "-" + this.plus4; 
            return "";
        }
        /*
         * Note: An empty street-line is thrown. So code that
         * evaluates the street-line is unnecessary.
        */
       private void assignAddressFoundNote() {          
          
           String street = matchCode != null ? this.getValidatedStreetFormat():
                   this.deliveryLine; //Validated not generated if match==null
            
            String zip = !this.zipcode.isEmpty() && !this.plus4.isEmpty() ?
                     this.zipcode + "-" + this.plus4 : this.zipcode;
            
            String lastLine =  this.city + SPACE
                    + this.state + SPACE
                    + zip;     
            
            componentsFoundLine = lastLine.trim().isEmpty() ? street : 
                    street + ", " + lastLine;                   
          
            String label = "Components Evaluated: ";            
            
            String found = label + componentsFoundLine; //Add a label for address               
             
            orderedDpvNotes.add(found);             
            
            this.assignSecondaryLineNote();         
          
        }
        
        //To do: If designator == '#' concat without a space. Done in assignSecondary.
        private void assignSecondaryLineNote() {      
            
            String note = "Secondary Line: ";           
            
            String secondary = this.getSecondary();
            
            if(secondary.isEmpty())
                note += "None";           
            else note += secondary;
            
            orderedDpvNotes.add(note);
            
        }
        
        private String getSecondary() {
            
             Candidate.Components comp = this.candidate.getComponents();
             
             String designator = comp.getSecondaryDesignator();
             
             if(designator == null || designator.trim().isEmpty())
                 return "";
             
             if(StringUtil.isNullOrEmpty(comp.getSecondaryNumber()))
                 return "";
             
             String secondary = designator.equals("#") ?
                     comp.getSecondaryDesignator() + comp.getSecondaryNumber()
                     :
                     comp.getSecondaryDesignator() + " " + comp.getSecondaryNumber();
             
             return secondary;
        } 
        /*
         * Generate streetline to compare to input.
         * Exits on NULL matchcode.
         */
        private String getValidatedStreetFormat() {           
            
            Candidate.Components comp = this.candidate.getComponents();
            
            debugPrintValidatedStreetFormat(comp);
            
            if(this.matchCode == null)
                return new String();
            
            String pmb = comp.getPmbDesignator()  
                    + " " + comp.getPmbNumber() ;   //Private Mail Box
            
            if(!pmb.trim().isEmpty())
                return pmb;
            
            String streetName = comp.getStreetName();
            
            if(streetName.contentEquals("PO Box")) //PO Box
                return streetName + " " + comp.getPrimaryNumber();
                
            
            String predirection = StringUtil.isNullOrEmpty(comp.getStreetPredirection()) ?
                    "" : comp.getStreetPredirection() + " ";
            
            String suffix = StringUtil.isNullOrEmpty(comp.getStreetSuffix()) ?
                    "" : comp.getStreetSuffix() + " ";        
            
            String streetline = comp.getPrimaryNumber() + " " +
                    predirection +
                    comp.getStreetName() + " " +
                    suffix +
                    comp.getStreetPostdirection(); //Empty Strings returned from getters           
            
           if(streetline.trim().isEmpty())
               return ""; 
           
           
           String compare = streetline.trim() + " " + getSecondary();
           
           return compare.trim();
            
        }   
        
        private void debugPrintValidatedStreetFormat(Candidate.Components comp) {
            System.out.println("AddressSvc#getValidatedStreetFormat: printing");
            
            String debug = MessageFormat.format("primaryNo={0}" 
            + " streetName={1} suffix={2} pmbDesignator={3}"
            + " pmbNumber={4}"
            + " city={5} state={6} secondary={7} number={8}"
            + " compass-pre={9}"
            + " compass-post={10}", comp.getPrimaryNumber(),
            comp.getStreetName(), comp.getStreetSuffix(),
            comp.getPmbDesignator(), comp.getPmbNumber(), 
            comp.getCityName(), comp.getStateAbbreviation(),
            comp.getSecondaryDesignator(),
            comp.getSecondaryNumber(),
            comp.getStreetPredirection(),
            comp.getStreetPostdirection());
            
            System.out.println(debug);   
        }
        
        /* Exit out on null match, since components may not have a valid format.
         *
         * Not sure if CompassError ("K") will have a valid format for an ordinal street-name
         *
         * May also want to exit if zipPlus4 result is empty
         *
         * Note: If there are city/state revisions, and match is null, 
         * confirmRequired is set, but JavaScript will fill only city/state, 
         * not the address-line if empty.
         */
        private void assignAddressRevisedNote(){            
           
            String global = "Address found differs from entry for ";
            
            String gmessage = "";
            
            Request.Address addressEntry = request.getAddress().get(0);           
           
            System.out.println("AddressSvc#assigneAddressRevised: compareLine="
                + validatedStreetLineFormat);
            
              if(!city.isEmpty() && !this.city.equals(addressEntry.getCity())){
                FieldEhr ehr = new FieldEhr();
                ehr.setField(AddrSvcConstants.fldCity);
                ehr.setMessage("City found: " + city); 
                this.postal.addFieldEhr(ehr);
                gmessage += "city, ";                
            }
            if(!state.isEmpty() && !this.state.equals(addressEntry.getState())){
                FieldEhr ehr = new FieldEhr();
                ehr.setField(AddrSvcConstants.fldState);
                ehr.setMessage("state found: " + state); 
                this.postal.addFieldEhr(ehr);
                gmessage += "state, ";
            }     
            
          /*
           * Exit street-line compare if null match. 
           * Components are mal-formed: Ordinal suffix removed; Plus 4 removed from zip.           
           */
           
           if(matchCode != null) {
               gmessage += compareStreetAndZip(addressEntry);
           }      
            
            if(!gmessage.isEmpty()) {
                gmessage = global + gmessage;
                gmessage = gmessage.substring(0, gmessage.length() - 2);
            
                this.orderedDpvNotes.add(gmessage);  
                
                postal.setConfirmRequired(true);
                postal.setConfirmPrompt(CONFIRM_PROMPT);                
                
            } //end if       
    }  
        
    private String compareStreetAndZip (Request.Address addressEntry) {
        
         String gmessage = "";
        
         if(!validatedStreetLineFormat.isEmpty() &&
                 !StringUtil.tokenizeAndCompare(validatedStreetLineFormat,
                  addressEntry.getStreet(), false)) { //boolean ignoreSize -> false
                FieldEhr ehr = new FieldEhr();
                ehr.setField(AddrSvcConstants.fldStreet);
                ehr.setMessage("Line found: " + validatedStreetLineFormat);
                this.postal.addFieldEhr(ehr);
                gmessage += "street-line, ";
                
            }          
            
            String cmpZipResultPlus = this.initZipPlusFour(); //Returns zipPlus4 or empty           
            
            String cmpZipEntry = addressEntry.getZipcode() == null ? 
                    "" : addressEntry.getZipcode();  
                        
            if(!cmpZipResultPlus.isEmpty() &&
             !StringUtil.compareExact(cmpZipEntry, cmpZipResultPlus)) {
                FieldEhr ehr = new FieldEhr();
                ehr.setField(AddrSvcConstants.fldZip);
                ehr.setMessage("zipcode found: " + cmpZipResultPlus); 
                this.postal.addFieldEhr(ehr);
                gmessage += "zipcode, ";
            }
            
            return gmessage;
    }
   
     private String getDirectionNote() {
        
        Candidate.Components comp = this.candidate.getComponents();
        
        String predirection = comp.getStreetPredirection();
        
        String postdirection = comp.getStreetPostdirection();         
        
        String pre = "";
        String post = "";
        String message = "";
        
        if(!StringUtil.isNullOrEmpty(predirection)) {
            pre = "Predirection: " + predirection;            
        }        
        if(!StringUtil.isNullOrEmpty(postdirection)) {
            post = "Postdirection: " + postdirection;            
        }        
        if(!pre.isEmpty() && !post.isEmpty())
            message = pre + ", " + post;
        else if(!pre.isEmpty())
            message = pre;
        else if(!post.isEmpty())
            message = post;
       return message;
    }   
    private SvcAnalysis initSvcAnalysis(int size) {
    	
        postal.setNumberFound(size);
        postal.setAllValidMessage(isAllValidMessage);
    	postal.setCity(this.city);
    	postal.setStateAbbrev(this.state);
    	postal.setZip(this.zipcode);
    	postal.setZipPlus4(initZipPlusFour()); 
    	postal.setDeliveryLine(this.deliveryLine);
    	postal.getSvcMessages().addAll(this.orderedDpvNotes);
        postal.getSvcDetails().addAll(this.detailsDpv);   
        postal.setComponentsFoundLine(this.componentsFoundLine);
      //  postal.setStreetMinusSecondary(this.assignStreetMinusSecondary());
        postal.setValidatedStreetLineFormat(this.validatedStreetLineFormat);
        postal.setMatchCode(matchCode);        
        postal.setDebugMatchCode(debugMatch);        
        postal.setValid(this.isValidatedMatch);
        postal.setContinueOnInvalid(this.continueOnInvalid);
        if(this.validWarning)
           postal.setValidWarningMessage(VALID_WARNING_MSG);
    	return postal;
    }
    
   
    /*
     *Only valid condition is matchcode equal to "Y"
     */
    @SuppressWarnings("StringEquality")
    private boolean assignValidity() {
        
        boolean valid = false;  
        
       if(matchCode == this.MATCH_NULL || matchCode.isEmpty()) {
            valid = false;
           
        } else if(matchCode.equals(this.MATCH_SECONDARY_INVALID)) { //'S'
            valid = false;
            
        }
        else if(matchCode.equals(this.MATCH_SECONDARY_REQUIRED)) { //'D'
            valid = false;
                   
        }  
        else if(matchCode.equals(this.MATCH_NOT_FOUND)) { //'N'
           valid = false;
          
        } else if(matchCode.equalsIgnoreCase(this.MATCH_FOUND)) { //'Y'
            valid = true;
            
        }
        else this.throwIllegalArg("assignValidity", "Unknown matchCode value");
        
        return valid;        
    }
    private boolean assignContinueOnInvalid() {       
        
        if(matchCode == null || matchCode.isEmpty())
            return false ;
        
        String dpv = candidate.getAnalysis().getDpvFootnotes();        
        
        boolean continueOn = false;
        
        switch (matchCode) {
            
            case MATCH_FOUND : //"Y"
                
                if(candidate.getAnalysis().getEwsMatch() != null) {
                    this.validWarning = true; 
                }
                continueOn = true;
                break;
                
            case MATCH_NOT_FOUND : //"N"
                if(dpv.contains("AA")) {
                    continueOn = true;
                    break;
                }
            case MATCH_SECONDARY_INVALID : //"S" (Invalid Interval)
                if(dpv.contains("BB") ) { // All valid
                    continueOn = true; //Address is not a multi-unit. Not required
                    break;
                }
                else if(dpv.contains("C1")) { //Required or invalid interval
                    continueOn = false;
                    break;
                }
                else if(dpv.contains("AA")) {
                    continueOn = true;
                    break;
                }         
                
            case MATCH_SECONDARY_REQUIRED : //"D" (Required)
                if(dpv.contains("N1")) { //Required-almost always
                    continueOn = false;
                    break;
                }
                else if(dpv.contains("AA")) {
                    continueOn = true;
                    break;
                } 
        }      
        
        return continueOn;
    }
      private boolean assignContinueOnInvalidLenient() {       
        
        if(matchCode == null || matchCode.isEmpty())
            return false ;
        
        String zipPlus4 = this.initZipPlusFour();
        
        if(matchCode.equals("S") || matchCode.equals("D")) {
            if(zipPlus4.isEmpty())
               return false;
            else return true;        
        }  
        
        if(matchCode.equals("Y")){
            if(candidate.getAnalysis().getEwsMatch() != null)
               this.validWarning = true;
        }
        
        return true;
    }
    private void assignDebugMatch() {
        
            //Only valid condition is matchcode equal to "Y"
        if(matchCode == null || matchCode.isEmpty()) {
            
           this.debugMatch = "MATCH_NULL";
        }
        
        else if(matchCode.equalsIgnoreCase("Y")) {
            
            this.debugMatch = "MATCH_FOUND (" + matchCode + ")";
        }
        else if(matchCode.equals("S")) {
            
            this.debugMatch = "MATCH_SECONDARY_INVALID (" + matchCode + ")";
        }
        else if(matchCode.equals("D")) {
            
            this.debugMatch = "MATCH_SECONDARY_REQUIRED (" + matchCode + ")";      
        }  
        else if(matchCode.equals("N")) {
           
           this.debugMatch = "MATCH_NOT_FOUND (" + matchCode + ")";
        }
        
    }
  
    /*
     * To do: throw reflection exceptions as illegal argument exceptions
     * Note: recursion ends when fields.substring(2) has a start index equal to the length
     * of the string
     */
    private void assignDpv(String fields) {
              

    	if(fields == null || fields.isEmpty()) {
                System.out.println("assignDpv: fields is null or empty");
    		return;
        }
        String value = "";
        String key = fields.substring(0, 2);        
       
        try {       
            
            Field fld = constants.getClass().getDeclaredField(key);
            
            value = (String) fld.get(constants);
            
            if(key.equalsIgnoreCase("BB")) {
                this.isAllValidMessage = true;
                this.orderedDpvNotes.clear(); //Remove 'AA' and foot-notes
                this.orderedDpvNotes.add(0, value);   
            }
            else if(value.startsWith("Error")){               
                this.orderedDpvNotes.add(0, value);                
            }            
            else if(value.startsWith("Info:"))
            	this.orderedDpvNotes.add(value);
            
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            
           // throw new IllegalArgumentException
           // ("AddressSvc#assignDpv: Error using java.lang.reflect", ex);
           
            orderedDpvNotes.add(this.debugMatch + ": Additional information not available.");
           
        } 
        if(fields.length()== 2) 
            return;
        else
            assignDpv(fields.substring(2));
        
    }
    /*
     * Production: Remove info messages. Just errors.
    */
    private void assignErrorFootNote(String notes) {
       
        if (notes == null || notes.isEmpty()) {
            return;
        }
        String[] temp = notes.split("#");
        String value = "";
        
        for (int i = 0; i < temp.length; i++) {
           try {
                Field fld = constants.getClass().getDeclaredField(temp[i]);
                value = (String) fld.get(constants);
                if(fld.getName().equals("K")){
                    this.isCompassError = true;
                    replaceErrorInMessages(value); //Replace as first message
                }
                else if(value.contains("Error:")){
                   
                    orderedDpvNotes.add(0, value);
                    //replaceErrorInMessages(value);
                }   
                else if(value.contains("Info:")) 
                    this.orderedDpvNotes.add(value);
                else if(value.contains("Revision:")) 
                    this.detailsDpv.add(value);
            } catch (NoSuchFieldException | IllegalAccessException ex) {  
                
                //throw new IllegalArgumentException
                //("AddressSvc#assignErrorFootnotes: error using java.lang.reflect", ex);
                
                orderedDpvNotes.add(this.debugMatch + ": Additional information not available.");
                
                continue;
                
            } 
        }//end for
    }
    /*
     * Note: Match may be null if there is a compass error
     * Note: Separated from assignAddressRevised() since there
     * is no comparison of street-line if match is null, but
     * we want to show revision.
     * To do: Concat compass-note if FieldEhr for fldStreet exists
     * 
    */
    private void assignCompassFoundNote() {
        
        if(isCompassError) {   
            
            String message = this.getDirectionNote();
            
            if(!StringUtil.isNullOrEmpty(message)) {
                
                FieldEhr ehr = new FieldEhr();
                ehr.setField(AddrSvcConstants.fldStreet);
                ehr.setMessage(message);
                this.postal.addFieldEhr(ehr);
                orderedDpvNotes.add(message);
            }
        }
               
    }
    
    private void replaceErrorInMessages(String value) {
        if(orderedDpvNotes.size() > 0)
            orderedDpvNotes.set(0,value);
        else orderedDpvNotes.add(value);
    }
    
    private void throwNullAnalysisComponents(Candidates.Candidate.Analysis analysis, 
           Candidates.Candidate.Components comp) {
        
        String err = "";
        
        if(analysis == null)
            err = "Candidates.Candidate.Analysis is null";
        if(comp == null)
            err += "Candidates.Candidate.Components is null";
       
        
        if(!err.isEmpty())
            throw new IllegalArgumentException(
                    EhrLogger.doError("AddressSvc", "throwNullAnalysisComponents", err)
            );
        
    }

    private void throwEmptyDeliveryLine() {
        
        String message = "Candidate#deliveryLine is null or empty.  "
                + "Possible deserialization error?" ;
        
         if(this.deliveryLine == null || deliveryLine.isEmpty())
                
                throw new IllegalArgumentException (
                        EhrLogger.doError("AddressSvc", 
                                "throwEmptyDeliveryLine", 
                                message)
                );
        
    }
    
    private void throwEmptyDpvNotes(String dpvNotes) {
        
        String message = "No confirmation or rejection message returned in "
                + "Candidate.Analysis#dpvFootnotes";
        
        if(dpvNotes == null || dpvNotes.isEmpty())
                
                throw new IllegalArgumentException (
                        EhrLogger.doError("AddressSvc", 
                                "throwEmptyOrderedDpvNotes", 
                                message));
    }
    
    private void throwIllegalArg(String method, String message) {
        
         throw new IllegalArgumentException (
                        EhrLogger.doError(this.getClass().getCanonicalName(), 
                                method, 
                                message));        
    }
    
    private void debugPrint() {
        System.out.println(this.getClass().getName() + "#debugPrint:");
        
        System.out.println("matchCode=" + this.matchCode);
        System.out.println("city=" + this.city);
        System.out.println("state=" + this.state);
        System.out.println("zip=" + this.zipcode);
        System.out.println("plus4=" + this.plus4);
        System.out.println("deliveryLine=" + this.deliveryLine);
        System.out.println("DPV Notes = " + this.dpvFootnotes );
        System.out.println("Footnotes=" + this.footnotes);
    } 
    
    private void debugPrintMessages(List<String> list) {
        list.forEach(System.out::println);
    }
    
    @SuppressWarnings("LocalVariableHidesMemberVariable") //Candidates.Candidate candidate
    private void debugCandidateList(List<Candidates.Candidate> candidates) {
        
        System.out.println("AddressSvc#debugCandidateList: size=" + candidates.size());
        
        int ct = 1;
        
        for(Candidates.Candidate candidate : candidates) {
        
            String line = candidate.getDeliveryLine1();
            
            System.out.println(ct + ") "  + line);
            
            Candidates.Candidate.Analysis analysis = candidate.getAnalysis();           
            
            System.out.println("active=" + analysis.getActive());
            
            Candidates.Candidate.Components components = candidate.getComponents();
            
            System.out.println("zipcode=" + components.getZipcode());
            
            System.out.println("plus4=" + components.getPlus4Code());
            
            Candidates.Candidate.Metadata meta = candidate.getMetadata();
            
            if(meta != null)            
                System.out.println("BuildingDefaultIndicator: " + meta.getBuildingDefaultIndicator());
            
            ct++;
        
        }       
        
    } 
   
} //end class
