/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
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
@Table(name = "communes", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Communes.findAll", query = "SELECT c FROM Communes c"),
    @NamedQuery(name = "Communes.findByCommuneId", query = "SELECT c FROM Communes c WHERE c.communeId = :communeId"),
    @NamedQuery(name = "Communes.findByCommuneName", query = "SELECT c FROM Communes c WHERE c.communeName = :communeName"),
    @NamedQuery(name = "Communes.findByPopulation", query = "SELECT c FROM Communes c WHERE c.population = :population"),
    @NamedQuery(name = "Communes.findByAreaId", query = "SELECT c FROM Communes c WHERE c.areaId = :areaId"),
    @NamedQuery(name = "Communes.findByGeom", query = "SELECT c FROM Communes c WHERE c.geom = :geom")})
public class Communes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "commune_id", nullable = false)
    private Short communeId;
    @Size(max = 100)
    @Column(name = "commune_name", length = 100)
    private String communeName;
    @Column(name = "population")
    private Integer population;
    @Column(name = "area_id")
    private Short areaId;
    @Size(max = 2147483647)
    @Column(name = "geom", length = 2147483647)
    private String geom;

    public Communes() {
    }

    public Communes(Short communeId) {
        this.communeId = communeId;
    }

    public Short getCommuneId() {
        return communeId;
    }

    public void setCommuneId(Short communeId) {
        this.communeId = communeId;
    }

    public String getCommuneName() {
        return communeName;
    }

    public void setCommuneName(String communeName) {
        this.communeName = communeName;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Short getAreaId() {
        return areaId;
    }

    public void setAreaId(Short areaId) {
        this.areaId = areaId;
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
        hash += (communeId != null ? communeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Communes)) {
            return false;
        }
        Communes other = (Communes) object;
        if ((this.communeId == null && other.communeId != null) || (this.communeId != null && !this.communeId.equals(other.communeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.Communes[ communeId=" + communeId + " ]";
    }
    
}
