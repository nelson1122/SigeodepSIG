/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "relation_values", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RelationValues.findAll", query = "SELECT r FROM RelationValues r"),
    @NamedQuery(name = "RelationValues.findByIdRelationValues", query = "SELECT r FROM RelationValues r WHERE r.idRelationValues = :idRelationValues"),
    @NamedQuery(name = "RelationValues.findByNameExpected", query = "SELECT r FROM RelationValues r WHERE r.nameExpected = :nameExpected"),
    @NamedQuery(name = "RelationValues.findByNameFound", query = "SELECT r FROM RelationValues r WHERE r.nameFound = :nameFound")})
public class RelationValues implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_relation_values", nullable = false)
    private Integer idRelationValues;
    @Size(max = 2147483647)
    @Column(name = "name_expected", length = 2147483647)
    private String nameExpected;
    @Size(max = 2147483647)
    @Column(name = "name_found", length = 2147483647)
    private String nameFound;
    @JoinColumn(name = "id_relation_variables", referencedColumnName = "id_relation_variables")
    @ManyToOne
    private RelationVariables idRelationVariables;

    public RelationValues() {
    }

    public RelationValues(Integer idRelationValues) {
	this.idRelationValues = idRelationValues;
    }

    public Integer getIdRelationValues() {
	return idRelationValues;
    }

    public void setIdRelationValues(Integer idRelationValues) {
	this.idRelationValues = idRelationValues;
    }

    public String getNameExpected() {
	return nameExpected;
    }

    public void setNameExpected(String nameExpected) {
	this.nameExpected = nameExpected;
    }

    public String getNameFound() {
	return nameFound;
    }

    public void setNameFound(String nameFound) {
	this.nameFound = nameFound;
    }

    public RelationVariables getIdRelationVariables() {
	return idRelationVariables;
    }

    public void setIdRelationVariables(RelationVariables idRelationVariables) {
	this.idRelationVariables = idRelationVariables;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (idRelationValues != null ? idRelationValues.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof RelationValues)) {
	    return false;
	}
	RelationValues other = (RelationValues) object;
	if ((this.idRelationValues == null && other.idRelationValues != null) || (this.idRelationValues != null && !this.idRelationValues.equals(other.idRelationValues))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.RelationValues[ idRelationValues=" + idRelationValues + " ]";
    }
    
}
