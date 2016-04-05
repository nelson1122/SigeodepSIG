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
@Table(name = "corridors", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Corridors.findAll", query = "SELECT c FROM Corridors c"),
    @NamedQuery(name = "Corridors.findByCorridorId", query = "SELECT c FROM Corridors c WHERE c.corridorId = :corridorId"),
    @NamedQuery(name = "Corridors.findByCorridorName", query = "SELECT c FROM Corridors c WHERE c.corridorName = :corridorName"),
    @NamedQuery(name = "Corridors.findByPopulation", query = "SELECT c FROM Corridors c WHERE c.population = :population"),
    @NamedQuery(name = "Corridors.findByGeom", query = "SELECT c FROM Corridors c WHERE c.geom = :geom")})
public class Corridors implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "corridor_id", nullable = false)
    private Short corridorId;
    @Size(max = 200)
    @Column(name = "corridor_name", length = 200)
    private String corridorName;
    @Column(name = "population")
    private Integer population;
    @Size(max = 2147483647)
    @Column(name = "geom", length = 2147483647)
    private String geom;

    public Corridors() {
    }

    public Corridors(Short corridorId) {
        this.corridorId = corridorId;
    }

    public Short getCorridorId() {
        return corridorId;
    }

    public void setCorridorId(Short corridorId) {
        this.corridorId = corridorId;
    }

    public String getCorridorName() {
        return corridorName;
    }

    public void setCorridorName(String corridorName) {
        this.corridorName = corridorName;
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
        hash += (corridorId != null ? corridorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Corridors)) {
            return false;
        }
        Corridors other = (Corridors) object;
        if ((this.corridorId == null && other.corridorId != null) || (this.corridorId != null && !this.corridorId.equals(other.corridorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.Corridors[ corridorId=" + corridorId + " ]";
    }
    
}
