/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 * This class handles enumerators related with SCC_F_033
 *
 * @author SANTOS
 */
public enum SCC_F_033Enum {

    institucion_receptora,
    primer_apellido,
    segundo_apellido,
    primer_nombre,
    segundo_nombre,
    tipo_identificacion_victima,
    numero_identificacion_victima,
    medida_edad,
    edad_victima,
    sexo_victima,
    ocupacion_victima,
    aseguradora,
    departamento_residencia,
    municipio_residencia,
    barrio_residencia_victima,
    direccion_residencia,
    telefono_residencia,
    departamento_evento,
    municipio_evento,
    barrio_evento,
    direccion_evento,
    dia_evento,
    mes_evento,
    año_evento,
    fecha_evento,
    dia_semana_evento,
    hora_evento,
    minuto_evento,
    am_pm,
    hora_militar_evento,
    dia_consulta,
    mes_consulta,
    año_consulta,
    fecha_consulta,
    dia_semana_consulta,
    hora_consulta,
    minuto_consulta,
    am_pm1,
    hora_militar_consulta,
    es_remitido,
    de_que_ips,
    lugar_ocurrio_evento,
    otro_lugar,
    activida_que_realizaba,
    otra_actividad,
    mecanismo_objeto_lesion,
    caida_a_que_altura,
    que_tipo_polvora,
    cual_desastre_natural,
    otro_tipo_mecanismo_objeto_lesion,
    uso_alcohol,
    uso_drogas,
    grado_mas_grave_quemadura,
    porcentaje_cuerpo_quemado,
    grupos_vulnerables,
    otro_grupo_vulnerable,
    grupos_etnicos,
    otro_grupo_etnico,
    maltrato_fisico,
    tipo_agresor_padre,
    maltrato_psicologico,
    tipo_agresor_madre,
    maltrato_abuso_sexual,
    tipo_agresor_padrastro,
    maltrato_negligencia,
    tipo_agresor_madrastra,
    maltrato_abandono,
    tipo_agresor_conyuge,
    maltrato_institucional,
    tipo_agresor_hermano,
    maltrato_sin_dato,
    tipo_agresor_hijo,
    maltrato_otro,
    tipo_agresor_otro,
    cual_otro_tipo_agresor,
    tipo_agresor_sin_dato,
    cual_otro_tipo_maltrato,
    tipo_agresor_novio,
    accion_realizar_conciliacion,
    accion_realizar_caucion,
    accion_realizar_dictamen,
    accion_realizar_remision_fiscalia,
    accion_realizar_remision_medicina_legal,
    accion_realizar_remision_comisaria,
    accion_realizar_remision_icbf,
    accion_realizar_medidas_proteccion,
    accion_realizar_resimison_salud,
    accion_realizar_atencion_psicologica,
    accion_realizar_restablecimiento_derechos,
    accion_realizar_otra,
    cual_otra_accion_realizar,
    accion_realizar_sin_dato,
    responsable,
    fecha_nacimiento,
    NOVALUE;

    /**
     * converts a string to an enumerator, if not possible then returns NOVALUE
     *
     * @param str
     * @return
     */
    public static SCC_F_033Enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
