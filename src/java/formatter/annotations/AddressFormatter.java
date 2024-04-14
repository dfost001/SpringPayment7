/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.annotations;



import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.StringUtil;


/**
 *
 * @author Dinah
 */

public class AddressFormatter {  
    
    private static final String[] ordinalSfx = {"st", "nd", "rd", "th"};
	
    private static final String[] compass = {"NW", "NE", "SW", "SE"};
    
    private static final String[] secondary = {"Apt", "Ste", "Suite", "Fl"};
	
    private static final char SPACE = (char)32;
	
	public static String format(String line){
            
                if(line == null || line.isEmpty()) return line;
            
                String edited = evalBoxFormat2(line);  
                
                if(edited != null && !edited.isEmpty())
                    return edited;
		
		edited = removeOrdinalSpaceSuffix(line);
                
                edited = lowerCaseOrdinalSuffix(edited); //see FormatterUtil#captialize 
                
		edited = capitalizeCompass(edited);
                
		return edited;
	} 
        
           public static String evalBoxFormat2(String entry) {  
           
            String line = StringUtil.removeSpaces(entry);
           
            boolean containsDigit = false;
           
            int digitIndex = StringUtil.findDigitIndex(line);            
                        
            String eval = ""; 
            
            if(digitIndex == 0)
                return null;    //Starts with digit        
            else if(digitIndex > 0) {          
                 eval = line.substring(0,digitIndex);
                 containsDigit = true;
            }
            else eval = line; //no digit token
            
            eval = StringUtil.removeIfEndsWith("#", eval); //Pattern will not match PO Box #
                
            boolean matches = matchesBox4(eval);
            
          /*  System.out.println(MessageFormat.format("entry: {0} eval: {1} matches: {2}",
                    entry, eval, matches)
            ); */
            
            String edited = "PO" + SPACE + "Box" + SPACE;
            
            if(matches && containsDigit) { //Concat formatted line to digits
                 
                 return edited + line.substring(digitIndex);
            }
            else if(matches) 
                
                return edited.trim(); 
            
            else return null;      
       }  
	
	
         public static boolean matchesBox3(String eval) {
             
            boolean found = false; 
            
            String[] regex = {"[pobox]{5,}", 
                    "[pobx]{4,}", 
                    "[pobo]{4,}", 
                    "[pox]{3,}",                    
                    "[po]{2,}",
                    "[px]{2,}" ,
                    "[pbox]{4,}",
                    "[pbx]{3,}", 
                    "[pbo]{3,}"} ;
            
            for(int i = 0; i<regex.length; i++)  { 
                
                 Pattern pattern = Pattern.compile(regex[i], Pattern.CASE_INSENSITIVE);
            
                 Matcher matcher = pattern.matcher(eval);
            
                 if(matcher.matches()){
                     found=true;
                     break;
                 }
           
            }
           return found;
        }
         
           public static boolean matchesBox4(String eval) {
             
            boolean found = false; 
            
            String[] regex = {"(pobox)+", 
                    "(pobx)+", 
                    "(pobo)+",                            
                    "(po)+",
                    "(pbox)+",
                    "(pbx)+", 
                    "(box)+",
                    "(pbo)+"} ;
            
            for(int i = 0; i<regex.length; i++)  { 
                
                 Pattern pattern = Pattern.compile(regex[i], Pattern.CASE_INSENSITIVE);
            
                 Matcher matcher = pattern.matcher(eval);
            
                 if(matcher.matches()){
                     found=true;
                     break;
                 }
           
            }
           return found;
        } 
        
        
	private static String capitalizeCompass(String value) {
		
		String edited = "";
		
		String[] tokens = value.split("\\s");
		
		for(int i=0; i < tokens.length; i++) {
			
			for(String c : compass)
			   if(c.equals(tokens[i].toUpperCase())) {
				  tokens[i] = c;
				  break;
			   }
			edited += tokens[i] + SPACE;
		}
		return edited.trim();
        }
        
        
        
        private static String removeOrdinalSpaceSuffix (String entry) {
            
            String[]tokens = entry.split("\\s");
		
	    String edited = tokens[0];
		
		for(int i=1; i < tokens.length; i++)  { //Skip the first token
                    
			if(ordMatches(tokens[i]) && StringUtil.allDigits(tokens[i-1]))
				edited += tokens[i].toLowerCase();
			else edited += SPACE + tokens[i];
                }
                
            return edited;    
            
        } //end
        
        private static boolean ordMatches(String tok) {
		for(String s : ordinalSfx)
			if(tok.toLowerCase().equals(s))
				return true;
		return false;
	}
        
        private static String lowerCaseOrdinalSuffix(String line) {
            
            String[]tokens = line.split("\\s");		
	   
            
            for(int i=0; i < tokens.length; i++) {
                for(String ord : ordinalSfx) {
                    String tok = tokens[i];
                    if(tok.toLowerCase().endsWith(ord) && 
                            StringUtil.allDigits(tok.substring(0, tok.length()-2)))
                         tokens[i] = tok.toLowerCase();
                } 
            }
            
            String joined  = "";
            
            for(String tok : tokens) {
                
                joined += tok + SPACE;
                
            }
            
            return joined.trim();
        }
        
         public static String[] findCompass(String value) {
             
           ArrayList<String> compassArray = new ArrayList<>();
             
            String found = null; 
             
            if(value == null || value.trim().isEmpty()) return null;
             
            String[] tokens = value.split("\\s");
		
	    for(int i=0; i < tokens.length; i++) {
			
		for(String c : compass) {
                    
		   if(c.equals(tokens[i].toUpperCase())) {
                    
		       compassArray.add(c);
		   }
                }	
	   }
	   return (String[])compassArray.toArray();
       }

    
} //end class
