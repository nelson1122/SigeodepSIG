/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "others", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Others.findAll", query = "SELECT o FROM Others o"),
    @NamedQuery(name = "Others.findByVictimId", query = "SELECT o FROM Others o WHERE o.othersPK.victimId = :victimId"),
    @NamedQuery(name = "Others.findByFieldId", query = "SELECT o FROM Others o WHERE o.othersPK.fieldId = :fieldId"),
    @NamedQuery(name = "Others.findByValueText", query = "SELECT o FROM Others o WHERE o.valueText = :valueText")})
public class Others implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OthersPK othersPK;
    @Size(max = 2147483647)
    @Column(name = "value_text", length = 2147483647)
    private String valueText;
    @JoinColumn(name = "victim_id", referencedColumnName = "victim_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Victims victims;

    public Others() {
    }

    public Others(OthersPK othersPK) {
	this.othersPK = othersPK;
    }

    public Others(int victimId, short fieldId) {
	this.othersPK = new OthersPK(victimId, fieldId);
    }

    public OthersPK getOthersPK() {
	return othersPK;
    }

    public void setOthersPK(OthersPK othersPK) {
	this.othersPK = othersPK;
    }

    public String getValueText() {
	return valueText;
    }

    public void setValueText(String valueText) {
	this.valueText = valueText;
    }

    public Victims getVictims() {
	return victims;
    }

    public void setVictims(Victims victims) {
	this.victims = victims;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (othersPK != null ? othersPK.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Others)) {
	    return false;
	}
	Others other = (Others) object;
	if ((this.othersPK == null && other.othersPK != null) || (this.othersPK != null && !this.othersPK.equals(other.othersPK))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Others[ othersPK=" + othersPK + " ]";
    }
    
}
