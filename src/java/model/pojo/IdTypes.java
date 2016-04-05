/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "id_types", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"type_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IdTypes.findAll", query = "SELECT i FROM IdTypes i"),
    @NamedQuery(name = "IdTypes.findByTypeId", query = "SELECT i FROM IdTypes i WHERE i.typeId = :typeId"),
    @NamedQuery(name = "IdTypes.findByTypeName", query = "SELECT i FROM IdTypes i WHERE i.typeName = :typeName")})
public class IdTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "type_id", nullable = false)
    private Short typeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;
    @OneToMany(mappedBy = "typeId")
    private List<Victims> victimsList;

    public IdTypes() {
    }

    public IdTypes(Short typeId) {
	this.typeId = typeId;
    }

    public IdTypes(Short typeId, String typeName) {
	this.typeId = typeId;
	this.typeName = typeName;
    }

    public Short getTypeId() {
	return typeId;
    }

    public void setTypeId(Short typeId) {
	this.typeId = typeId;
    }

    public String getTypeName() {
	return typeName;
    }

    public void setTypeName(String typeName) {
	this.typeName = typeName;
    }

    @XmlTransient
    public List<Victims> getVictimsList() {
	return victimsList;
    }

    public void setVictimsList(List<Victims> victimsList) {
	this.victimsList = victimsList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (typeId != null ? typeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof IdTypes)) {
	    return false;
	}
	IdTypes other = (IdTypes) object;
	if ((this.typeId == null && other.typeId != null) || (this.typeId != null && !this.typeId.equals(other.typeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.IdTypes[ typeId=" + typeId + " ]";
    }
    
}
