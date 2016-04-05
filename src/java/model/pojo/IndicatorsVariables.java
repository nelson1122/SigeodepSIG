/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "indicators_variables", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IndicatorsVariables.findAll", query = "SELECT i FROM IndicatorsVariables i"),
    @NamedQuery(name = "IndicatorsVariables.findByVariableName", query = "SELECT i FROM IndicatorsVariables i WHERE i.indicatorsVariablesPK.variableName = :variableName"),
    @NamedQuery(name = "IndicatorsVariables.findByAddValues", query = "SELECT i FROM IndicatorsVariables i WHERE i.addValues = :addValues"),
    @NamedQuery(name = "IndicatorsVariables.findByCategory", query = "SELECT i FROM IndicatorsVariables i WHERE i.category = :category"),
    @NamedQuery(name = "IndicatorsVariables.findByIndicatorId", query = "SELECT i FROM IndicatorsVariables i WHERE i.indicatorsVariablesPK.indicatorId = :indicatorId")})
public class IndicatorsVariables implements Serializable {
    @Size(max = 2147483647)
    @Column(name = "source_table", length = 2147483647)
    private String sourceTable;
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected IndicatorsVariablesPK indicatorsVariablesPK;
    @Column(name = "add_values")
    private Boolean addValues;
    @Size(max = 2147483647)
    @Column(name = "category", length = 2147483647)
    private String category;
    @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Indicators indicators;

    public IndicatorsVariables() {
    }

    public IndicatorsVariables(IndicatorsVariablesPK indicatorsVariablesPK) {
        this.indicatorsVariablesPK = indicatorsVariablesPK;
    }

    public IndicatorsVariables(String variableName, int indicatorId) {
        this.indicatorsVariablesPK = new IndicatorsVariablesPK(variableName, indicatorId);
    }

    public IndicatorsVariablesPK getIndicatorsVariablesPK() {
        return indicatorsVariablesPK;
    }

    public void setIndicatorsVariablesPK(IndicatorsVariablesPK indicatorsVariablesPK) {
        this.indicatorsVariablesPK = indicatorsVariablesPK;
    }

    public Boolean getAddValues() {
        return addValues;
    }

    public void setAddValues(Boolean addValues) {
        this.addValues = addValues;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Indicators getIndicators() {
        return indicators;
    }

    public void setIndicators(Indicators indicators) {
        this.indicators = indicators;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (indicatorsVariablesPK != null ? indicatorsVariablesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IndicatorsVariables)) {
            return false;
        }
        IndicatorsVariables other = (IndicatorsVariables) object;
        if ((this.indicatorsVariablesPK == null && other.indicatorsVariablesPK != null) || (this.indicatorsVariablesPK != null && !this.indicatorsVariablesPK.equals(other.indicatorsVariablesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "newpackage.IndicatorsVariables[ indicatorsVariablesPK=" + indicatorsVariablesPK + " ]";
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }
    
}
