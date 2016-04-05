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
@Table(name = "counterpart_service_type", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CounterpartServiceType.findAll", query = "SELECT c FROM CounterpartServiceType c"),
    @NamedQuery(name = "CounterpartServiceType.findByCounterpartServiceTypeId", query = "SELECT c FROM CounterpartServiceType c WHERE c.counterpartServiceTypeId = :counterpartServiceTypeId")})
public class CounterpartServiceType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "counterpart_service_type_id", nullable = false)
    private Integer counterpartServiceTypeId;
    @JoinColumn(name = "service_type_id", referencedColumnName = "service_type_id", nullable = false)
    @ManyToOne(optional = false)
    private ServiceTypes serviceTypeId;
    @JoinColumn(name = "fatal_injury_id", referencedColumnName = "fatal_injury_id", nullable = false)
    @ManyToOne(optional = false)
    private FatalInjuries fatalInjuryId;

    public CounterpartServiceType() {
    }

    public CounterpartServiceType(Integer counterpartServiceTypeId) {
        this.counterpartServiceTypeId = counterpartServiceTypeId;
    }

    public Integer getCounterpartServiceTypeId() {
        return counterpartServiceTypeId;
    }

    public void setCounterpartServiceTypeId(Integer counterpartServiceTypeId) {
        this.counterpartServiceTypeId = counterpartServiceTypeId;
    }

    public ServiceTypes getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(ServiceTypes serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public FatalInjuries getFatalInjuryId() {
        return fatalInjuryId;
    }

    public void setFatalInjuryId(FatalInjuries fatalInjuryId) {
        this.fatalInjuryId = fatalInjuryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (counterpartServiceTypeId != null ? counterpartServiceTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CounterpartServiceType)) {
            return false;
        }
        CounterpartServiceType other = (CounterpartServiceType) object;
        if ((this.counterpartServiceTypeId == null && other.counterpartServiceTypeId != null) || (this.counterpartServiceTypeId != null && !this.counterpartServiceTypeId.equals(other.counterpartServiceTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.CounterpartServiceType[ counterpartServiceTypeId=" + counterpartServiceTypeId + " ]";
    }
    
}
