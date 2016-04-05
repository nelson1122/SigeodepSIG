/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author SANTOS
 */
@Embeddable
public class IndicatorsVariablesPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "variable_name", nullable = false, length = 2147483647)
    private String variableName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    public IndicatorsVariablesPK() {
    }

    public IndicatorsVariablesPK(String variableName, int indicatorId) {
        this.variableName = variableName;
        this.indicatorId = indicatorId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public int getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(int indicatorId) {
        this.indicatorId = indicatorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (variableName != null ? variableName.hashCode() : 0);
        hash += (int) indicatorId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IndicatorsVariablesPK)) {
            return false;
        }
        IndicatorsVariablesPK other = (IndicatorsVariablesPK) object;
        if ((this.variableName == null && other.variableName != null) || (this.variableName != null && !this.variableName.equals(other.variableName))) {
            return false;
        }
        if (this.indicatorId != other.indicatorId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "newpackage.IndicatorsVariablesPK[ variableName=" + variableName + ", indicatorId=" + indicatorId + " ]";
    }
    
}
