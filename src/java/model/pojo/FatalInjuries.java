/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "fatal_injuries", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FatalInjuries.findAll", query = "SELECT f FROM FatalInjuries f"),
    @NamedQuery(name = "FatalInjuries.findByInjuryDate", query = "SELECT f FROM FatalInjuries f WHERE f.injuryDate = :injuryDate"),
    @NamedQuery(name = "FatalInjuries.findByInjuryTime", query = "SELECT f FROM FatalInjuries f WHERE f.injuryTime = :injuryTime"),
    @NamedQuery(name = "FatalInjuries.findByInjuryAddress", query = "SELECT f FROM FatalInjuries f WHERE f.injuryAddress = :injuryAddress"),
    @NamedQuery(name = "FatalInjuries.findByInjuryNeighborhoodId", query = "SELECT f FROM FatalInjuries f WHERE f.injuryNeighborhoodId = :injuryNeighborhoodId"),
    @NamedQuery(name = "FatalInjuries.findByVictimNumber", query = "SELECT f FROM FatalInjuries f WHERE f.victimNumber = :victimNumber"),
    @NamedQuery(name = "FatalInjuries.findByInjuryDescription", query = "SELECT f FROM FatalInjuries f WHERE f.injuryDescription = :injuryDescription"),
    @NamedQuery(name = "FatalInjuries.findByInputTimestamp", query = "SELECT f FROM FatalInjuries f WHERE f.inputTimestamp = :inputTimestamp"),
    @NamedQuery(name = "FatalInjuries.findByInjuryDayOfWeek", query = "SELECT f FROM FatalInjuries f WHERE f.injuryDayOfWeek = :injuryDayOfWeek"),
    @NamedQuery(name = "FatalInjuries.findByFatalInjuryId", query = "SELECT f FROM FatalInjuries f WHERE f.fatalInjuryId = :fatalInjuryId"),
    @NamedQuery(name = "FatalInjuries.findByAlcoholLevelVictim", query = "SELECT f FROM FatalInjuries f WHERE f.alcoholLevelVictim = :alcoholLevelVictim"),
    @NamedQuery(name = "FatalInjuries.findByCode", query = "SELECT f FROM FatalInjuries f WHERE f.code = :code")})
