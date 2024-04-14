/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.annotations;


import formatter.annotations.TextFormat.Format;

/**
 * To do: "AT &T" -> AT T (fixed)
 * 
 */
public class FormatterUtil {
    
    private boolean isProperName = false;
    private boolean isPostalName = false;
    private boolean isEmail = false;
    private boolean isAddressLine = false;
    private boolean isPostalCode = false;
    private boolean isDefault = false;
    private boolean isNoFormat = false;
    private boolean isUpper = false;
    private boolean isLower = false;
    private boolean isProper = false;
    private boolean isNoCase = false;
    
    private static final String PERIOD = Character.valueOf((char)46).toString();
    private static  final String SPC = Character.valueOf((char)32).toString();

    
    /*
     * Ensure that formatting is consistent to one type of logical field
     * Iterate array Format constants and break after one type is found.
     * The type is assigned to a module-level variable, and accessed from
     * the format method.
     */
    public FormatterUtil(Format[] formats) {
        
        boolean fmtSpecified = false;
        boolean caseSpecified = false;
        
        for(Format fmt : formats) {
            if(fmt.equals(Format.PROPER_NAME)) {
                isProperName = true;
                fmtSpecified = true;
                break;
            } 
            else if(fmt.equals(Format.POSTAL_NAME)) {
                isPostalName = true;
                fmtSpecified = true;
                break;
            } 
            else if(fmt.equals(Format.EMAIL)){
                isEmail = true;
                fmtSpecified = true;
                break;
            }
            else if(fmt.equals(Format.NO_FORMAT)){
                isNoFormat = true;
                fmtSpecified = true;
                break;
            }
            else if(fmt.equals(Format.ADDRESS_LINE)) {
                isAddressLine = true;
                fmtSpecified = true;
                break;
            }
            else if(fmt.equals(Format.POSTAL_CODE)){
                isPostalCode = true;
                fmtSpecified = true;
            }
        }
        
       if(!fmtSpecified)
            isDefault = true;  
       
        
         for(Format fmt : formats) {
            if(fmt.equals(Format.UPPER)) {
                isUpper = true;
                caseSpecified = true;
                break;
            } 
            else if(fmt.equals(Format.LOWER)){
                isLower = true;
                caseSpecified = true;
                break;
            }
            else if(fmt.equals(Format.PROPER)){
                isProper = true;
                caseSpecified = true;
                break;
            }
            else if(fmt.equals(Format.NO_CASE)){
                isNoCase = true;
                caseSpecified = true;
                break;
            }
        }//end for
        if(!caseSpecified)
            isProper = true;
        
    }//end constructor
    
