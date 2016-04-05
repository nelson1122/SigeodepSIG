/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "populationsMB")
@SessionScoped
public class PopulationsMB implements Serializable {

    private UploadedFile file;
    private String typePopulations = "Zona";
    private ConnectionJdbcMB connectionJdbcMB;
    private int tuplesProcessed;
    private String errors = "";
    private boolean continueProcess = true;
    private int recordsInserts = 0;
    private int recordsUpdate = 0;
    private int recordsIgnored = 0;
    private boolean renderedResult = false;
    private String strResult = "";

    public PopulationsMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void changeTypePopulation() {
        continueProcess = true;
    }

    private void copyFile(String fileName, InputStream in) {
        try {
            try (OutputStream out = new FileOutputStream(new File(fileName))) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                in.close();
                out.flush();
            }
            //System.out.println("El nuevo fichero fue creado con éxito!");
        } catch (IOException e) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            file = event.getFile();
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
            recordsInserts = 0;
            recordsUpdate = 0;
            recordsIgnored = 0;
            renderedResult = false;
            File file2 = new File(file.getFileName());
            errors = "";
            try {
                //---------------------------------------------------------------------------------------------------------
                //---------------------------------------------------------------------------------------------------------
                //INICIA LA VALIDACION DEL ARCHIVO----------------------------------------------------------------------
                //---------------------------------------------------------------------------------------------------------
                //---------------------------------------------------------------------------------------------------------
                OPCPackage container;
                container = OPCPackage.open(file2.getAbsolutePath());
                ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(container);
                XSSFReader xssfReader = new XSSFReader(container);
                StylesTable styles = xssfReader.getStylesTable();
                XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
                continueProcess = true;
                tuplesProcessed = 0;
                while (iter.hasNext() && continueProcess) {
                    InputStream stream = iter.next();
                    InputSource sheetSource = new InputSource(stream);
                    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = saxFactory.newSAXParser();
                        XMLReader sheetParser = saxParser.getXMLReader();
                        ContentHandler handler;//means result instead of formula                                
                        handler = new XSSFSheetXMLHandler(styles, strings, new XSSFSheetXMLHandler.SheetContentsHandler() {
                            ArrayList<String> rowFileData;

                            @Override
                            public void startRow(int rowNum) {
                                rowFileData = new ArrayList<>();
                            }

                            @Override
                            public void endRow() {
                                if (tuplesProcessed == 0) {//Se lee cabecera
                                    if (typePopulations.compareTo("Zona") == 0) {
                                        validateHeaderArea(rowFileData);
                                    } else {
                                        validateHeaderCommune(rowFileData);
                                    }
                                } else {//Se lee registro                                    
                                    if (typePopulations.compareTo("Zona") == 0) {//se valida Cabecera por zonas                                        
                                        validateRecordArea(rowFileData);
                                    } else {
                                        validateRecordCommune(rowFileData);
                                    }
                                }
                                tuplesProcessed++;
                            }

                            @Override
                            public void cell(String cellReference, String formattedValue) {
                                rowFileData.add(formattedValue);
                            }

                            @Override
                            public void headerFooter(String text, boolean isHeader, String tagName) {
                            }
                        }, false);
                        sheetParser.setContentHandler(handler);
                        sheetParser.parse(sheetSource);
                    } catch (ParserConfigurationException e) {
                        System.out.println("Error 12 en " + this.getClass().getName() + ":" + e.toString());
                    }
                    stream.close();
                    break;
                }
            } catch (InvalidFormatException e) {
                System.out.println("Error 13 en " + this.getClass().getName() + ":" + e.toString());
            } catch (SAXException e) {
                System.out.println("Error 14 en " + this.getClass().getName() + ":" + e.toString());
            } catch (OpenXML4JException e) {
                System.out.println("Error 15 en " + this.getClass().getName() + ":" + e.toString());
            }




            if (continueProcess) {//Si se continua con el proceso se debe ingresar los datos
                //---------------------------------------------------------------------------------------------------------
                //---------------------------------------------------------------------------------------------------------
                //INICIA LA REGISTRO DE LOS DATOS DEL ARCHIVO----------------------------------------------------------------------
                //---------------------------------------------------------------------------------------------------------
                //---------------------------------------------------------------------------------------------------------
                try {//procesar el archivo
                    OPCPackage container;
                    container = OPCPackage.open(file2.getAbsolutePath());
                    ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(container);
                    XSSFReader xssfReader = new XSSFReader(container);
                    StylesTable styles = xssfReader.getStylesTable();
                    XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
                    continueProcess = true;
                    tuplesProcessed = 0;


                    while (iter.hasNext() && continueProcess) {
                        InputStream stream = iter.next();
                        InputSource sheetSource = new InputSource(stream);
                        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
                        try {
                            SAXParser saxParser = saxFactory.newSAXParser();
                            XMLReader sheetParser = saxParser.getXMLReader();
                            ContentHandler handler;//means result instead of formula                                
                            handler = new XSSFSheetXMLHandler(styles, strings, new XSSFSheetXMLHandler.SheetContentsHandler() {
                                ArrayList<String> rowFileData;

                                @Override
                                public void startRow(int rowNum) {
                                    rowFileData = new ArrayList<>();
                                }

                                @Override
                                public void endRow() {
                                    if (tuplesProcessed != 0) {//Se lee registro                                                           
                                        if (typePopulations.compareTo("Zona") == 0) {//se valida Cabecera por zonas                                        
                                            insertPopulation("populations_by_area", rowFileData);
                                        } else {
                                            insertPopulation("populations_by_commune", rowFileData);
                                        }
                                    }
                                    tuplesProcessed++;
                                }

                                @Override
                                public void cell(String cellReference, String formattedValue) {
                                    rowFileData.add(formattedValue);
                                }

                                @Override
                                public void headerFooter(String text, boolean isHeader, String tagName) {
                                }
                            }, false);
                            sheetParser.setContentHandler(handler);
                            sheetParser.parse(sheetSource);
                        } catch (ParserConfigurationException e) {
                            System.out.println("Error 12 en " + this.getClass().getName() + ":" + e.toString());
                        }
                        stream.close();
                        break;
                    }
                } catch (InvalidFormatException e) {
                    System.out.println("Error 13 en " + this.getClass().getName() + ":" + e.toString());
                } catch (SAXException e) {
                    System.out.println("Error 14 en " + this.getClass().getName() + ":" + e.toString());
                } catch (OpenXML4JException e) {
                    System.out.println("Error 15 en " + this.getClass().getName() + ":" + e.toString());
                }
            } else {//si no se continua el proceso se debe informar al usuario
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error:", "No se pudo realizar la importación: " + errors));
            }
            if (recordsIgnored != 0 || recordsUpdate != 0 || recordsInserts != 0) {
                renderedResult = true;
                strResult = "El resultado de la importacion de registrios es: \n"
                        + recordsIgnored + " registros Ignorados \n"
                        + recordsUpdate + " registros Actualizados \n"
                        + recordsInserts + " registros Ingresados \n";
            } else {
                renderedResult = false;
            }
            RequestContext.getCurrentInstance().update("IdForm1");

        } catch (Exception ex) {
            System.out.println("Error 20 en " + this.getClass().getName() + ":" + ex.toString());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error:", "error al realizar la carga del archivo" + ex.toString()));
        }
    }

    public void insertPopulation(String table, ArrayList<String> rowFileData) {
        boolean recordFound = false;
        ResultSet rs;
        //se revisa si ya existe 
        try {
            if (table.compareTo("populations_by_commune") == 0) {
                rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    * "
                        + " FROM "
                        + "   populations_by_commune "
                        + " WHERE "
                        + "   year_population = " + rowFileData.get(0) + " AND "
                        + "   age             = " + rowFileData.get(1) + " AND "
                        + "   gender_id       = " + rowFileData.get(2) + " AND "
                        + "   commune_id      = " + rowFileData.get(3));
                if (rs.next()) {
                    recordFound = true;
                }
            } else {
                rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    * "
                        + " FROM "
                        + "   populations_by_area "
                        + " WHERE "
                        + "   year_population = " + rowFileData.get(0) + " AND "
                        + "   age             = " + rowFileData.get(1) + " AND "
                        + "   gender_id       = " + rowFileData.get(2) + " AND "
                        + "   area_id      = " + rowFileData.get(3));
                if (rs.next()) {
                    recordFound = true;
                }
            }

            if (recordFound) {//SE DEBE ACTUALIZAR
                if (table.compareTo("populations_by_commune") == 0) {
                    connectionJdbcMB.non_query(""
                            + " UPDATE "
                            + "    populations_by_commune "
                            + " SET "
                            + "   population = " + rowFileData.get(4)
                            + " WHERE "
                            + "   year_population = " + rowFileData.get(0) + " AND "
                            + "   age             = " + rowFileData.get(1) + " AND "
                            + "   gender_id       = " + rowFileData.get(2) + " AND "
                            + "   commune_id      = " + rowFileData.get(3));
                    recordsUpdate++;
                } else {
                    connectionJdbcMB.non_query(""
                            + " UPDATE "
                            + "    populations_by_area "
                            + " SET "
                            + "   population = " + rowFileData.get(4)
                            + " WHERE "
                            + "   year_population = " + rowFileData.get(0) + " AND "
                            + "   age             = " + rowFileData.get(1) + " AND "
                            + "   gender_id       = " + rowFileData.get(2) + " AND "
                            + "   area_id      = " + rowFileData.get(3));
                    recordsUpdate++;
                }
            } else {//SE DEBE ISERTAR
                connectionJdbcMB.non_query(""
                        + "INSERT INTO " + table + " VALUES ( "
                        + rowFileData.get(0) + ","
                        + rowFileData.get(1) + ","
                        + rowFileData.get(2) + ","
                        + rowFileData.get(3) + ","
                        + rowFileData.get(4) + ""
                        + " )");
                recordsInserts++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PopulationsMB.class.getName()).log(Level.SEVERE, null, ex);
            recordsIgnored++;
        }
    }

    public void validateHeaderArea(ArrayList<String> rowFileData) {
        //año, edad, genero, zona, poblacion
        if (rowFileData.size() == 5) {
            if (rowFileData.get(0).compareToIgnoreCase("año") == 0
                    && rowFileData.get(1).compareToIgnoreCase("edad") == 0
                    && rowFileData.get(2).compareToIgnoreCase("genero") == 0
                    && rowFileData.get(3).compareToIgnoreCase("zona") == 0
                    && rowFileData.get(4).compareToIgnoreCase("poblacion") == 0) {
                continueProcess = true;
                return;
            }
        }
        errors = "La cabecera debe contener 5 columnas de nombres: año, edad, genero, zona y  poblacion  ";
        continueProcess = false;

    }

    public void validateHeaderCommune(ArrayList<String> rowFileData) {
        //año, edad, genero, comuna, poblacion
        if (rowFileData.size() == 5) {
            if (rowFileData.get(0).compareToIgnoreCase("año") == 0
                    && rowFileData.get(1).compareToIgnoreCase("edad") == 0
                    && rowFileData.get(2).compareToIgnoreCase("genero") == 0
                    && rowFileData.get(3).compareToIgnoreCase("comuna") == 0
                    && rowFileData.get(4).compareToIgnoreCase("poblacion") == 0) {
                continueProcess = true;
                return;
            }
        }
        errors = "La cabecera debe contener 5 columnas de nombres: año, edad, genero, comuna y  poblacion  ";
        continueProcess = false;
    }

    private boolean validateInt(String value, int min, int max) {
        try {
            int v = Integer.parseInt(value);
            if (v <= max && v >= min) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void validateRecordArea(ArrayList<String> rowFileData) {
        //año, edad, genero, area, poblacion
        if (rowFileData.size() != 5) {
            errors = "Todos los registros deben contener 5 valores correspondientes a año, edad, genero, area y poblacion";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(0), 2000, 2100)) {
            errors = "El año debe ser un valor entero de 2000 a 2100";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(1), 0, 200)) {
            errors = "La edad debe ser un valor entero de 0 a 200";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(2), 1, 2)) {
            errors = "La genero debe ser un valor entero: 1=masculino 2=femenino";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(3), 1, 2)) {
            errors = "La zona debe ser un valor entero: 1=Urbana 2=Rural";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(4), 0, 1000000)) {
            errors = "La población debe ser un valor entero de 0 a 1'000.000";
            continueProcess = false;
            return;
        }
    }

    public void validateRecordCommune(ArrayList<String> rowFileData) {
        //año, edad, genero, comuna, poblacion
        if (rowFileData.size() != 5) {
            errors = "Todos los registros deben contener 5 valores correspondientes a año, edad, genero, comuna y poblacion";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(0), 2000, 2100)) {
            errors = "El año debe ser un valor entero de 2000 a 2100";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(1), 0, 200)) {
            errors = "La edad debe ser un valor entero de 0 a 200";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(2), 1, 2)) {
            errors = "La genero debe ser un valor entero: 1=masculino 2=femenino";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(3), 1, 100)) {
            errors = "La comuna debe ser un valor entero de a 100";
            continueProcess = false;
            return;
        }
        if (!validateInt(rowFileData.get(4), 0, 1000000)) {
            errors = "La población debe ser un valor entero de 0 a 1'000.000";
            continueProcess = false;
            return;
        }
    }

    private void createCell(HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    public void postProcessXLS(Object document) {
        try {
            HSSFWorkbook book = (HSSFWorkbook) document;
            HSSFRow row;
            HSSFCellStyle cellStyle = book.createCellStyle();

            HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro


            int rowPosition = 0;
            row = sheet.createRow(rowPosition++);
            createCell(row, 0, "año");
            createCell(row, 1, "edad");
            createCell(row, 2, "genero");
            createCell(row, 4, "poblacion");

            ResultSet rs;
            if (typePopulations.compareTo("Zona") == 0) {
                createCell(row, 3, "zona");
                rs = connectionJdbcMB.consult("SELECT * FROM populations_by_area");
            } else {
                createCell(row, 3, "comuna");
                rs = connectionJdbcMB.consult("SELECT * FROM populations_by_commune");
            }
            while (rs.next()) {
                row = sheet.createRow(rowPosition++);
                createCell(row, 0, rs.getString(1));
                createCell(row, 1, rs.getString(2));
                createCell(row, 2, rs.getString(3));
                createCell(row, 3, rs.getString(4));
                createCell(row, 4, rs.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PopulationsMB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void reset() {
    }

    public String getTypePopulations() {
        return typePopulations;
    }

    public void setTypePopulations(String typePopulations) {
        this.typePopulations = typePopulations;
    }

    public boolean isRenderedResult() {
        return renderedResult;
    }

    public void setRenderedResult(boolean renderedResult) {
        this.renderedResult = renderedResult;
    }

    public String getStrResult() {
        return strResult;
    }

    public void setStrResult(String strResult) {
        this.strResult = strResult;
    }
}