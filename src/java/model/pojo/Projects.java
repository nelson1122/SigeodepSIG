/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "projects", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Projects.findAll", query = "SELECT p FROM Projects p"),
    @NamedQuery(name = "Projects.findByProjectId", query = "SELECT p FROM Projects p WHERE p.projectId = :projectId"),
    @NamedQuery(name = "Projects.findByProjectName", query = "SELECT p FROM Projects p WHERE p.projectName = :projectName"),
    @NamedQuery(name = "Projects.findByUserId", query = "SELECT p FROM Projects p WHERE p.userId = :userId"),
    @NamedQuery(name = "Projects.findByFormId", query = "SELECT p FROM Projects p WHERE p.formId = :formId"),
    @NamedQuery(name = "Projects.findBySourceId", query = "SELECT p FROM Projects p WHERE p.sourceId = :sourceId"),
    @NamedQuery(name = "Projects.findByFileDelimiter", query = "SELECT p FROM Projects p WHERE p.fileDelimiter = :fileDelimiter"),
    @NamedQuery(name = "Projects.findByFileName", query = "SELECT p FROM Projects p WHERE p.fileName = :fileName"),
    @NamedQuery(name = "Projects.findByRelationGroupName", query = "SELECT p FROM Projects p WHERE p.relationGroupName = :relationGroupName")})
public class Projects implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "project_id", nullable = false)
    private Integer projectId;
    @Size(max = 2147483647)
    @Column(name = "project_name", length = 2147483647)
    private String projectName;
    @Column(name = "user_id")
    private Integer userId;
    @Size(max = 2147483647)
    @Column(name = "form_id", length = 2147483647)
    private String formId;
    @Column(name = "source_id")
    private Short sourceId;
    @Size(max = 3)
    @Column(name = "file_delimiter", length = 3)
    private String fileDelimiter;
    @Size(max = 2147483647)
    @Column(name = "file_name", length = 2147483647)
    private String fileName;
    @Size(max = 2147483647)
    @Column(name = "relation_group_name", length = 2147483647)
    private String relationGroupName;
    @Column(name = "start_column_id")
    private BigInteger startColumnId;
    @Column(name = "end_column_id")
    private BigInteger endColumnId;

    public Projects() {
    }

    public Projects(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Short getSourceId() {
        return sourceId;
    }

    public void setSourceId(Short sourceId) {
        this.sourceId = sourceId;
    }

    public String getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRelationGroupName() {
        return relationGroupName;
    }

    public void setRelationGroupName(String relationGroupName) {
        this.relationGroupName = relationGroupName;
    }
    
    public BigInteger getStartColumnId() {
        return startColumnId;
    }

    public void setStartColumnId(BigInteger startColumnId) {
        this.startColumnId = startColumnId;
    }

    public BigInteger getEndColumnId() {
        return endColumnId;
    }

    public void setEndColumnId(BigInteger endColumnId) {
        this.endColumnId = endColumnId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectId != null ? projectId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Projects)) {
            return false;
        }
        Projects other = (Projects) object;
        if ((this.projectId == null && other.projectId != null) || (this.projectId != null && !this.projectId.equals(other.projectId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Projects[ projectId=" + projectId + " ]";
    }
    
}
