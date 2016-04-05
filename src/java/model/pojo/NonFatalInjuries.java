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
@Table(name = "non_fatal_injuries", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NonFatalInjuries.findAll", query = "SELECT n FROM NonFatalInjuries n"),
    @NamedQuery(name = "NonFatalInjuries.findByCheckupDate", query = "SELECT n FROM NonFatalInjuries n WHERE n.checkupDate = :checkupDate"),
    @NamedQuery(name = "NonFatalInjuries.findByCheckupTime", query = "SELECT n FROM NonFatalInjuries n WHERE n.checkupTime = :checkupTime"),
    @NamedQuery(name = "NonFatalInjuries.findByInjuryDate", query = "SELECT n FROM NonFatalInjuries n WHERE n.injuryDate = :injuryDate"),
    @NamedQuery(name = "NonFatalInjuries.findByInjuryTime", query = "SELECT n FROM NonFatalInjuries n WHERE n.injuryTime = :injuryTime"),
    @NamedQuery(name = "NonFatalInjuries.findByInjuryAddress", query = "SELECT n FROM NonFatalInjuries n WHERE n.injuryAddress = :injuryAddress"),
    @NamedQuery(name = "NonFatalInjuries.findByBurnInjuryDegree", query = "SELECT n FROM NonFatalInjuries n WHERE n.burnInjuryDegree = :burnInjuryDegree"),
    @NamedQuery(name = "NonFatalInjuries.findByBurnInjuryPercentage", query = "SELECT n FROM NonFatalInjuries n WHERE n.burnInjuryPercentage = :burnInjuryPercentage"),
    @NamedQuery(name = "NonFatalInjuries.findBySubmittedPatient", query = "SELECT n FROM NonFatalInjuries n WHERE n.submittedPatient = :submittedPatient"),
    @NamedQuery(name = "NonFatalInjuries.findByInputTimestamp", query = "SELECT n FROM NonFatalInjuries n WHERE n.inputTimestamp = :inputTimestamp"),
    @NamedQuery(name = "NonFatalInjuries.findByInjuryDayOfWeek", query = "SELECT n FROM NonFatalInjuries n WHERE n.injuryDayOfWeek = :injuryDayOfWeek"),
    @NamedQuery(name = "NonFatalInjuries.findByNonFatalInjuryId", query = "SELECT n FROM NonFatalInjuries n WHERE n.nonFatalInjuryId = :nonFatalInjuryId")})
public class NonFatalInjuries implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "checkup_date")
    @Temporal(TemporalType.DATE)
    private Date checkupDate;
    @Column(name = "checkup_time")
    @Temporal(TemporalType.TIME)
    private Date checkupTime;
    @Column(name = "injury_date")
    @Temporal(TemporalType.DATE)
    private Date injuryDate;
    @Column(name = "injury_time")
    @Temporal(TemporalType.TIME)
    private Date injuryTime;
    @Size(max = 300)
    @Column(name = "injury_address", length = 300)
    private String injuryAddress;
    @Column(name = "burn_injury_degree")
    private Short burnInjuryDegree;
    @Column(name = "burn_injury_percentage")
    private Short burnInjuryPercentage;
    @Column(name = "submitted_patient")
    private Boolean submittedPatient;
    @Column(name = "input_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inputTimestamp;
    @Size(max = 10)
    @Column(name = "injury_day_of_week", length = 10)
    private String injuryDayOfWeek;
    //@Column(name = "residence_departament")
    //private Short residenceDepartament;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_injury_id", nullable = false)
    private Integer nonFatalInjuryId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "nonFatalInjuries")
    private NonFatalInterpersonal nonFatalInterpersonal;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "nonFatalInjuries")
    private NonFatalTransport nonFatalTransport;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "nonFatalInjuries")
    private NonFatalSelfInflicted nonFatalSelfInflicted;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "nonFatalInjuries")
    private NonFatalDomesticViolence nonFatalDomesticViolence;
    @JoinColumn(name = "victim_id", referencedColumnName = "victim_id")
    @ManyToOne
    private Victims victimId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private Users userId;
    @JoinColumn(name = "activity_id", referencedColumnName = "activity_id")
    @ManyToOne
    private Activities activityId;
    @JoinColumn(name = "destination_patient_id", referencedColumnName = "destination_patient_id")
    @ManyToOne
    private DestinationsOfPatient destinationPatientId;
    @JoinColumn(name = "health_professional_id", referencedColumnName = "health_professional_id")
    @ManyToOne
    private HealthProfessionals healthProfessionalId;
    @JoinColumn(name = "injury_id", referencedColumnName = "injury_id", nullable = false)
    @ManyToOne(optional = false)
    private Injuries injuryId;
    @JoinColumn(name = "intentionality_id", referencedColumnName = "intentionality_id")
    @ManyToOne
    private Intentionalities intentionalityId;
    @JoinColumn(name = "mechanism_id", referencedColumnName = "mechanism_id")
    @ManyToOne
    private Mechanisms mechanismId;
    @JoinColumn(name = "injury_neighborhood_id", referencedColumnName = "neighborhood_id")
    @ManyToOne
    private Neighborhoods injuryNeighborhoodId;
    @JoinColumn(name = "non_fatal_data_source_id", referencedColumnName = "non_fatal_data_source_id")
    @ManyToOne
    private NonFatalDataSources nonFatalDataSourceId;
    @JoinColumn(name = "submitted_data_source_id", referencedColumnName = "non_fatal_data_source_id")
    @ManyToOne
    private NonFatalDataSources submittedDataSourceId;
    @JoinColumn(name = "injury_place_id", referencedColumnName = "non_fatal_place_id")
    @ManyToOne
    private NonFatalPlaces injuryPlaceId;
    @JoinColumn(name = "use_drugs_id", referencedColumnName = "use_alcohol_drugs_id")
    @ManyToOne
    private UseAlcoholDrugs useDrugsId;
    @JoinColumn(name = "use_alcohol_id", referencedColumnName = "use_alcohol_drugs_id")
    @ManyToOne
    private UseAlcoholDrugs useAlcoholId;
    //@ManyToMany(mappedBy = "nonFatalInjuriesList")
    //private List<Diagnoses> diagnosesList;
    @JoinTable(name = "non_fatal_diagnosis", joinColumns = {
        @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "diagnosis_id", referencedColumnName = "diagnosis_id", nullable = false)})
    @ManyToMany
    private List<Diagnoses> diagnosesList;
    @JoinTable(name = "non_fatal_anatomical_location", joinColumns = {
        @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "anatomical_location_id", referencedColumnName = "anatomical_location_id", nullable = false)})
    @ManyToMany
    private List<AnatomicalLocations> anatomicalLocationsList;
    @JoinTable(name = "non_fatal_kind_of_injury", joinColumns = {
        @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "kind_injury_id", referencedColumnName = "kind_injury_id", nullable = false)})
    @ManyToMany
    private List<KindsOfInjury> kindsOfInjuryList;
    @JoinColumn(name = "quadrant_id", referencedColumnName = "quadrant_id")
    @ManyToOne
    private Quadrants quadrantId;

