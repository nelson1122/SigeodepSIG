/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "tags", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tags.findAll", query = "SELECT t FROM Tags t"),
    @NamedQuery(name = "Tags.findByTagId", query = "SELECT t FROM Tags t WHERE t.tagId = :tagId"),
    @NamedQuery(name = "Tags.findByTagName", query = "SELECT t FROM Tags t WHERE t.tagName = :tagName"),
    @NamedQuery(name = "Tags.findByTagFileInput", query = "SELECT t FROM Tags t WHERE t.tagFileInput = :tagFileInput"),
    @NamedQuery(name = "Tags.findByTagFileStored", query = "SELECT t FROM Tags t WHERE t.tagFileStored = :tagFileStored")})
public class Tags implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "tag_id", nullable = false)
    private Integer tagId;
    @Size(max = 100)
    @Column(name = "tag_name", length = 100)
    private String tagName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "tag_file_input", nullable = false, length = 2147483647)
    private String tagFileInput;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "tag_file_stored", nullable = false, length = 2147483647)
    private String tagFileStored;
    @JoinColumn(name = "form_id", referencedColumnName = "form_id")
    @ManyToOne
    private Forms formId;
    @OneToMany(mappedBy = "tagId")
    private List<Victims> victimsList;    
    
    public Tags() {
    }

    public Tags(Integer tagId) {
	this.tagId = tagId;
    }

    public Tags(Integer tagId, String tagFileInput, String tagFileStored) {
	this.tagId = tagId;
	this.tagFileInput = tagFileInput;
	this.tagFileStored = tagFileStored;
    }

    public Integer getTagId() {
	return tagId;
    }

    public void setTagId(Integer tagId) {
	this.tagId = tagId;
    }

    public String getTagName() {
	return tagName;
    }

    public void setTagName(String tagName) {
	this.tagName = tagName;
    }

    public String getTagFileInput() {
	return tagFileInput;
    }

    public void setTagFileInput(String tagFileInput) {
	this.tagFileInput = tagFileInput;
    }

    public String getTagFileStored() {
	return tagFileStored;
    }

    public void setTagFileStored(String tagFileStored) {
	this.tagFileStored = tagFileStored;
    }

    public Forms getFormId() {
        return formId;
    }

    public void setFormId(Forms formId) {
        this.formId = formId;
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
	hash += (tagId != null ? tagId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Tags)) {
	    return false;
	}
	Tags other = (Tags) object;
	if ((this.tagId == null && other.tagId != null) || (this.tagId != null && !this.tagId.equals(other.tagId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Tags[ tagId=" + tagId + " ]";
    }
    
}
