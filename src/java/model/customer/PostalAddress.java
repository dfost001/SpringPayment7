/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.customer;

import exception_handler.LoggerResource;
import formatter.annotations.TextFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import validation.constraints.EmailValid;

/**
 *
 * @author Dinah
 */
//@Entity
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@MappedSuperclass
abstract public class PostalAddress {    
    
    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="address_id", nullable=false)
    @Valid
    protected Address addressId;
    
    @Column(name="first_name", nullable=false, length=45)
    @NotEmpty
    @Size(min=2, max=45)
    @TextFormat({TextFormat.Format.UPPER, TextFormat.Format.PROPER_NAME})    
    protected String firstName;
    
    @Column(name="last_name", nullable=false, length=45)
    @NotEmpty
    @Size(min=2, max=45)
    @TextFormat({TextFormat.Format.UPPER, TextFormat.Format.PROPER_NAME})   
    protected String lastName;  
    
    @Column(name="email", length=50)
    @TextFormat({TextFormat.Format.EMAIL,TextFormat.Format.LOWER})
    @EmailValid
    protected String email;
    
    @Temporal(TemporalType.DATE)
    @Column(name="create_date", nullable=false, length=19)
    @DateTimeFormat(pattern="EEE MMM d, yyyy HH:mm a z") 
    protected Date createDate;
     
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_update", nullable=false, length=19)
    @DateTimeFormat(pattern="EEE MMM d, yyyy HH:mm a z") 
    protected Date lastUpdate;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddressId() {
        return addressId;
    }

    public void setAddressId(Address addressId) {
        this.addressId = addressId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.addressId);
        hash = 59 * hash + Objects.hashCode(this.firstName);
        hash = 59 * hash + Objects.hashCode(this.lastName);
        hash = 59 * hash + Objects.hashCode(this.email);
        hash = 59 * hash + Objects.hashCode(this.createDate);
        hash = 59 * hash + Objects.hashCode(this.lastUpdate);
        return hash;
    }

  /*  @Override
    public boolean equals(Object obj) {
        
        String msg = null;
        
        Logger logger = LoggerResource.createFileHandler(
                "C:\\Users\\dinah\\myLogs\\Spring7\\is_equal_logger.txt", this.getClass());  
        
        final PostalAddress other = (PostalAddress) obj;
        
       // boolean isEqual = true;
       if(this == obj){           
           msg = "this == obj" ;
           logger.info(msg);
           return true;
        } 
        
       else if (obj == null) {
            msg = "obj == null" ;
            logger.info(msg);
            return false;
        }
        else if (getClass() != obj.getClass()) {
            msg = "getClass() != obj.getClass()";
            logger.info(msg);
            return false;
        }
        
        else if (!this.firstName.equals(other.firstName)) {
            msg="!this.firstName.equals(other.firstName)";
            logger.info(msg);
            return false;
        }
        else if (!this.lastName.equals(other.lastName)) {
            msg ="!Objects.equals(this.lastName, other.lastName)";
            logger.info(msg);
            return false;
        }
        else if (!this.email.equals(other.email)) {
            msg ="!Objects.equals(this.email, other.email)" ;
            logger.info(msg);
            return false;
        }
        else if (!addressId.equals(other.addressId)) {
            msg="!addressId.equals(other.addressId)" ;
            logger.info(msg);
            return false;
        }
        else if (!this.createDate.equals(other.createDate)) {
            msg = "!Objects.equals(this.createDate, other.createDate)" ;
            logger.info(msg);
            return false;
        }
        else if (!this.lastUpdate.equals(other.lastUpdate)) {
            msg ="!Objects.equals(this.lastUpdate, other.lastUpdate)" ;
            logger.info(msg);
            return false;
        }
        
        msg = "PostalAddress#equals returning true";              
        logger.info(msg);
        
        return true; 
        
    } */  
    
}
