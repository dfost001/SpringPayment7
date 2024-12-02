/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import error_util.EhrLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author dinah
 * 
 * To do: Account for null arguments
 */
public class StringUtil {
    
    public static boolean compareRemoveSpacesIgnoreCase(String str1, String str2) {
        
        String cmp1 = str1.replaceAll("\\s", "").toLowerCase();
        String cmp2 = str2.replaceAll("\\s", "").toLowerCase();
        
        if(cmp1.contentEquals(cmp2))
            return true;
        
        return false;
        
    }
    
    public static boolean compareRemoveSpaces(String str1, String str2) {
        
        String cmp1 = str1.replaceAll("\\s", "");
        String cmp2 = str2.replaceAll("\\s", "");
        
        if(cmp1.contentEquals(cmp2))
            return true;
        
        return false;
        
    }
    
    public static boolean compareExact(String str1, String str2) {
        
        if(str1.contentEquals(str2))
            return true;
        return false;
    }
    
    public static boolean allDigits(String entry){
        
        if(entry == null || entry.trim().isEmpty())
            return false;         
        
        for(int i=0; i < entry.length(); i++)
            if(!Character.isDigit(entry.charAt(i)))
                   return false;
        
        return true;
    }
    
    public static boolean allAlphaDigits(String entry) {
        
         if(entry == null || entry.trim().isEmpty())
            return false;         
        
        for(int i=0; i < entry.length(); i++)
            if(!Character.isDigit(entry.charAt(i)) && !Character.isAlphabetic(entry.charAt(i)))
                   return false;
        
        return true;
    }
    
    public static String getValueOrEmpty(String value) {
        
        if(value == null || value.trim().isEmpty())
            return new String();
        return value;
    }
    
    public static boolean isNullOrEmpty(String value) {
        
        if(value == null || value.trim().isEmpty())
            return true;
        return false;
    }
    /*
     * To do: Allowable tokens if entry is longer
     */
    public static boolean tokenizeAndCompare(String compareTo, String entry, boolean ignoreEntrySize) {
        
        if(compareTo == null || entry == null)
            return false;
        
        String [] tokensCmp = compareTo.split("\\s+");
        
        String [] tokensEntry = entry.split("\\s+");       
        
        int i = 0;
        
        for(; i < tokensCmp.length; i++) {
            if(i == tokensEntry.length) //If entry shorter than compareTo
                return false;
            else if(!tokensCmp[i].contentEquals(tokensEntry[i]))
                return false;
        }
       if(ignoreEntrySize)
           return true;
       if(i < tokensEntry.length)//If entry longer than compareTo
           return false;
        return true;
    }
    
    private static void checkSplit(String[] tokens1, String[] tokens2) {
        
        String space = new String(new byte[] {32});
        
        tokens1 = removeTokens(tokens1, new String[] {"", space});
        
        tokens2 = removeTokens(tokens2, new String[] {"", space});
        
        debugPrintRemoveArray(tokens1, "compareTo");
        
        debugPrintRemoveArray(tokens2, "entry");
    }
    
    public static String[] removeTokens(String[] arrTokens, String[] exclude) {
        
        List<String> tokens = Arrays.asList(arrTokens);
        
        debugPrintRemoveList(tokens, "Before");
        
        for(int i=0; i < exclude.length; i++) {
            Iterator<String> it = tokens.iterator();
            while(it.hasNext())
                if(it.next().equals(exclude[i]))
                    it.remove();
        } 
        
        debugPrintRemoveList(tokens, "After");
        
        return tokens.toArray(new String[0]);
    }
    
    public static String subStringFromArray(String[] source, int from, int toExclusive) {
        
        String edited="";
        
        if(source == null)
            EhrLogger.throwIllegalArg(StringUtil.class.getCanonicalName(), "subStringFromArray",
                    "Source array is null");
        
        if(toExclusive > source.length)
            EhrLogger.throwIllegalArg(StringUtil.class.getCanonicalName(), 
                    "subStringFromArray", "Index 'to' is greater than length of source array");
        
        for(int start=from; start < toExclusive; start++)
            edited += source[start];
        
        return edited;
    }
    
    public static int findDigitIndex(String line) {
        
        if(line==null || line.isEmpty())
            return -1;
        for(int i=0; i < line.length(); i++) {
            if(Character.isDigit(line.charAt(i)))
                return i;
        }
        return -1;
    }
    
    public static String removeIfEndsWith(String charToRemove, String source){
        
        if(source == null || source.isEmpty())
            return source;
        
        if(source.trim().endsWith(charToRemove))
            return source.substring(0,source.length()-1);
        
        return source;
    }
    
    public static String removePunctuation(String value, String...exclude) {
        
        if(isNullOrEmpty(value)) return value;
        
        @SuppressWarnings("StringBufferMayBeStringBuilder")
                
        StringBuffer edited = new StringBuffer();
        
        List<String> exlist = new ArrayList<>();        
        
        if(exclude != null)
           exlist = Arrays.asList(exclude) ;
        
        for(int i=0; i < value.length(); i++) {
            char c = value.charAt(i);
            if(Character.isAlphabetic(c) || Character.isDigit(c)
               || exlist.contains(Character.valueOf(c).toString()))
            edited.append(c)  ;  
        } 
        
        return edited.toString();
    }
    
    public static String removeSpaces(String line) {
        
        if(line == null || line.isEmpty())
            return line;
        
         String[] tokens = line.trim().split("\\s+") ;
         
         return String.join("", tokens);
    }
    
    private static void debugPrintRemoveList(List<String> tokens, String title) {
        System.out.println("StringUtil#debugPrintRemoveList: " + title);
        tokens.forEach(s -> System.out.println("token=" + s));
    }
    private static void debugPrintRemoveArray(String[] tokens, String title) {
         System.out.println("StringUtil#debugPrintRemoveArray: " + title);
         for(String s : tokens)
             System.out.println("token=" + s);
         
    }
}
