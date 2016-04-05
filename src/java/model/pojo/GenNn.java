/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "gen_nn", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GenNn.findAll", query = "SELECT g FROM GenNn g"),
    @NamedQuery(name = "GenNn.findByCodNn", query = "SELECT g FROM GenNn g WHERE g.codNn = :codNn")})
public class GenNn implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_nn", nullable = false)
    private Integer codNn;

    public GenNn() {
    }

    public GenNn(Integer codNn) {
	this.codNn = codNn;
    }

    public Integer getCodNn() {
	return codNn;
    }

    public void setCodNn(Integer codNn) {
	this.codNn = codNn;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (codNn != null ? codNn.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof GenNn)) {
	    return false;
	}
	GenNn other = (GenNn) object;
	if ((this.codNn == null && other.codNn != null) || (this.codNn != null && !this.codNn.equals(other.codNn))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.GenNn[ codNn=" + codNn + " ]";
    }
    
}
