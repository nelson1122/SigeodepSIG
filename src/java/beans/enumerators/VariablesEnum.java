/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 * This class handles enumerators related with variables
 *
 * @author SANTOS
 */
public enum VariablesEnum {

    day,//dia,
    age,//edad
    neighborhoods,//barrio,
    communes,//comuna,
    corridors,//corredor,
    areas,//zona,
    municipality,//municipio
    genders,//genero,
    days,//dia_semana,
    year,//año
    month,//mes
    hour,//hora
    daily,//diario
    monthly,//mensual
    annual,//anual
    temporalDisaggregation,//Desagrega en un periodo de tiempo
    quadrants,//cuadrante
    injuries_fatal,
    injuries_non_fatal,
    activities,
    boolean3,
    victim_characteristics,
    accident_classes,
    alcohol_levels,
    use_alcohol_drugs,
    alcohol_levels_counterparts,
    alcohol_levels_victim,
    murder_contexts,
    contexts,
    destinations_of_patient,
    related_events,
    precipitating_factors,
    aggressor_genders,
    places,
    non_fatal_places,
    mechanisms,
    accident_mechanisms,
    suicide_mechanisms,
    protective_measures,
    relationships_to_victim,
    weapon_types,
    counterpart_service_type,
    service_types,
    transport_counterparts,
    transport_types,
    transport_users,
    involved_vehicles,
    road_types,
    abuse_types,
    aggressor_types,
    degree,
    ethnic_groups,
    insurance,
    jobs,
    public_health_actions,
    sivigila_educational_level,
    sivigila_group,
    sivigila_mechanism,
    sivigila_no_relative,
    sivigila_tip_ss,
    sivigila_vulnerability,
    vulnerable_groups,
    kinds_of_injury,
    anatomical_locations,
    actions_to_take,
    security_elements,
    source_vif,
    non_fatal_data_sources,
    NOVALUE;

    /**
     * converts a string to an enumerator, if not possible then returns NOVALUE
     *
     * @param str
     * @return
     */
    public static VariablesEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
