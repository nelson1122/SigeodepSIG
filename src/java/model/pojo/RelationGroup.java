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
@Table(name = "relation_group", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RelationGroup.findAll", query = "SELECT r FROM RelationGroup r"),
    @NamedQuery(name = "RelationGroup.findByIdRelationGroup", query = "SELECT r FROM RelationGroup r WHERE r.idRelationGroup = :idRelationGroup"),
    @NamedQuery(name = "RelationGroup.findByNameRelationGroup", query = "SELECT r FROM RelationGroup r WHERE r.nameRelationGroup = :nameRelationGroup"),
    @NamedQuery(name = "RelationGroup.findBySourceId", query = "SELECT r FROM RelationGroup r WHERE r.sourceId = :sourceId")})
public class RelationGroup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_relation_group", nullable = false)
    private Integer idRelationGroup;
    @Size(max = 2147483647)
    @Column(name = "name_relation_group", length = 2147483647)
    private String nameRelationGroup;
    @Column(name = "source_id")
    private Integer sourceId;
    @Column(name = "user_id")
    private Integer userId;
    @JoinColumn(name = "form_id", referencedColumnName = "form_id")
    @ManyToOne
    private Forms formId;
    @OneToMany(mappedBy = "idRelationGroup")
    private List<RelationVariables> relationVariablesList;

    public RelationGroup() {
    }

    public RelationGroup(Integer idRelationGroup) {
        this.idRelationGroup = idRelationGroup;
    }

    public Integer getIdRelationGroup() {
        return idRelationGroup;
    }

    public void setIdRelationGroup(Integer idRelationGroup) {
        this.idRelationGroup = idRelationGroup;
    }

    public String getNameRelationGroup() {
        return nameRelationGroup;
    }

    public void setNameRelationGroup(String nameRelationGroup) {
        this.nameRelationGroup = nameRelationGroup;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }
    
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Forms getFormId() {
        return formId;
    }

    public void setFormId(Forms formId) {
        this.formId = formId;
    }

    @XmlTransient
    public List<RelationVariables> getRelationVariablesList() {
        return relationVariablesList;
    }

    public void setRelationVariablesList(List<RelationVariables> relationVariablesList) {
        this.relationVariablesList = relationVariablesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRelationGroup != null ? idRelationGroup.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RelationGroup)) {
            return false;
        }
        RelationGroup other = (RelationGroup) object;
        if ((this.idRelationGroup == null && other.idRelationGroup != null) || (this.idRelationGroup != null && !this.idRelationGroup.equals(other.idRelationGroup))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.RelationGroup[ idRelationGroup=" + idRelationGroup + " ]";
    }

    public RelationVariables findRelationVar(String currentVariableExpected, String currentVariableFound) {
        if (relationVariablesList != null) {
            for (int i = 0; i < relationVariablesList.size(); i++) {
                if (relationVariablesList.get(i).getNameExpected().compareTo(currentVariableExpected) == 0
                        && relationVariablesList.get(i).getNameFound().compareTo(currentVariableFound) == 0) {
                    return relationVariablesList.get(i);
                }
            }
        }
        return null;
    }

    public RelationVariables findRelationVarByNameFound(String nameFound) {
        if (relationVariablesList != null) {
            for (int i = 0; i < relationVariablesList.size(); i++) {
                if (relationVariablesList.get(i).getNameFound().compareTo(nameFound) == 0) {
                    return relationVariablesList.get(i);
                }
            }
        }
        return null;
    }
    public RelationVariables findRelationVarByNameExpected(String nameExpected) {
        if (relationVariablesList != null) {
            for (int i = 0; i < relationVariablesList.size(); i++) {
                if (relationVariablesList.get(i).getNameExpected().compareTo(nameExpected) == 0) {
                    return relationVariablesList.get(i);
                }
            }
        }
        return null;
    }

//    public String findRelationVarDescription(String currentVarFound) {
//        if (relationVariablesList != null) {
//            for (int i = 0; i < relationVariablesList.size(); i++) {
//                if (relationVariablesList.get(i).getNameFound().compareTo(currentVarFound) == 0) {
//                    return relationVariablesList.get(i);
//                }
//            }
//        }
//        return null;
//    }
}
