/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "quadrants", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Quadrants.findAll", query = "SELECT q FROM Quadrants q"),
    @NamedQuery(name = "Quadrants.findByQuadrantId", query = "SELECT q FROM Quadrants q WHERE q.quadrantId = :quadrantId"),
    @NamedQuery(name = "Quadrants.findByQuadrantName", query = "SELECT q FROM Quadrants q WHERE q.quadrantName = :quadrantName"),
    @NamedQuery(name = "Quadrants.findByPopulation", query = "SELECT q FROM Quadrants q WHERE q.population = :population"),
    @NamedQuery(name = "Quadrants.findByGeom", query = "SELECT q FROM Quadrants q WHERE q.geom = :geom")})
public class Quadrants implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "quadrant_id", nullable = false)
    private Integer quadrantId;
    @Size(max = 2147483647)
    @Column(name = "quadrant_name", length = 2147483647)
    private String quadrantName;
    @Column(name = "population")
    private Integer population;
    @Size(max = 2147483647)
    @Column(name = "geom", length = 2147483647)
    private String geom;
    @OneToMany(mappedBy = "quadrantId")
    private List<FatalInjuries> fatalInjuriesList;
    @OneToMany(mappedBy = "quadrantId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public Quadrants() {
    }

    public Quadrants(Integer quadrantId) {
        this.quadrantId = quadrantId;
    }

    public Integer getQuadrantId() {
        return quadrantId;
    }

    public void setQuadrantId(Integer quadrantId) {
        this.quadrantId = quadrantId;
    }

    public String getQuadrantName() {
        return quadrantName;
    }

    public void setQuadrantName(String quadrantName) {
        this.quadrantName = quadrantName;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (quadrantId != null ? quadrantId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Quadrants)) {
            return false;
        }
        Quadrants other = (Quadrants) object;
        if ((this.quadrantId == null && other.quadrantId != null) || (this.quadrantId != null && !this.quadrantId.equals(other.quadrantId))) {
            return false;
        }
        return true;
    }
    
    @XmlTransient
    public List<FatalInjuries> getFatalInjuriesList() {
        return fatalInjuriesList;
    }

    public void setFatalInjuriesList(List<FatalInjuries> fatalInjuriesList) {
        this.fatalInjuriesList = fatalInjuriesList;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
        return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
        this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @Override
    public String toString() {
        return "pojos.Quadrants[ quadrantId=" + quadrantId + " ]";
    }
    
}
