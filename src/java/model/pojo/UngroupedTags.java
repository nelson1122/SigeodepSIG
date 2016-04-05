/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "ungrouped_tags", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UngroupedTags.findAll", query = "SELECT u FROM UngroupedTags u"),
    @NamedQuery(name = "UngroupedTags.findByUngroupedTagId", query = "SELECT u FROM UngroupedTags u WHERE u.ungroupedTagId = :ungroupedTagId"),
    @NamedQuery(name = "UngroupedTags.findByUngroupedTagName", query = "SELECT u FROM UngroupedTags u WHERE u.ungroupedTagName = :ungroupedTagName"),
    @NamedQuery(name = "UngroupedTags.findByUngroupedTagDate", query = "SELECT u FROM UngroupedTags u WHERE u.ungroupedTagDate = :ungroupedTagDate")})
public class UngroupedTags implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ungrouped_tag_id", nullable = false)
    private Integer ungroupedTagId;
    @Size(max = 2147483647)
    @Column(name = "ungrouped_tag_name", length = 2147483647)
    private String ungroupedTagName;
    
    @Column(name = "current_tag_id")
    private Integer currentTagId;
    
    
    @Column(name = "ungrouped_tag_date")
    @Temporal(TemporalType.DATE)
    private Date ungroupedTagDate;
    @Size(max = 12)
    @Column(name = "form_id", length = 12)
    private String formId;

    public UngroupedTags() {
    }

    public UngroupedTags(Integer ungroupedTagId) {
        this.ungroupedTagId = ungroupedTagId;
    }

    public Integer getUngroupedTagId() {
        return ungroupedTagId;
    }

    public void setUngroupedTagId(Integer ungroupedTagId) {
        this.ungroupedTagId = ungroupedTagId;
    }

    public String getUngroupedTagName() {
        return ungroupedTagName;
    }

    public void setUngroupedTagName(String ungroupedTagName) {
        this.ungroupedTagName = ungroupedTagName;
    }

    public Date getUngroupedTagDate() {
        return ungroupedTagDate;
    }

    public void setUngroupedTagDate(Date ungroupedTagDate) {
        this.ungroupedTagDate = ungroupedTagDate;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Integer getCurrentTagId() {
        return currentTagId;
    }

    public void setCurrentTagId(Integer currentTagId) {
        this.currentTagId = currentTagId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ungroupedTagId != null ? ungroupedTagId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UngroupedTags)) {
            return false;
        }
        UngroupedTags other = (UngroupedTags) object;
        if ((this.ungroupedTagId == null && other.ungroupedTagId != null) || (this.ungroupedTagId != null && !this.ungroupedTagId.equals(other.ungroupedTagId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.UngroupedTags[ ungroupedTagId=" + ungroupedTagId + " ]";
    }
    
}
