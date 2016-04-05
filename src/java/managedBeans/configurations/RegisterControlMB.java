/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.*;

/**
 * The RegisterControlMB class is responsible for keeping track of records by a
 * date range to which a report in xls format,Which yields control is done by
 * the user who entered the system.
 *
 * @author SANTOS
 */
@ManagedBean(name = "registerControlMB")
@SessionScoped
public class RegisterControlMB implements Serializable {

    /**
     * Esta clase permite llevar el control de la cantidad de registros que los
     * usuarios hain ingresado al sistema
     */
    private Date initialDate = new Date();
    private Date endDate = new Date();
    private String exportFileName = "";
    private Boolean btnExportDisabled = false;
    private ConnectionJdbcMB connectionJdbcMB;
    SimpleDateFormat format_es = new SimpleDateFormat("dd MMM yyyy", new Locale("ES"));
    SimpleDateFormat format_sql = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat format_postgres = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * It is responsible to connect to the database and perform validations if
     * month, day, valid year.
     */
    public RegisterControlMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);

        initialDate.setDate(1);
        initialDate.setMonth(0);
        initialDate.setYear(2012 - 1900);
        endDate.setDate(1);
        endDate.setMonth(5);
        endDate.setYear(2012 - 1900);

        exportFileName = format_es.format(initialDate) + " - " + format_es.format(endDate);

    }

    /**
     * permit to change the date.
     */
    public void changeDate() {
        exportFileName = format_es.format(initialDate) + " - " + format_es.format(endDate);
    }

    /**
     * It is responsible to create a cell within the row.
     *
     * @param cellStyle: Style that will have the cell.
     * @param fila: row where create the cell
     * @param position: Determines the position where anger cell within the row.
     * @param value: Sets the value that will be created within the cell.
     */
    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                                
        try {
            double valueDouble = Double.parseDouble(value.replace(",", "."));
            cell.setCellValue(valueDouble);
        } catch (Exception e) {
            cell.setCellValue(new HSSFRichTextString(value));
        }
        cell.setCellStyle(cellStyle);
    }

    /**
     * It is responsible to create a cell within the row.
     *
     * @param fila: row where create the cell
     * @param position: Determines the position where anger cell within the row.
     * @param value: Sets the value that will be created within the cell.
     */
    private void createCell(HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        try {
            double valueDouble = Double.parseDouble(value.replace(",", "."));
            cell.setCellValue(valueDouble);
        } catch (Exception e) {
            cell.setCellValue(new HSSFRichTextString(value));
        }
    }

    /**
     * runs a xls file where the user inserts a row within a worksheet where two
     * fields are set: CODE, NAME.
     *
     * @param document: Document to modify the name and code field.
     */
    public void postProcessXLS(Object document) {

        boolean continueProcess = true;
        //validacion de fechas
        if (continueProcess) {//VALIDACION DE FECHAS            
            long fechaInicialMs = initialDate.getTime();
            long fechaFinalMs = endDate.getTime();
            long diferencia = fechaFinalMs - fechaInicialMs;
            exportFileName = format_es.format(initialDate) + " - " + format_es.format(endDate);


            if (diferencia < 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
                continueProcess = false;
                document = null;
            }
        }

        //genero el xls
        if (continueProcess) {
            HSSFWorkbook book = (HSSFWorkbook) document;
            HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
            HSSFRow rowXls;
            HSSFCellStyle cellStyle = book.createCellStyle();
            HSSFFont font = book.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);
            String sql;
            ResultSet rs;

            try {
                //GENERO LA CONSULTA CON RESULTADOS DE USUARIOS DIA Y CONTEO-----------------
                connectionJdbcMB.non_query("DROP VIEW IF EXISTS register_control");
                sql = ""
                        + "create view register_control as\n"
                        + "SELECT\n"
                        + "	u.user_id, user_name,CAST(input_timestamp AS DATE), count(*)\n"
                        + "FROM\n"
                        + "	non_fatal_injuries\n"
                        + "JOIN\n"
                        + "	users u\n"
                        + "USING\n"
                        + "	(user_id)\n"
                        + "WHERE\n"
                        + "	input_timestamp BETWEEN '" + format_postgres.format(initialDate) + "' AND '" + format_postgres.format(endDate) + "'\n"
                        + "GROUP BY\n"
                        + "	1,2, 3\n"
                        + "order by \n"
                        + "	3";
                connectionJdbcMB.non_query(sql);
                //DETERMINO TOTAL DE USUARIOS(columnas)----------------------------------------
                rs = connectionJdbcMB.consult("SELECT COUNT(DISTINCT(user_name)) from register_control;");
                rs.next();
                int numColumns = rs.getInt(1) + 2;//+2 por fecha y total por fecha
                //DETERMINO TOTAL DE FECHAS(filas)----------------------------------------
                rs = connectionJdbcMB.consult("SELECT COUNT(DISTINCT(input_timestamp)) from register_control order by 1;");
                rs.next();
                int numRows = rs.getInt(1) + 2;//+2 por cabecera y total por usuario
                //GENERO LA MATRIZ----------------------------------------
                String[][] matrixResult = new String[numColumns][numRows];
                //PRIMER FILA----------------------------------------
                int posCol = 0;
                matrixResult[posCol][0] = "FECHA";
                posCol++;
                rs = connectionJdbcMB.consult("SELECT DISTINCT(user_name) from register_control;");
                while (rs.next()) {
                    matrixResult[posCol][0] = rs.getString(1);
                    posCol++;
                }
                matrixResult[posCol][0] = "TOTAL DIARIO";
                //CALCULAR GRAN TOTAL-------------------------------------------
                rs = connectionJdbcMB.consult("SELECT SUM(count) from register_control");
                rs.next();
                int granTotal = rs.getInt(1);

                //PRIMER COLUMNA----------------------------------------
                int posRow = 1;
                rs = connectionJdbcMB.consult("SELECT DISTINCT(input_timestamp) from register_control order by 1");
                while (rs.next()) {
                    matrixResult[0][posRow] = rs.getString(1);
                    posRow++;
                }
                matrixResult[0][posRow] = "TOTAL POR USUARIO";
                //ALMACENO LOS CONTEOS EN LA MATRIZ----------------------------------------
                rs = connectionJdbcMB.consult("SELECT * from register_control");
                while (rs.next()) {
                    for (int i = 0; i < numRows; i++) {
                        if (matrixResult[0][i].compareTo(rs.getString("input_timestamp")) == 0) {
                            posRow = i;
                            break;
                        }
                    }
                    for (int i = 0; i < numColumns; i++) {
                        if (matrixResult[i][0].compareTo(rs.getString("user_name")) == 0) {
                            posCol = i;
                            break;
                        }
                    }
                    matrixResult[posCol][posRow] = rs.getString("count");
                    if (matrixResult[numColumns - 1][posRow] == null) {
                        matrixResult[numColumns - 1][posRow] = rs.getString("count");
                    } else {
                        matrixResult[numColumns - 1][posRow] = String.valueOf(Integer.parseInt(matrixResult[numColumns - 1][posRow]) + rs.getInt("count"));
                    }
                    if (matrixResult[posCol][numRows - 1] == null) {
                        matrixResult[posCol][numRows - 1] = rs.getString("count");
                    } else {
                        matrixResult[posCol][numRows - 1] = String.valueOf(Integer.parseInt(matrixResult[posCol][numRows - 1]) + rs.getInt("count"));
                    }
                    posRow++;
                }

                //GENERO XLS A PARTIR DE matrixResult
                String text;
                HSSFCell celda;
                for (int row = 0; row < numRows; row++) {
                    rowXls = sheet.createRow(row);// crea una fila
                    for (int col = 0; col < numColumns; col++) {
                        text = "0";
                        if (matrixResult[col][row] != null) {
                            text = matrixResult[col][row];
                        }
                        if (row == 0) {
                            createCell(cellStyle, rowXls, col, text);
                        } else {
                            createCell(rowXls, col, text);
                        }
                    }
                    if (row == numRows - 1) {
                        createCell(rowXls, numColumns - 1, String.valueOf(granTotal));
                    }
                }

            } catch (SQLException | NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void reset() {
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    public Boolean getBtnExportDisabled() {
        return btnExportDisabled;
    }

    public void setBtnExportDisabled(Boolean btnExportDisabled) {
        this.btnExportDisabled = btnExportDisabled;
    }
}
