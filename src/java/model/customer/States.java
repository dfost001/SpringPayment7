/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.customer;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Dinah
 */
@Entity
@Table(name = "states", catalog = "sakila_2")
@NamedQueries({
    @NamedQuery(name = "States.findAll", query = "SELECT s FROM States s"),
    @NamedQuery(name = "States.findByStCode", query = "SELECT s FROM States s WHERE s.stCode = :stCode"),
    @NamedQuery(name = "States.findByStName", query = "SELECT s FROM States s WHERE s.stName = :stName"),
    @NamedQuery(name = "States.findByFirstZip", query = "SELECT s FROM States s WHERE s.firstZip = :firstZip"),
    @NamedQuery(name = "States.findByLastZip", query = "SELECT s FROM States s WHERE s.lastZip = :lastZip")})
public class States implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 2, max = 2)
    @Column(name = "StCode")
    private String stCode;
    @Size(max = 25)
    @Column(name = "StName")
    private String stName;
    @Size(max = 10)
    @Column(name = "FirstZip")
    private String firstZip;
    @Size(max = 10)
    @Column(name = "LastZip")
    private String lastZip;

    public States() {
    }

    public States(String stCode) {
        this.stCode = stCode;
    }

    public String getStCode() {
        return stCode;
    }

    public void setStCode(String stCode) {
        this.stCode = stCode;
    }

    public String getStName() {
        return stName;
    }

    public void setStName(String stName) {
        this.stName = stName;
    }

    public String getFirstZip() {
        return firstZip;
    }

    public void setFirstZip(String firstZip) {
        this.firstZip = firstZip;
    }

    public String getLastZip() {
        return lastZip;
    }

    public void setLastZip(String lastZip) {
        this.lastZip = lastZip;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stCode != null ? stCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof States)) {
            return false;
        }
        States other = (States) object;
        if ((this.stCode == null && other.stCode != null) || (this.stCode != null && !this.stCode.equals(other.stCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.customer.States[ stCode=" + stCode + " ]";
    }
    
}
