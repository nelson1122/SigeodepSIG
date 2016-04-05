/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 * This class handles enumerators related with SCC_F_029
 *
 * @author SANTOS
 */
public enum SCC_F_029Enum {

    departamento_evento,
    municipio_evento,
    certificado_defuncion,
    dia_evento,
    mes_evento,
    año_evento,
    fecha_evento,
    hora_evento,
    minuto_evento,
    am_pm,
    hora_militar_evento,
    hora_sin_establecer,
    direccion_evento,
    barrio_evento,
    tipo_via,
    area_evento,
    clase_accidente,
    dia_semana_evento,
    numero_victimas_fatales,
    numero_lesionados_evento,
    nombres_victima,
    apellidos_victima,
    sexo_victima,
    tipo_edad_victima,
    edad_victima,
    ocupacion_victima,
    municipio_residencia,
    barrio_residencia_victima,
    tipo_identificacion_victima,
    numero_identificacion_victima,
    procedencia_victima,
    caracteristicas_victima,
    medidas_proteccion,
    vehiculo_involucrado_victima,
    vehiculo_involucrado_contraparte_1,
    vehiculo_involucrado_contraparte_2,
    vehiculo_involucrado_contraparte_3,
    tipo_servicio_vehiculo_victima,
    tipo_servicio_contraparte_1,
    tipo_servicio_contraparte_2,
    tipo_servicio_contraparte_3,
    narracion_evento,
    narracion_evento_1,
    narracion_evento_2,
    nivel_alcohol_victima,
    detalle_nivel_alcohol_victima,
    nivel_alcohol_culpable,
    detalle_nivel_alcohol_culpable,
    NOVALUE;

    /**
     * converts a string to an enumerator, if not possible then returns NOVALUE
     *
     * @param str
     * @return
     */
    public static SCC_F_029Enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
