/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.customer;


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;


/**
 *
 * @author Dinah
 */
@Entity
@Table(name="ship_address", catalog="sakila_2")
public class ShipAddress extends PostalAddress implements Serializable {
    
    @Id @GeneratedValue(strategy=IDENTITY)    
    @Column(name="ship_id", unique=true, nullable=false)
    private Short shipId; 
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customerId;  
    
    
    public ShipAddress() {
    }
    
    public Short getShipId() {
        return shipId;
    }

    public void setShipId(Short shipId) {
        this.shipId = shipId;
    }

    public Customer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Customer customerId) {
        this.customerId = customerId;
    }   
    
    
} //end entity
