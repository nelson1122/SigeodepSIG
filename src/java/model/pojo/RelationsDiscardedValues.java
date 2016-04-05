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
@Table(name = "relations_discarded_values", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RelationsDiscardedValues.findAll", query = "SELECT r FROM RelationsDiscardedValues r"),
    @NamedQuery(name = "RelationsDiscardedValues.findByIdDiscardedValue", query = "SELECT r FROM RelationsDiscardedValues r WHERE r.idDiscardedValue = :idDiscardedValue"),
    @NamedQuery(name = "RelationsDiscardedValues.findByDiscardedValueName", query = "SELECT r FROM RelationsDiscardedValues r WHERE r.discardedValueName = :discardedValueName")})
public class RelationsDiscardedValues implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_discarded_value", nullable = false)
    private Integer idDiscardedValue;
    @Size(max = 2147483647)
    @Column(name = "discarded_value_name", length = 2147483647)
    private String discardedValueName;
    //private String fieldType;
    //@Column(name = "discarded_value_name", length = 200)
    
    @JoinColumn(name = "id_relation_variables", referencedColumnName = "id_relation_variables")
    @ManyToOne
    private RelationVariables idRelationVariables;

    public RelationsDiscardedValues() {
    }

    public RelationsDiscardedValues(Integer idDiscardedValue) {
        this.idDiscardedValue = idDiscardedValue;
    }

    public Integer getIdDiscardedValue() {
        return idDiscardedValue;
    }

    public void setIdDiscardedValue(Integer idDiscardedValue) {
        this.idDiscardedValue = idDiscardedValue;
    }

    public String getDiscardedValueName() {
        return discardedValueName;
    }

    public void setDiscardedValueName(String discardedValueName) {
        this.discardedValueName = discardedValueName;
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
        hash += (idDiscardedValue != null ? idDiscardedValue.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RelationsDiscardedValues)) {
            return false;
        }
        RelationsDiscardedValues other = (RelationsDiscardedValues) object;
        if ((this.idDiscardedValue == null && other.idDiscardedValue != null) || (this.idDiscardedValue != null && !this.idDiscardedValue.equals(other.idDiscardedValue))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.RelationsDiscardedValues[ idDiscardedValue=" + idDiscardedValue + " ]";
    }
    
}
