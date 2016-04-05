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
@Table(name = "months", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Months.findAll", query = "SELECT m FROM Months m"),
    @NamedQuery(name = "Months.findByMonthId", query = "SELECT m FROM Months m WHERE m.monthId = :monthId"),
    @NamedQuery(name = "Months.findByMonthName", query = "SELECT m FROM Months m WHERE m.monthName = :monthName")})
public class Months implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "month_id", nullable = false)
    private Short monthId;
    @Size(max = 10)
    @Column(name = "month_name", length = 10)
    private String monthName;

    public Months() {
    }

    public Months(Short monthId) {
        this.monthId = monthId;
    }

    public Short getMonthId() {
        return monthId;
    }

    public void setMonthId(Short monthId) {
        this.monthId = monthId;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (monthId != null ? monthId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Months)) {
            return false;
        }
        Months other = (Months) object;
        if ((this.monthId == null && other.monthId != null) || (this.monthId != null && !this.monthId.equals(other.monthId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Months[ monthId=" + monthId + " ]";
    }
    
}
