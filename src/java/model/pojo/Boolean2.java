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
@Table(name = "boolean2", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Boolean2.findAll", query = "SELECT b FROM Boolean2 b"),
    @NamedQuery(name = "Boolean2.findByBoolean2Id", query = "SELECT b FROM Boolean2 b WHERE b.boolean2Id = :boolean2Id"),
    @NamedQuery(name = "Boolean2.findByBoolean2Name", query = "SELECT b FROM Boolean2 b WHERE b.boolean2Name = :boolean2Name")})
public class Boolean2 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "boolean2_id", nullable = false)
    private Short boolean2Id;
    @Size(max = 2147483647)
    @Column(name = "boolean2_name", length = 2147483647)
    private String boolean2Name;

    public Boolean2() {
    }

    public Boolean2(Short boolean2Id) {
        this.boolean2Id = boolean2Id;
    }

    public Short getBoolean2Id() {
        return boolean2Id;
    }

    public void setBoolean2Id(Short boolean2Id) {
        this.boolean2Id = boolean2Id;
    }

    public String getBoolean2Name() {
        return boolean2Name;
    }

    public void setBoolean2Name(String boolean2Name) {
        this.boolean2Name = boolean2Name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (boolean2Id != null ? boolean2Id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Boolean2)) {
            return false;
        }
        Boolean2 other = (Boolean2) object;
        if ((this.boolean2Id == null && other.boolean2Id != null) || (this.boolean2Id != null && !this.boolean2Id.equals(other.boolean2Id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.Boolean2[ boolean2Id=" + boolean2Id + " ]";
    }
    
}
