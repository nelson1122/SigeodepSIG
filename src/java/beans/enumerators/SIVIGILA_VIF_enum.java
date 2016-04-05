/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 * This class handles enumerators related with SIVIGILA_VIF
 *
 * @author SANTOS
 */
public enum SIVIGILA_VIF_enum {

    accion_salud_anticonceptivo,
    accion_salud_atencion,
    accion_salud_autoridad,
    accion_salud_mental,
    accion_salud_orientacion,
    accion_salud_otro,
    accion_salud_profilaxis,
    antecedentes_hecho_similar,
    area_ocurrencia_de_los_hechos,
    armas_utilizadas,
    aseguradora,
    barrio_del_evento,
    convive_con_agresor,
    //cual_otra_accion, NO SE PROCESA
    cual_otro_mecanismo,
    direccion_evento,
    direccion_residencia,
    edad_de_la_victima,
    edad_del_agresor,
    escenario_hechos,
    escolaridad_agresor,
    escolaridad_victima,
    factor_vulnerabilidad_victima,
    fecha_consulta,
    fecha_evento,
    fecha_nacimiento,
    genero_agresor,
    genero_victima,
    grupo_agresor,
    grupo_vulnerable,
    hora_evento,
    institucion_de_salud,
    medida_edad,
    //municipio_de_procedencia, NO SE PROCESA
    municipio_de_residencia,
    naturaleza_violencia,
    nombre_profesional_salud,
    numero_de_identificacion,
    ocupacion_agresor,
    ocupacion_victima,
    otra_arma,
    otra_relacion_familiar,
    otra_relacion_no_familiar,
    otro_factor_vulnerabilidad,
    otro_grupo_agresor,
    otro_mecanismo,
    pertenencia_etnica,
    presencia_alcohol_agresor,
    presencia_alcohol_victima,
    primer_apellido,
    primer_nombre,
    recomienda_proteccion,
    relacion_familiar_victima,
    relacion_no_familiar_victima,
    segundo_apellido,
    segundo_nombre,
    sustancia_intoxicacion,
    telefono_paciente,
    tipo_de_identificacion,
    tipo_de_regimen_en_salud,
    trabajo_de_campo,
    zona_conflicto,
    NOVALUE;

    /**
     * converts a string to an enumerator, if not possible then returns NOVALUE
     *
     * @param str
     * @return
     */
    public static SIVIGILA_VIF_enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
