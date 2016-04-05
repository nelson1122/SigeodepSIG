/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author SANTOS
 */
@Embeddable
public class OthersPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "victim_id", nullable = false)
    private int victimId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "field_id", nullable = false)
    private short fieldId;

    public OthersPK() {
    }

    public OthersPK(int victimId, short fieldId) {
	this.victimId = victimId;
	this.fieldId = fieldId;
    }

    public int getVictimId() {
	return victimId;
    }

    public void setVictimId(int victimId) {
	this.victimId = victimId;
    }

    public short getFieldId() {
	return fieldId;
    }

    public void setFieldId(short fieldId) {
	this.fieldId = fieldId;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (int) victimId;
	hash += (int) fieldId;
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof OthersPK)) {
	    return false;
	}
	OthersPK other = (OthersPK) object;
	if (this.victimId != other.victimId) {
	    return false;
	}
	if (this.fieldId != other.fieldId) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.OthersPK[ victimId=" + victimId + ", fieldId=" + fieldId + " ]";
    }
    
}
