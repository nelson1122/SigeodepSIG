/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 * This class handles enumerators related with SCC_F_030
 *
 * @author SANTOS
 */
public enum SCC_F_030Enum {

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
    area_evento,
    clase_lugar_evento,
    dia_semana_evento,
    numero_victimas_fatales,
    nombres_victima,
    apellidos_victima,
    sexo_victima,
    tipo_edad_victima,
    edad_victima,
    ocuacion_victima,
    municipio_residencia,
    barrio_residencia_victima,
    tipo_identificacion_victima,
    numero_identificacion_victima,
    procedencia_victima,
    arma_o_causa_muerte,
    eventos_relacionados_evento,
    intentos_previos,
    antecedentes_salud_mental,
    narracion_evento,
    narracion_evento_1,
    narracion_evento_2,
    nivel_alcohol_victima,
    nivel_alcohol_sin_dato,
    nivel_alcohol_pendiente,
    nivel_alcohol_desconocido,
    nivel_alcohol_negativo,
    NOVALUE;

    /**
     * converts a string to an enumerator, if not possible then returns NOVALUE
     *
     * @param str
     * @return
     */
    public static SCC_F_030Enum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