//    @JoinColumn(name = "id_state_checkup_date", referencedColumnName = "id_state_date")
//    @ManyToOne
//    private StateDate idStateCheckupDate;
//    @JoinColumn(name = "id_state_injury_date", referencedColumnName = "id_state_date")
//    @ManyToOne
//    private StateDate idStateInjuryDate;
//    @JoinColumn(name = "id_state_checkup_time", referencedColumnName = "id_state_time")
//    @ManyToOne
//    private StateTime idStateCheckupTime;
//    @JoinColumn(name = "id_state_injury_time", referencedColumnName = "id_state_time")
//    @ManyToOne
//    private StateTime idStateInjuryTime;
    public NonFatalInjuries() {
    }

    public NonFatalInjuries(Integer nonFatalInjuryId) {
        this.nonFatalInjuryId = nonFatalInjuryId;
    }

    public Date getCheckupDate() {
        return checkupDate;
    }

    public void setCheckupDate(Date checkupDate) {
        this.checkupDate = checkupDate;
    }

    public Date getCheckupTime() {
        return checkupTime;
    }

    public void setCheckupTime(Date checkupTime) {
        this.checkupTime = checkupTime;
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

    public Short getBurnInjuryDegree() {
        return burnInjuryDegree;
    }

    public void setBurnInjuryDegree(Short burnInjuryDegree) {
        this.burnInjuryDegree = burnInjuryDegree;
    }

    public Short getBurnInjuryPercentage() {
        return burnInjuryPercentage;
    }

    public void setBurnInjuryPercentage(Short burnInjuryPercentage) {
        this.burnInjuryPercentage = burnInjuryPercentage;
    }

    public Boolean getSubmittedPatient() {
        return submittedPatient;
    }

    public void setSubmittedPatient(Boolean submittedPatient) {
        this.submittedPatient = submittedPatient;
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

    public Integer getNonFatalInjuryId() {
        return nonFatalInjuryId;
    }

    public void setNonFatalInjuryId(Integer nonFatalInjuryId) {
        this.nonFatalInjuryId = nonFatalInjuryId;
    }

//    public Short getResidenceDepartament() {
//        return residenceDepartament;
//    }
//
//    public void setResidenceDepartament(Short residenceDepartament) {
//        this.residenceDepartament = residenceDepartament;
//    }
    @XmlTransient
    public List<Diagnoses> getDiagnosesList() {
        return diagnosesList;
    }

    public void setDiagnosesList(List<Diagnoses> diagnosesList) {
        this.diagnosesList = diagnosesList;
    }

    @XmlTransient
    public List<AnatomicalLocations> getAnatomicalLocationsList() {
        return anatomicalLocationsList;
    }

    public void setAnatomicalLocationsList(List<AnatomicalLocations> anatomicalLocationsList) {
        this.anatomicalLocationsList = anatomicalLocationsList;
    }

    @XmlTransient
    public List<KindsOfInjury> getKindsOfInjuryList() {
        return kindsOfInjuryList;
    }

    public void setKindsOfInjuryList(List<KindsOfInjury> kindsOfInjuryList) {
        this.kindsOfInjuryList = kindsOfInjuryList;
    }

    public NonFatalSelfInflicted getNonFatalSelfInflicted() {
        return nonFatalSelfInflicted;
    }

    public void setNonFatalSelfInflicted(NonFatalSelfInflicted nonFatalSelfInflicted) {
        this.nonFatalSelfInflicted = nonFatalSelfInflicted;
    }

    public NonFatalDomesticViolence getNonFatalDomesticViolence() {
        return nonFatalDomesticViolence;
    }

    public void setNonFatalDomesticViolence(NonFatalDomesticViolence nonFatalDomesticViolence) {
        this.nonFatalDomesticViolence = nonFatalDomesticViolence;
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

    public Activities getActivityId() {
        return activityId;
    }

    public void setActivityId(Activities activityId) {
        this.activityId = activityId;
    }

    public DestinationsOfPatient getDestinationPatientId() {
        return destinationPatientId;
    }

    public void setDestinationPatientId(DestinationsOfPatient destinationPatientId) {
        this.destinationPatientId = destinationPatientId;
    }

    public HealthProfessionals getHealthProfessionalId() {
        return healthProfessionalId;
    }

    public void setHealthProfessionalId(HealthProfessionals healthProfessionalId) {
        this.healthProfessionalId = healthProfessionalId;
    }

    public Injuries getInjuryId() {
        return injuryId;
    }

    public void setInjuryId(Injuries injuryId) {
        this.injuryId = injuryId;
    }

    public Intentionalities getIntentionalityId() {
        return intentionalityId;
    }

    public void setIntentionalityId(Intentionalities intentionalityId) {
        this.intentionalityId = intentionalityId;
    }

    public Mechanisms getMechanismId() {
        return mechanismId;
    }

    public void setMechanismId(Mechanisms mechanismId) {
        this.mechanismId = mechanismId;
    }

    public Neighborhoods getInjuryNeighborhoodId() {
        return injuryNeighborhoodId;
    }

    public void setInjuryNeighborhoodId(Neighborhoods injuryNeighborhoodId) {
        this.injuryNeighborhoodId = injuryNeighborhoodId;
    }

    public NonFatalDataSources getNonFatalDataSourceId() {
        return nonFatalDataSourceId;
    }

    public void setNonFatalDataSourceId(NonFatalDataSources nonFatalDataSourceId) {
        this.nonFatalDataSourceId = nonFatalDataSourceId;
    }

    public NonFatalDataSources getSubmittedDataSourceId() {
        return submittedDataSourceId;
    }

    public void setSubmittedDataSourceId(NonFatalDataSources submittedDataSourceId) {
        this.submittedDataSourceId = submittedDataSourceId;
    }

    public NonFatalPlaces getInjuryPlaceId() {
        return injuryPlaceId;
    }

    public void setInjuryPlaceId(NonFatalPlaces injuryPlaceId) {
        this.injuryPlaceId = injuryPlaceId;
    }

    public UseAlcoholDrugs getUseDrugsId() {
        return useDrugsId;
    }

    public void setUseDrugsId(UseAlcoholDrugs useDrugsId) {
        this.useDrugsId = useDrugsId;
    }

    public UseAlcoholDrugs getUseAlcoholId() {
        return useAlcoholId;
    }

    public void setUseAlcoholId(UseAlcoholDrugs useAlcoholId) {
        this.useAlcoholId = useAlcoholId;
    }

    public NonFatalInterpersonal getNonFatalInterpersonal() {
        return nonFatalInterpersonal;
    }

    public void setNonFatalInterpersonal(NonFatalInterpersonal nonFatalInterpersonal) {
        this.nonFatalInterpersonal = nonFatalInterpersonal;
    }

    public NonFatalTransport getNonFatalTransport() {
        return nonFatalTransport;
    }

    public void setNonFatalTransport(NonFatalTransport nonFatalTransport) {
        this.nonFatalTransport = nonFatalTransport;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nonFatalInjuryId != null ? nonFatalInjuryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NonFatalInjuries)) {
            return false;
        }
        NonFatalInjuries other = (NonFatalInjuries) object;
        if ((this.nonFatalInjuryId == null && other.nonFatalInjuryId != null) || (this.nonFatalInjuryId != null && !this.nonFatalInjuryId.equals(other.nonFatalInjuryId))) {
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
        return "model.pojo.NonFatalInjuries[ nonFatalInjuryId=" + nonFatalInjuryId + " ]";
    }
}
