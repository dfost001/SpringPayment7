/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.modelattribute;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
//import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import formatter.constraints.PhoneValid;


/**
 *
 * @author Dinah
 */
public class BindModel implements Serializable{
    
    public enum Card_Type {
        Visa, MasterCard, AmEx
    }
    @NotEmpty
    @Size(min=2, max=40)
    private String name;
    
    @NotNull
    @Size(min=16, max=16)
    private String card;
    
    @NotNull
    private Integer month;
    
    @NotNull
    private Integer year;    
    
    @NotNull
    private Card_Type type;
    
    @NotNull
    @NumberFormat(style=NumberFormat.Style.CURRENCY)
    private BigDecimal amount;
    
    @NotNull
    //@DatePartParse
   // @DateTimeFormat(pattern="MM/dd/yyyy")
    private Date startDate;
    
    //@PhoneValid
    @NotNull
    private String phone ;    
    
    //@DateTimeFormat(pattern="MM/dd/yyyy")
    @Past
    @NotNull
    private Date birthDate;
    
    private Boolean storeInfo = Boolean.TRUE;

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Card_Type getType() {
        return type;
    }

    public void setType(Card_Type type) {
        this.type = type;
    }   

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 

    public Boolean getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(Boolean storeInfo) {
        this.storeInfo = storeInfo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    
    
} //end class
