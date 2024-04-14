/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService.addressService;

/**
 *
 * @author Dinah3
 */
public class AddrSvcConstants {
    /*
     * Dpv messages
     */
    public static final String AA = "Info: City, State, Zip found. Does NOT guarantee deliverability."; //Covered by BB or an Error
    public static final String A1 = "Error: Street not found in city/state or zip";
    public static final String BB = "Info: Entire address is valid. Confirmed as DELIVERABLE.";   
    public static final String CC = "Error: Address is made all valid by not including secondary.";
    public static final String M1 = "Error: Building number is missing"; 
    public static final String M3 = "Error: Building number is not valid";
    public static final String C1 = "Error: Submitted secondary is not valid. REQUIRED for delivery." ;
    public static final String N1 = "Error: Address confirmed by adding a REQUIRED apt/suite number.";
    public static final String P1 = "Error: PO, RR,or HC Box Number is missing";
    public static final String P3 = "Error: PO, RR,or HC Box Number is invalid";
    public static final String RR = "Info: Private mail box confirmed";
    public static final String R1 = "Error: Address confirmed by excluding private mail box";
    public static final String U1 = "Info: Unique zipcode confirmed";
    public static final String PB = "Info: PO-Box/Street-Name recognized";
    public static final String R7 = "Info: Valid address that currently does not receive USPS delivery";
    public static final String F1 = "Info: Military or Diplomatic address recognized";
    public static final String G1 = "Info: General Delivery address recognized";
    public static final String TA = "Info: Primary number matched by dropping trailing alpha";
    
    /*
     * Footnotes - Revision or additional messages
     */
    public static final String A = "Revision: Zipcode corrected";
    public static final String B = "Revision: Fixed city/state spelling";
    public static final String C = "Error: Invalid city-state or zip";
    public static final String D = "Error: USPS lists address as not deliverable. No zip plus 4.";
    public static final String E = "Info: Multiple records returned"; 
    public static final String F = "Dup: Street could not be found in city/state or zip";
    public static final String G = "Revision: Recipient combined into address";
    public static final String H = "Error: Please enter an apartment or suite number";
    public static final String I = "Error: More than one zip found. Pre or post direction may be required**";
    public static final String J = "Error: The input contained two addresses";
    public static final String K = "Error: Address may be found by changing the pre or post direction.";
    public static final String L = "Revision: Directional or suffix added, changed, deleted";
    public static final String M = "Revision: Changed street-name spelling";
    public static final String N = "Revision: Address line standardized.";
    public static final String O = "Revision: Multiple zip match. Service is returning lowest zip.";
    public static final String P = "Revision: Street-name changed to preferred";
    public static final String Q = "Info: Unique zip area matched";
    public static final String R = "Info: Exact match will be available soon";
    public static final String S = "Error: Secondary number not within valid interval";
    public static final String T = "Error: Address is missing a suffix or direction causing an inexact match**";
    public static final String U = "Revision: City or post office name changed to acceptable.";
    public static final String V = "Revision: City/State changed to corresponding zip";
    public static final String W = "Error: USPS requires a PO Box, General Delivery or Postmaster for delivery " +
            "within this zip";
    public static final String X = "Revision: Used the only address within this zip";
    public static final String Y = "Info: Military zipcode matched";
    public static final String Z = "Revision: Zip changed to a moved code";
    
    /*
     * Field names in the AjaxRequest class
     * Used by JavaScript to assign message columns
     */
    public static final String fldStreet = "street";
    public static final String fldCity = "city";
    public static final String fldState = "state";
    public static final String fldZip = "zipcode";
    public static final String fldCountry = "country";
    
} //end class