    /*
     * To do: if isNoFormat, and a case specified skip format, and apply case
     * To do: case '&' only evaluates alphabetic preceding and following chars, but can also be digits 
     * 
     */
    public String format(String value) {
        
       
        String emailLocal = "";      
        
        if(isNoFormat)
            return value;        
        
        String edited = "";
        
        char prev = (char)0;
        
        boolean included = false;
        
       if(value == null || value.trim().isEmpty()) 
           return "";
       
       
        if(isEmail){ //Currently only string after the '@' is edited
            if(value.indexOf('@') != -1) { 
                
                 emailLocal = value.substring(0, value.indexOf('@')); 
                 
                 value = value.substring(value.indexOf('@'));
                  
            }
            else return value; //Currently, local-user is not formatted
        }
        
        for(int i = 0; i < value.length(); i++) {
            
           included = true; 
           
           if(Character.isAlphabetic(value.charAt(i))) {
                edited += value.charAt(i);
               
            }
            else if(Character.isDigit(value.charAt(i)) && !isProperName && !isPostalName) {
                edited += value.charAt(i);
                if(isProperName || isPostalName)
                    included = false;
            }
            else {
                char c = value.charAt(i);
                switch(c){
                    case (char)32:
                         if(isEmail || isPostalCode){
                             included = false;                       
                         }
                         else if(Character.isAlphabetic(prev)|| Character.isDigit(prev) ){
                             edited += c;
                         }
                         else if(prev == '&'){                                                         
                             if(edited.substring(edited.length()-2, edited.length()-1).equals(SPC))
                                edited += c; //ampersand in edited string is preceded by a space
                         }    
                         else included = false;
                         break;
                    case '-': 
                        if(Character.isAlphabetic(prev)|| Character.isDigit(prev) ){
                             edited += c;
                         }
                        else included = false;
                         break;
                    case '/' :
                        if(isEmail || isProperName || isPostalName){
                            included = false;
                        }
                        else if(Character.isAlphabetic(prev)|| Character.isDigit(prev) ){
                             edited += c;
                         }
                        else included = false; 
                        break;
                    case '@' :
                        if(!isEmail)
                            included = false;
                        else if(prev == '@')
                            included = false;
                        else
                            edited += c;
                        break;
                    case (char)46  : //period
                        if(!isEmail)
                            included = false;
                        else if(prev == (char)46)
                            included = false;                           
                        else
                            edited += c;
                        break;
                    case '#' :
                        if(isEmail || isProperName || isPostalName)
                            included = false;
                        else if(edited.isEmpty())
                            edited += c;
                        else if(Character.valueOf(prev).toString().equals(SPC))
                            edited += c; //can only be followed by a letter or digit
                        else if(Character.isAlphabetic(prev) || Character.isDigit(prev)) 
                            edited += SPC + '#';
                        else included = false;
                        break;    
                    case '&' :
                        if(isEmail || isProperName || isPostalName)
                            included = false;
                        //include if ampersand joins two words
                        else if(prev == (char)32
                                && i + 2 < value.length() 
                                && value.charAt(i+1) == (char)32                                    
                                && Character.isAlphabetic(value.charAt(i+2)))
                              edited += c;  
                        else if(Character.isAlphabetic(prev))
                            edited += c; //punctuation or space following will be removed
                        //replace preceding space with amp if following char is alphabetic
                        else if(prev == (char)32
                                && i + 1 < value.length() 
                                && Character.isAlphabetic(value.charAt(i+1)))
                            edited =  this.replaceCharAtPosition(edited.length()-1, edited, '&');
                        //include if ampersand is pnemonic - preceeded by a letter                        
                        else
                            included = false;
                        break;
                    
                    default:
                        included = false;
                                
                } //end switch
            }//end else
            if(included)
                prev = value.charAt(i);
        }//end for  
         
       // System.out.println("FormatterUtil#format: After format loop: " + edited + "=" + edited.length());
        
        if(edited.isEmpty()) //no valid sequences
            return "";
        
        int i = edited.length() - 1;
        
        for(; i >= 0; i--)
            if(Character.isAlphabetic(edited.charAt(i))
                    || Character.isDigit(edited.charAt(i)))
                break;      
        
        edited = edited.substring(0, i+1); //remove trailing       
        
         if(isEmail)
                return evalEmail(edited, emailLocal);
            
        if(edited.isEmpty())
            return edited;
        
        if(isProperName)
            edited = this.replaceSpace(edited);
        
        edited = doCase(edited);           
        
        if(isAddressLine)
            edited = AddressFormatter.format(edited);
        
        return edited;
    }
    
    private String evalEmail(String edited, String local){
        
        String retVal = "";
        
        if(!edited.isEmpty() && !local.isEmpty())
            retVal = local + edited.toLowerCase();
        
        else if(!edited.isEmpty()) // local is empty
            retVal = edited;
        
        else if(!local.isEmpty())  // edited is empty
            retVal = local + "@" ;     
             
        
        return retVal;
    }
    
    private String replaceCharAtPosition(int offset, String edited, char replacement) {
       @SuppressWarnings("StringBufferMayBeStringBuilder")
       StringBuffer sb = new StringBuffer(edited);
       sb.setCharAt(offset, replacement);
       return sb.toString();
    }
    
    private String doCase(String edited) {
        if (isNoCase) {
            return edited;
        }
        if (isDefault || isProper) {
            edited = properCase(edited);
        } else if (isUpper) {
            edited = edited.toUpperCase();
        } else if (isLower) {
            edited = edited.toLowerCase();
        }
        return edited;
    }
        private String properCase(String val) {
		
		String formatted = "";
		int start = 0;
		String token="";
		val = val.trim();
		for(int i=0; i < val.length(); i++) {
			char c = val.charAt(i);
			if(c == '/' || c == '-' || c == (char)32) {
				token = val.substring(start,i);
				if(token.isEmpty()) {//embedded space, consecutive delimiter
					start += 1;
					continue;
				}
				token = capitalize(token);
				start = i + 1;
				formatted += token + new Character(c).toString();
			}
		}
		token = val.substring(start); //Get the last token since there is no delimiter
		formatted += capitalize(token);
		return formatted;
		
	}
	
	private String capitalize(String token) {
		String edit="";
                if(token.contains("&"))
                    return token.toUpperCase();
                else if(token.contains("#"))
                    return token.toUpperCase();
               // else if(token.toUpperCase().equals(token))
               //     return token;
                else if(Character.isDigit(token.charAt(0)))
                    return(token.toUpperCase());
		edit = token.substring(0,1).toUpperCase()
				+ token.substring(1,token.length()).toLowerCase();
		return edit;
	}	
    
    private String replaceSpace(String name) {     
      
      char replacement = '-';
        
      name = name.replace((char)32, replacement);
        
        return name;
    }
    
    private boolean allSymbols(String value){
        for(int i=0; i < value.length(); i++)
            if(Character.isAlphabetic(value.charAt(i))
                  || Character.isDigit(value.charAt(i)))
                       return false;
        return true;
    }
    
}// end class
