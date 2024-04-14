/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService.addressService;

/**
 *
 * @author Dinah
 */
public class FieldEhr {
    
    private String field;
    private String message;
    
    public FieldEhr(){}
    
    public FieldEhr(String fld, String message){
        this.field = fld;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
}