public class FatalInjuries implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "injury_date")
    @Temporal(TemporalType.DATE)
    private Date injuryDate;
    @Column(name = "injury_time")
    @Temporal(TemporalType.TIME)
    private Date injuryTime;
    @Size(max = 2147483647)
    @Column(name = "injury_address", length = 2147483647)
    private String injuryAddress;
    @Column(name = "injury_neighborhood_id")
    private Integer injuryNeighborhoodId;
    @Column(name = "victim_number")
    private Short victimNumber;
    @Size(max = 2147483647)
    @Column(name = "injury_description", length = 2147483647)
    private String injuryDescription;
    @Column(name = "input_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inputTimestamp;
    @Size(max = 10)
    @Column(name = "injury_day_of_week", length = 10)
    private String injuryDayOfWeek;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "fatal_injury_id", nullable = false)
    private Integer fatalInjuryId;
    @Column(name = "alcohol_level_victim")
    private Short alcoholLevelVictim;
    @Size(max = 2147483647)
    @Column(name = "code", length = 2147483647)
    private String code;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fatalInjuryId")
    private List<CounterpartServiceType> counterpartServiceTypeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fatalInjuryId")
    private List<CounterpartInvolvedVehicle> counterpartInvolvedVehicleList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "fatalInjuries")
    private FatalInjuryMurder fatalInjuryMurder;
    @JoinColumn(name = "quadrant_id", referencedColumnName = "quadrant_id")
    @ManyToOne
    private Quadrants quadrantId;
    @JoinColumn(name = "victim_id", referencedColumnName = "victim_id")
    @ManyToOne
    private Victims victimId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private Users userId;
    @JoinColumn(name = "injury_place_id", referencedColumnName = "place_id")
    @ManyToOne
    private Places injuryPlaceId;
    @JoinColumn(name = "injury_id", referencedColumnName = "injury_id", nullable = false)
    @ManyToOne(optional = false)
    private Injuries injuryId;
    @JoinColumn(name = "area_id", referencedColumnName = "area_id")
    @ManyToOne
    private Areas areaId;
    @JoinColumn(name = "alcohol_level_victim_id", referencedColumnName = "alcohol_level_id")
    @ManyToOne
    private AlcoholLevels alcoholLevelVictimId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "fatalInjuries")
    private FatalInjuryTraffic fatalInjuryTraffic;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "fatalInjuries")
    private FatalInjuryAccident fatalInjuryAccident;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "fatalInjuries")
    private FatalInjurySuicide fatalInjurySuicide;
    @Size(max = 2147483647)
    @Column(name = "victim_place_of_origin", length = 2147483647)
    private String victimPlaceOfOrigin;

    public FatalInjuries() {
    }

    public FatalInjuries(Integer fatalInjuryId) {
        this.fatalInjuryId = fatalInjuryId;
    }

    public Date getInjuryDate() {
        return injuryDate;
    }

    public void setInjuryDate(Date injuryDate) {
        this.injuryDate = injuryDate;
    }

    public Date getInjuryTime() {
        return injuryTime;
    }

    public void setInjuryTime(Date injuryTime) {
        this.injuryTime = injuryTime;
    }

    public String getInjuryAddress() {
        return injuryAddress;
    }

    public void setInjuryAddress(String injuryAddress) {
        this.injuryAddress = injuryAddress;
    }

    public Integer getInjuryNeighborhoodId() {
        return injuryNeighborhoodId;
    }

    public void setInjuryNeighborhoodId(Integer injuryNeighborhoodId) {
        this.injuryNeighborhoodId = injuryNeighborhoodId;
    }

    public Short getVictimNumber() {
        return victimNumber;
    }

    public void setVictimNumber(Short victimNumber) {
        this.victimNumber = victimNumber;
    }

    public String getInjuryDescription() {
        return injuryDescription;
    }

    public void setInjuryDescription(String injuryDescription) {
        this.injuryDescription = injuryDescription;
    }

    public Date getInputTimestamp() {
        return inputTimestamp;
    }

    public void setInputTimestamp(Date inputTimestamp) {
        this.inputTimestamp = inputTimestamp;
    }

    public String getInjuryDayOfWeek() {
        return injuryDayOfWeek;
    }

    public void setInjuryDayOfWeek(String injuryDayOfWeek) {
        this.injuryDayOfWeek = injuryDayOfWeek;
    }

    public Integer getFatalInjuryId() {
        return fatalInjuryId;
    }

    public void setFatalInjuryId(Integer fatalInjuryId) {
        this.fatalInjuryId = fatalInjuryId;
    }

    public Short getAlcoholLevelVictim() {
        return alcoholLevelVictim;
    }

    public void setAlcoholLevelVictim(Short alcoholLevelVictim) {
        this.alcoholLevelVictim = alcoholLevelVictim;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FatalInjuryMurder getFatalInjuryMurder() {
        return fatalInjuryMurder;
    }

    public void setFatalInjuryMurder(FatalInjuryMurder fatalInjuryMurder) {
        this.fatalInjuryMurder = fatalInjuryMurder;
    }

    public Victims getVictimId() {
        return victimId;
    }

    public void setVictimId(Victims victimId) {
        this.victimId = victimId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public Places getInjuryPlaceId() {
        return injuryPlaceId;
    }

    public void setInjuryPlaceId(Places injuryPlaceId) {
        this.injuryPlaceId = injuryPlaceId;
    }

    public Injuries getInjuryId() {
        return injuryId;
    }

    public void setInjuryId(Injuries injuryId) {
        this.injuryId = injuryId;
    }

    public Areas getAreaId() {
        return areaId;
    }

    public void setAreaId(Areas areaId) {
        this.areaId = areaId;
    }

    public AlcoholLevels getAlcoholLevelVictimId() {
        return alcoholLevelVictimId;
    }

    public void setAlcoholLevelVictimId(AlcoholLevels alcoholLevelVictimId) {
        this.alcoholLevelVictimId = alcoholLevelVictimId;
    }

    public FatalInjuryTraffic getFatalInjuryTraffic() {
        return fatalInjuryTraffic;
    }

    public void setFatalInjuryTraffic(FatalInjuryTraffic fatalInjuryTraffic) {
        this.fatalInjuryTraffic = fatalInjuryTraffic;
    }

    public FatalInjuryAccident getFatalInjuryAccident() {
        return fatalInjuryAccident;
    }

    public void setFatalInjuryAccident(FatalInjuryAccident fatalInjuryAccident) {
        this.fatalInjuryAccident = fatalInjuryAccident;
    }

    public FatalInjurySuicide getFatalInjurySuicide() {
        return fatalInjurySuicide;
    }

    public void setFatalInjurySuicide(FatalInjurySuicide fatalInjurySuicide) {
        this.fatalInjurySuicide = fatalInjurySuicide;
    }

    public String getVictimPlaceOfOrigin() {
        return victimPlaceOfOrigin;
    }

    public void setVictimPlaceOfOrigin(String victimPlaceOfOrigin) {
        this.victimPlaceOfOrigin = victimPlaceOfOrigin;
    }

    @XmlTransient
    public List<CounterpartServiceType> getCounterpartServiceTypeList() {
        return counterpartServiceTypeList;
    }

    public void setCounterpartServiceTypeList(List<CounterpartServiceType> counterpartServiceTypeList) {
        this.counterpartServiceTypeList = counterpartServiceTypeList;
    }

    @XmlTransient
    public List<CounterpartInvolvedVehicle> getCounterpartInvolvedVehicleList() {
        return counterpartInvolvedVehicleList;
    }

    public void setCounterpartInvolvedVehicleList(List<CounterpartInvolvedVehicle> counterpartInvolvedVehicleList) {
        this.counterpartInvolvedVehicleList = counterpartInvolvedVehicleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fatalInjuryId != null ? fatalInjuryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FatalInjuries)) {
            return false;
        }
        FatalInjuries other = (FatalInjuries) object;
        if ((this.fatalInjuryId == null && other.fatalInjuryId != null) || (this.fatalInjuryId != null && !this.fatalInjuryId.equals(other.fatalInjuryId))) {
            return false;
        }
        return true;
    }

    public Quadrants getQuadrantId() {
        return quadrantId;
    }

    public void setQuadrantId(Quadrants quadrantId) {
        this.quadrantId = quadrantId;
    }

    @Override
    public String toString() {
        return "model.pojo.FatalInjuries[ fatalInjuryId=" + fatalInjuryId + " ]";
    }
}
