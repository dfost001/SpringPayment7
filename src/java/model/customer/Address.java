/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.customer;

import formatter.PhoneFormat;
import formatter.annotations.TextFormat;
import formatter.constraints.PhoneValid;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.EnumType;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Dinah
 */
@Entity
@Table(name="address", catalog="sakila_2")
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="address_type")
public class Address implements Serializable {
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="address_id", unique=true, nullable=false)
    private Short addressId;
    
    @Enumerated(value=EnumType.STRING)
    @Column(name="address_type", nullable=true)
    private AddressTypeEnum addressType;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="city_id", nullable=false)
    @Valid
     private City cityId;    
    
     @Size(min=3, max=50)
     @NotEmpty
     @Column(name="address1", nullable=false, length=50)
     @TextFormat({TextFormat.Format.ADDRESS_LINE, TextFormat.Format.PROPER})
     private String address1;
     
     @Column(name="address2", length=50)
     @TextFormat({TextFormat.Format.DEFAULT})
     @Size(min=0, max=50)
     private String address2;
     
    @Column(name="district", nullable=false, length=20)
    @Size(min=2, max=20)
    @NotEmpty
    @TextFormat({TextFormat.Format.POSTAL_NAME, TextFormat.Format.UPPER})
     private String district;
    
    @Column(name="postal_code", length=10)
    @NotEmpty 
    @Size(min=4, max=10)
    @TextFormat({TextFormat.Format.POSTAL_CODE, TextFormat.Format.UPPER})
    private String postalCode;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_update", nullable=false, length=19)
    private Date lastUpdate;
    
    @PhoneValid
    @PhoneFormat
    @Column(name="phone", nullable=false, length=20)
    private String phone;
    
    public Short getAddressId() {
        return addressId;
    }

    public void setAddressId(Short addressId) {
        this.addressId = addressId;
    }

    public AddressTypeEnum getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressTypeEnum addressType) {
        this.addressType = addressType;
    }   

    public City getCityId() {
        return cityId;
    }

    public void setCityId(City cityId) {
        this.cityId = cityId;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }      

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    // @OneToMany(fetch=FetchType.EAGER, mappedBy="addressId")
    // private Set<Customer> customers = new HashSet<>(0);
    
    // private Set<Store> stores = new HashSet<Store>(0);
    // private Set<Staff> staffs = new HashSet<Staff>(0);
    
    
}
