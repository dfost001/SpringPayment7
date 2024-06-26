package model.customer;
// Generated Jun 8, 2015 7:04:12 PM by Hibernate Tools 4.3.1


import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Store generated by hbm2java
 */
@Entity
@Table(name="store"
    ,catalog="sakila_2"
    , uniqueConstraints = @UniqueConstraint(columnNames="manager_staff_id") 
)
public class Store  implements java.io.Serializable {


     private Byte storeId;
     private Address addressId;
     private Staff staff;
     private Date lastUpdate;
     //private Set<Staff> staffs = new HashSet<Staff>(0);
    // private Set<Customer> customers = new HashSet<Customer>(0);

    public Store() {
    }   
   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="store_id", unique=true, nullable=false)
    public Byte getStoreId() {
        return this.storeId;
    }
    
    public void setStoreId(Byte storeId) {
        this.storeId = storeId;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="address_id", nullable=false)
    public Address getAddressId() {
        return this.addressId;
    }
    
    public void setAddressId(Address addressId) {
        this.addressId = addressId;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="manager_staff_id", unique=true, nullable=false)
    public Staff getStaff() {
        return this.staff;
    }
    
    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_update", nullable=false, length=19)
    public Date getLastUpdate() {
        return this.lastUpdate;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

/*@OneToMany(fetch=FetchType.LAZY, mappedBy="store")
    public Set<Staff> getStaffs() {
        return this.staffs;
    }
    
    public void setStaffs(Set<Staff> staffs) {
        this.staffs = staffs;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="store")
    public Set<Customer> getCustomers() {
        return this.customers;
    }
    
    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }*/

}


