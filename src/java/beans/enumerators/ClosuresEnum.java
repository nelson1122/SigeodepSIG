/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 * This class handles enumerators related with closures
 *
 * @author SANTOS
 */
public enum ClosuresEnum {

    //----------------------------------------------------------
    //FATALES Y NO FATALES--------------------------------------
    //----------------------------------------------------------
    id_lesion,
    edad_victima,
    mayor_edad,
    genero,
    ocupacion,
    barrio_residencia,
    fecha_evento,
    hora_evento,
    barrio_evento,
    cuadrante,
    estado,
    //----------------------------------------------------------
    //--FATALES ------------------------------------------------
    clase_lugar_hecho,
    nivel_alcohol,
    numero_victimas_fatales_mismo_hecho,
    //----homicidios -------------------------------------------
    tipo_arma,
    contexto_homicidio,
    //----accidentes -------------------------------------------
    numero_victimas_no_fatales_mismo_hecho,
    mecanismo_muerte,
    //----suicidios --------------------------------------------
    mecanismo_suicidio,
    intento_previo_suicidio,
    antecedentes_salud_mental,
    eventos_relacionados_con_hecho,
    //----transito ---------------------------------------------
    tipo_via_hecho,
    clase_accidente,
    caracteristicas_victima,
    medidas_proteccion,
    nivel_alcohol_contraparte,
    vehiculo_involucrado_victima,
    vehiculo_involucrado_contraparte,
    tipo_servicio_vehiculo_victima,
    tipo_servicio_vehiculo_contraparte,
    //----------------------------------------------------------
    //-- NO FATALES --------------------------------------------
    grado_quemadura,
    porcentaje_quemadura,
    grupo_etnico,
    aseguradora,
    grupo_vulnerable,
    fecha_consulta,
    hora_consulta,
    fuente_no_fatal,
    actividad,
    mecanismo,
    lugar_del_hecho,
    uso_de_alcohol,
    uso_de_drogas,
    destino_paciente,
    diagnostico_1,
    diagnostico_2,
    sitio_anatomico,
    naturaleza_lesion,
    //----transporte---------------------------------------------------------
    tipo_transporte_lesionado,
    tipo_transporte_contraparte,
    tipo_de_usuario,
    elementos_de_seguridad,
    //---- intrafamiliar ----------------------------------------------------
    tipo_maltrato,
    tipo_agresor,
    accion_a_realizar,
    //----interpersonal -----------------------------------------------------   
    antecedentes_previos,
    relacion_agresor_victima,
    contexto,
    genero_agresor,
    //----autoinflingida-----------------------------------------------------
    intento_previo,
    antecedentes_mentales,
    factores_precipitantes,
    //----sivigila-----------------------------------------------------
    institucion_receptora,//
    mayor_edad_victima,
    genero_victima,
    grupo_agresor,
    edad_agresor,
    mayor_edad_agresor,
    ocupacion_victima,
    ocupacion_agresor,
    pertenencia_etnica,
    grupo_poblacional,
    escolaridad_victima,
    factor_vulnerabilidad,
    antecedentes_hecho_similar,
    presencia_alcohol_victima,
    presencia_alcohol_agresor,
    tipo_regimen,
    zona_conflicto,
    escenario,
    escolaridad_agresor,
    relacion_familiar_victima,
    relacion_no_familiar,
    convive_con_agresor,
    armas_utilizadas,
    naturaleza_violencia,
    atencion_salud,
    recomienda_proteccion,
    trabajo_de_campo,
    //-------enumeraciones para tablas de cierres--------
    fatal_injury_murder_sta,
    fatal_injury_traffic_sta,
    fatal_injury_suicide_sta,
    fatal_injury_accident_sta,
    non_fatal_interpersonal_sta,
    non_fatal_transport_sta,
    non_fatal_self_inflicted_sta,
    non_fatal_domestic_violence_sta,
    non_fatal_non_intentional_sta,
    sivigila_sta,
    //-------enumeraciones para tipo de imputacion--------
    model_imputation,
    mode_imputation,
    none_imputation,
    NOVALUE;

    /**
     * converts a string to an enumerator, if not possible then returns NOVALUE
     *
     * @param str
     * @return
     */
    public static ClosuresEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
