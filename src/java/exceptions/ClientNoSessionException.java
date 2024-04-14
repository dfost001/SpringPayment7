/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

import view.attributes.*;

/**
 *
 * @author Dinah
 */
public class ClientNoSessionException extends RuntimeException{
    
   public ClientNoSessionException(String message){
       
       super(message);
   };
    
   
}
