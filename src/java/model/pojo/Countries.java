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
 * @author santos
 */
@Entity
@Table(name = "countries", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Countries.findAll", query = "SELECT c FROM Countries c"),
    @NamedQuery(name = "Countries.findByIdCountry", query = "SELECT c FROM Countries c WHERE c.idCountry = :idCountry"),
    @NamedQuery(name = "Countries.findByIso1", query = "SELECT c FROM Countries c WHERE c.iso1 = :iso1"),
    @NamedQuery(name = "Countries.findByIso2", query = "SELECT c FROM Countries c WHERE c.iso2 = :iso2"),
    @NamedQuery(name = "Countries.findByIso3", query = "SELECT c FROM Countries c WHERE c.iso3 = :iso3"),
    @NamedQuery(name = "Countries.findByName", query = "SELECT c FROM Countries c WHERE c.name = :name")})
public class Countries implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_country", nullable = false)
    private Short idCountry;
    @Column(name = "iso1")
    private Short iso1;
    @Size(max = 2)
    @Column(name = "iso2", length = 2)
    private String iso2;
    @Size(max = 3)
    @Column(name = "iso3", length = 3)
    private String iso3;
    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    public Countries() {
    }

    public Countries(Short idCountry) {
        this.idCountry = idCountry;
    }

    public Short getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(Short idCountry) {
        this.idCountry = idCountry;
    }

    public Short getIso1() {
        return iso1;
    }

    public void setIso1(Short iso1) {
        this.iso1 = iso1;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCountry != null ? idCountry.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Countries)) {
            return false;
        }
        Countries other = (Countries) object;
        if ((this.idCountry == null && other.idCountry != null) || (this.idCountry != null && !this.idCountry.equals(other.idCountry))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Countries[ idCountry=" + idCountry + " ]";
    }
    
}
