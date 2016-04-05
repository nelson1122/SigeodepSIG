/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import model.dao.NeighborhoodsFacade;
import model.dao.QuadrantsFacade;
import model.pojo.Quadrants;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.event.FileUploadEvent;

/**
 * The QuadrantsVariableMB class is responsible for managing everything related
 * to one quadrant, allowing to the user to have the functionality to edit,
 * delete, new and exported in xls list.
 *
 * @author SANTOS
 */
@ManagedBean(name = "quadrantsVariableMB")
@SessionScoped
public class QuadrantsVariableMB implements Serializable {

    @EJB
    QuadrantsFacade quadrantsFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    ConnectionJdbcMB connectionJdbcMB;//clase para la administracion de conexion JDBC
    private List<Quadrants> quadrantsList;//Lista de cuadrantes
    private Quadrants currentQuadrant; //Cuadrante seleccionado
    private List<RowDataTable> rowDataTableList;//Filas de la tabla cuadrantes
    private RowDataTable selectedRowDataTable;  //Filas seleccionada de la tabla cuadrantes  
    private int currentSearchCriteria = 2;//criterio de busqueda en cuadrantes 1. CODIGO 2. NOMBRE
    private String currentSearchValue = "";//valor que se buscara cuando se busca cuadrantes    
    private String quadrantName = "";//Nombre del cuadrante(actualizando existente).
    private String quadrantId = "";//Código del cuadrante(actualizando existente).
    private String quadrantPopuation = "0";//poblacion cuadrante(actualizando existente).
    private String neighborhoodFilter = "";//filtro de barrios disponibles(actualizando existente).
    private String neighborhoodFilter2 = "";//filtro de barrios agregados(actualizando existente).
    private String newQuadrantName = "";//Nombre del cuadrante (creando nuevo cuadrante)
    private String newQuadrantId = "";//Código del cuadrante (creando nuevo cuadrante)
    private String newQuadrantPopuation = "0";//poblacion cuadrante (creando nuevo cuadrante)
    private String newNeighborhoodFilter = "";//filtro de barrios disponibles(creando nuevo cuadrante).
    private String newNeighborhoodFilter2 = "";//filtro de barrios agregados(creando nuevo cuadrante).
    private String poligonText = "";//poligono para el nuevo cuadrante
    private String nameGeomFile = "";//nombre del archivo de geometria para nuevo cuadrante
    private String geomText = "<div style=\"color: red;\"><b>Geometría no cargada</b></div>";//aviso de si la geometria esta o no cargada
    private String realPath = "";//Ruta real en el servidor de la aplicacion
    private boolean disabledShowGeomFile = true;//activar/desactivar boton de ver archivo KML        
    private boolean btnEditDisabled = true;//Valor que determina si se activa o no el botón editar cuadrante
    private boolean btnRemoveDisabled = true;//Valor que determina si se activa o no el botón eliminar cuadrante
    private List<String> availableNeighborhoods = new ArrayList<>();//listado de barrios disponibles para agregar al cuadrante(Modificando un cuadrante existente)
    private List<String> selectedAvailableNeighborhoods = new ArrayList<>();//listado de barrios seleccionados para agregar al cuadrante(Modificando un cuadrante existente)
    private List<String> availableAddNeighborhoods = new ArrayList<>();//listado de barrios agregados al cuadrante(Modificando un cuadrante existente)
    private List<String> selectedAvailableAddNeighborhoods = new ArrayList<>();//listado de barrios seleciconados de los agregados al cuadrante(Modificando un cuadrante existente)
    private List<String> newAvailableNeighborhoods = new ArrayList<>();//listado de barrios disponibles para agregar al cuadrante(Creando un cuadrante nuevo)
    private List<String> newSelectedAvailableNeighborhoods = new ArrayList<>();//listado de barrios seleccionados para agregar al cuadrante(Creando un cuadrante nuevo)
    private List<String> newAvailableAddNeighborhoods = new ArrayList<>();//listado de barrios agregados al cuadrante(Creando un cuadrante nuevo)
    private List<String> newSelectedAvailableAddNeighborhoods = new ArrayList<>();//listado de barrios seleciconados de los agregados al cuadrante(Creando un cuadrante nuevo)

    /**
     * establishes the connection to the database and directory.
     */
    public QuadrantsVariableMB() {
        /*
         *Constructor de la clase, se btiene la clase que administra la conexion JDBC y la ruta real en servidor
         */
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        realPath = (String) servletContext.getRealPath("/");
    }

    /**
     * It is responsible for creating a cell within the row.
     *
     * @param cellStyle: Style that will have the cell.
     * @param fila: row where create the cell
     * @param position: Determines the position where anger cell within the row.
     * @param value: Sets the value that will be created within the cell.
     */
    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        /*
         *Crea una celda dentro de una fila de un archivo excell, aplicando un determinado estilo a la celde
         */
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
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
        /*
         *Crea una celda dentro de una fila de un archivo excell
         */
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    /**
     * runs a xls file where the user inserts a row within a worksheet where two
     * fields are set: CODE, NAME.
     *
     * @param document: Document to modify the name and code field.
     */
    public void postProcessXLS(Object document) {
        /*
         *Creacion de un archivo excell para exportar todos los cuadrantes
         */
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow row;
        HSSFCellStyle cellStyle = book.createCellStyle();
        HSSFFont font = book.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);

        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        
        createCell(cellStyle, row, 0, "CODIGO");
        createCell(cellStyle, row, 1, "NOMBRE");
        quadrantsList = quadrantsFacade.findAll();
        for (int i = 0; i < quadrantsList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, quadrantsList.get(i).getQuadrantId().toString());//CODIGO
            createCell(row, 1, quadrantsList.get(i).getQuadrantName());//NOMBRE            

        }
    }

    /**
     * This method is responsible to upload the new geometry quadrant.
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        /*
         * cargar el archivo de geometria de cuadrante nuevo
         */
        nameGeomFile = "";
        disabledShowGeomFile = true;
        try {
            if (copyFile(event.getFile().getFileName(), event.getFile().getInputstream())) {
                disabledShowGeomFile = false;
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo realizar la carga de este archivo"));
        }
    }

    /**
     * This method is responsible to realize the copy of a file and returns
     * "TRUE" if it was possible to realize this copy.
     *
     * @param fileName
     * @param in
     * @return
     */
    private boolean copyFile(String fileName, InputStream in) {
        /*
         * realiza la copia de un archivo, y retorna si se pudo o realizar esta copia
         */
        disabledShowGeomFile = true;
        nameGeomFile = "";
        try {
            java.io.File folder = new java.io.File(realPath + "web/configurations/maps");
            if (folder.exists()) {//verificar que el directorio exista
                StringBuilder nameAndPathFile = new StringBuilder();
                nameAndPathFile.append(realPath);
                nameAndPathFile.append("web/configurations/maps/");
                nameAndPathFile.append(fileName);
                nameGeomFile = fileName;//ruta que se usa en java script
                disabledShowGeomFile = false;
                java.io.File ficherofile = new java.io.File(nameAndPathFile.toString());
                if (ficherofile.exists()) {//Probamos a ver si existe ese ultimo dato                    
                    ficherofile.delete();//Lo Borramos
                }
                try (OutputStream out = new FileOutputStream(new File(nameAndPathFile.toString()))) {
                    int read;
                    byte[] bytes = new byte[1024];
                    while ((read = in.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    in.close();
                    out.flush();
                    //System.out.println("El fichero de geometria copiado con exito: " + nameAndPathFile.toString());
                    return true;
                } catch (IOException e) {
                    System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
                    return false;
                }
            } else {
                System.out.println("No se encuentra la carpeta");
                return false;
            }
        } catch (Exception e) {
            System.out.println("No se pudo procesar el archivo");
            return false;
        }
    }

    /**
     * This method is responsible to add a neighborhood when you are creating a
     * new quadrant.
     */
    public void addNeighborhoodInNewQuadrantClick() {
        /*
         * adicionar un barrio cuando se esta creando un nuevo cuadrante
         */
        if (newSelectedAvailableNeighborhoods != null && !newSelectedAvailableNeighborhoods.isEmpty()) {
            for (int i = 0; i < newSelectedAvailableNeighborhoods.size(); i++) {
                //lo adiciono a la lista de agregados
                newAvailableAddNeighborhoods.add(newSelectedAvailableNeighborhoods.get(i));
                //lo elimino de disponibles
                for (int j = 0; j < newAvailableNeighborhoods.size(); j++) {
                    if (newAvailableNeighborhoods.get(j).compareTo(newSelectedAvailableNeighborhoods.get(i)) == 0) {
                        newAvailableNeighborhoods.remove(j);
                        break;
                    }
                }
            }
            newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        }
    }

    /**
     * This method is responsible to add a neighborhood a the list of added,
     * when you are editing an existing quadrant.
     */
    public void addNeighborhoodInExistingQuadrantClick() {
        /*
         * adicionar un barrio a la lista de agregados, cuando se esta editando un cuadrante existente
         */
        if (selectedAvailableNeighborhoods != null && !selectedAvailableNeighborhoods.isEmpty()) {
            for (int i = 0; i < selectedAvailableNeighborhoods.size(); i++) {
                //lo adiciono a la lista de agregados
                availableAddNeighborhoods.add(selectedAvailableNeighborhoods.get(i));
                //lo elimino de disponibles
                for (int j = 0; j < availableNeighborhoods.size(); j++) {
                    if (availableNeighborhoods.get(j).compareTo(selectedAvailableNeighborhoods.get(i)) == 0) {
                        availableNeighborhoods.remove(j);
                        break;
                    }
                }
            }
            selectedAvailableAddNeighborhoods = new ArrayList<>();
        }
    }

    /**
     * remove a neighborhood the list of aggregates, when you are creating a new
     * quadrant.
     */
    public void removeNeighborhoodInNewQuadrantClick() {
        /*
         * quitar un barrio de la lista de agregados, cuando se esta creando un nuevo cuadrante
         */
        if (newSelectedAvailableAddNeighborhoods != null && !newSelectedAvailableAddNeighborhoods.isEmpty()) {
            for (int i = 0; i < newSelectedAvailableAddNeighborhoods.size(); i++) {
                //lo adiciono a la lista de disonibles
                newAvailableNeighborhoods.add(newSelectedAvailableAddNeighborhoods.get(i));
                //lo elimino de agregados
                for (int j = 0; j < newAvailableAddNeighborhoods.size(); j++) {
                    if (newAvailableAddNeighborhoods.get(j).compareTo(newSelectedAvailableAddNeighborhoods.get(i)) == 0) {
                        newAvailableAddNeighborhoods.remove(j);
                        break;
                    }
                }
            }
            newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        }
    }

    /**
     * remove a neighborhood the list of aggregates, when you are editing an
     * existing quadrant.
     */
    public void removeNeighborhoodInExistingQuadrantClick() {
        /*
         * quitar un barrio de la lista de agregados, cuando se esta editando un cuadrante existente
         */
        if (selectedAvailableAddNeighborhoods != null && !selectedAvailableAddNeighborhoods.isEmpty()) {
            for (int i = 0; i < selectedAvailableAddNeighborhoods.size(); i++) {
                //lo adiciono a la lista de disonibles
                availableNeighborhoods.add(selectedAvailableAddNeighborhoods.get(i));
                //lo elimino de agregados
                for (int j = 0; j < availableAddNeighborhoods.size(); j++) {
                    if (availableAddNeighborhoods.get(j).compareTo(selectedAvailableAddNeighborhoods.get(i)) == 0) {
                        availableAddNeighborhoods.remove(j);
                        break;
                    }
                }
            }
            selectedAvailableAddNeighborhoods = new ArrayList<>();
        }
    }

    /**
     * load data of a record when select a row of the table that show the
     * existing quadrant
     */
    public void loadRegistry() {
        /*
         * carga de los datos de un registro cuando se selecciona una fila de 
         * la tabla que muestra los cuadrantes existentes
         */
        disabledShowGeomFile = true;
        currentQuadrant = null;
        if (selectedRowDataTable != null) {
            currentQuadrant = quadrantsFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
        if (currentQuadrant != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentQuadrant.getQuadrantName() != null) {
                quadrantName = currentQuadrant.getQuadrantName();
            } else {
                quadrantName = "";
            }
            if (currentQuadrant.getQuadrantId() != null) {
                quadrantId = currentQuadrant.getQuadrantId().toString();// integer NOT NULL, -- Código del cuadrante.
            } else {
                quadrantId = "";
            }
            if (currentQuadrant.getPopulation() != null) {
                quadrantPopuation = String.valueOf(currentQuadrant.getPopulation());
            } else {
                quadrantPopuation = "0";
            }
            //determino si la geometria ya esta cargada
            if (currentQuadrant.getGeom() != null && currentQuadrant.getGeom().trim().length() != 0) {
                geomText = "<div style=\"color: blue;\"><b>Tiene geometría</b></div>";

            } else {
                geomText = "<div style=\"color: red;\"><b>No tiene geometría</b></div>";
            }

            //determino los barrios
            availableNeighborhoods = new ArrayList<>();
            selectedAvailableNeighborhoods = new ArrayList<>();
            availableAddNeighborhoods = new ArrayList<>();
            selectedAvailableAddNeighborhoods = new ArrayList<>();
            neighborhoodFilter = "";
            try {
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT * FROM neighborhoods WHERE neighborhood_id NOT IN "
                        + " (SELECT neighborhood_id FROM neighborhood_quadrant "
                        + " WHERE quadrant_id = " + currentQuadrant.getQuadrantId().toString() + ") ");
                while (rs.next()) {
                    availableNeighborhoods.add(rs.getString("neighborhood_name"));
                }
                rs = connectionJdbcMB.consult(""
                        + " SELECT * FROM neighborhoods WHERE neighborhood_id IN "
                        + " (SELECT neighborhood_id FROM neighborhood_quadrant "
                        + " WHERE quadrant_id = " + currentQuadrant.getQuadrantId().toString() + ") ");
                while (rs.next()) {
                    availableAddNeighborhoods.add(rs.getString("neighborhood_name"));
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * This method is responsible to load the selected geometry.
     */
    public void loadGeometrySelected() {
        if (poligonText != null && poligonText.trim().length() != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "la geometria ha sido cargada"));
            geomText = "<div style=\"color: blue;\"><b>Geometría cargada</b></div>";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha seleccionado ninguna geometria"));
        }
    }

    /**
     * delete a record of the table quadrant, provided and when not in use
     */
    public void deleteRegistry() {
        if (currentQuadrant != null) {
            //connectionJdbcMB.setShowMessages(false);//se desactiva la visualizacion de mensajes a traves de consola
            //1. modifico los eventos para que no tengan este cuadrante
            connectionJdbcMB.non_query(""
                    + " update non_fatal_injuries set quadrant_id = null WHERE quadrant_id = " + currentQuadrant.getQuadrantId() + "; \n"
                    + " update fatal_injuries set quadrant_id = null WHERE quadrant_id = " + currentQuadrant.getQuadrantId() + "; \n");
            //2. elimino este cuadrante
            connectionJdbcMB.non_query(""
                    + " DELETE FROM neighborhood_quadrant WHERE quadrant_id = " + currentQuadrant.getQuadrantId() + "; \n"
                    + " DELETE FROM quadrants WHERE quadrant_id = " + currentQuadrant.getQuadrantId() + "; \n");
            //3.  actualizo el cuadrante de los eventos en base donde cuadrante sea null
            connectionJdbcMB.non_query(""
                    + " UPDATE non_fatal_injuries "
                    + " SET quadrant_id = "
                    + " (SELECT quadrant_id FROM neighborhood_quadrant where neighborhood_quadrant.neighborhood_id = non_fatal_injuries.injury_neighborhood_id limit 1) "
                    + " WHERE quadrant_id is null; "
                    + " UPDATE fatal_injuries "
                    + " SET quadrant_id = "
                    + " (SELECT quadrant_id FROM neighborhood_quadrant where neighborhood_quadrant.neighborhood_id = fatal_injuries.injury_neighborhood_id limit 1) "
                    + " WHERE quadrant_id is null; ");
            //4. cuando el cuadrante sea null colocar cuadrante 0
            connectionJdbcMB.non_query(""
                    + " UPDATE non_fatal_injuries SET quadrant_id = 0 WHERE quadrant_id is null; "
                    + " UPDATE fatal_injuries SET quadrant_id = 0 WHERE quadrant_id is null; ");
            //5. pueden haber barrios sin cuadrante, se debe asociarles sin dato         
            connectionJdbcMB.non_query(""
                    + " INSERT INTO neighborhood_quadrant (neighborhood_id,quadrant_id) "
                    + " SELECT neighborhood_id,'0' FROM neighborhoods WHERE neighborhood_id NOT IN "
                    + " (SELECT distinct neighborhood_id FROM neighborhood_quadrant) ");
            //6. cuando hay barrios con dos cuadrantes y alguno es sin dato se quita el sin dato
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT * from ( "
                    + "SELECT count(neighborhood_id) AS num,neighborhood_id "
                    + "FROM neighborhood_quadrant group by neighborhood_id "
                    + ") AS a WHERE a.num > 1;");
            try {
                while (rs.next()) {
                    connectionJdbcMB.non_query(""
                            + " DELETE  FROM neighborhood_quadrant "
                            + " WHERE neighborhood_id = " + rs.getString("neighborhood_id") + " AND "
                            + " quadrant_id = 0 ");
                }
            } catch (SQLException ex) {
            }
            //7. para los barrios SIN DATO RURAL y SIN DATO URBANO el cuadrante es cero
            connectionJdbcMB.non_query(""
                    + " UPDATE non_fatal_injuries SET quadrant_id = 0 WHERE injury_neighborhood_id = 52001; "
                    + " UPDATE fatal_injuries SET quadrant_id = 0 WHERE injury_neighborhood_id = 52002; ");

            currentQuadrant = null;
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El registro fue eliminado"));

            //connectionJdbcMB.setShowMessages(true);se activa la visualizacion de mensajes a traves de consola
        }
    }

    /**
     * updates a record corresponding to a quadrant.
     */
    public void updateRegistry() {
        /*
         * Actualizar la informacion de un cuadrante existente
         */

        if (currentQuadrant != null) {
            boolean continueProcess = true;
            if (quadrantName.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
                continueProcess = false;
            }
            if (continueProcess) {
                if (quadrantId.trim().length() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un codigo"));
                    continueProcess = false;
                }
            }
            if (continueProcess) {
                quadrantName = quadrantName.toUpperCase();
                try {
                    //buscar si el codigo o cuadrante ya esta registrado
                    ResultSet rs = connectionJdbcMB.consult("SELECT * FROM quadrants "
                            + " WHERE quadrant_name LIKE '" + quadrantName + "' AND "
                            + " quadrant_name NOT LIKE '" + currentQuadrant.getQuadrantName() + "'");
                    if (rs.next()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un cuadrante con un nombre igual"));
                        continueProcess = false;
                    }
                } catch (SQLException ex) {
                }
            }
            if (continueProcess) {
                quadrantName = quadrantName.toUpperCase();
                currentQuadrant.setQuadrantName(quadrantName);
                if (poligonText != null && poligonText.trim().length() != 0) {
                    currentQuadrant.setGeom(poligonText);
                }
                currentQuadrant.setPopulation(Integer.parseInt(quadrantPopuation));
                quadrantsFacade.edit(currentQuadrant);

                String sql = "";
                //elimino los barrios de este cuadrante
                connectionJdbcMB.non_query("DELETE FROM neighborhood_quadrant WHERE quadrant_id = " + currentQuadrant.getQuadrantId());
                //se inserta los diferentes barrios que se haya indicado
                if (availableAddNeighborhoods != null && !availableAddNeighborhoods.isEmpty()) {
                    HashSet<String> hashSet = new HashSet<>(availableAddNeighborhoods);
                    availableAddNeighborhoods.clear();
                    availableAddNeighborhoods.addAll(hashSet);
                    for (int i = 0; i < availableAddNeighborhoods.size(); i++) {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + "SELECT neighborhood_id FROM neighborhoods WHERE neighborhood_name LIKE '" + availableAddNeighborhoods.get(i) + "'");
                        try {
                            while (rs.next()) {
                                sql = sql
                                        + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                                        + rs.getString(1) + ","//id_cuadrante
                                        + quadrantId + "); \n";//corredor
                            }
                        } catch (SQLException e) {
                        }
                    }
                } else {//se agrega sin dato si no se ha seleccionado algun barrio
                    sql = sql
                            + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                            + "52001,"//id_barrio
                            + quadrantId + "); \n";//id_cuadrante
                }
                connectionJdbcMB.non_query(sql);

                //1. modifico los eventos para que no tengan este cuadrante
                connectionJdbcMB.non_query(""
                        + " update non_fatal_injuries set quadrant_id = 0 WHERE quadrant_id = " + currentQuadrant.getQuadrantId() + "; \n"
                        + " update fatal_injuries set quadrant_id = 0 WHERE quadrant_id = " + currentQuadrant.getQuadrantId() + "; \n");
                //2.  actualizo el cuadrante de los eventos en base donde cuadrante sea 0
                connectionJdbcMB.non_query(""
                        + " UPDATE non_fatal_injuries "
                        + " SET quadrant_id = "
                        + " (SELECT quadrant_id FROM neighborhood_quadrant where neighborhood_quadrant.neighborhood_id = non_fatal_injuries.injury_neighborhood_id limit 1) "
                        + " WHERE quadrant_id = 0; "
                        + " UPDATE fatal_injuries "
                        + " SET quadrant_id = "
                        + " (SELECT quadrant_id FROM neighborhood_quadrant where neighborhood_quadrant.neighborhood_id = fatal_injuries.injury_neighborhood_id limit 1) "
                        + " WHERE quadrant_id = 0; ");
                //3. cuando el cuadrante sea null colocar cuadrante 0
                connectionJdbcMB.non_query(""
                        + " UPDATE non_fatal_injuries SET quadrant_id = 0 WHERE quadrant_id is null; "
                        + " UPDATE fatal_injuries SET quadrant_id = 0 WHERE quadrant_id is null; ");
                //4.pueden haber barrios sin cuadrante, se debe asociarles sin dato         
                connectionJdbcMB.non_query(""
                        + " INSERT INTO neighborhood_quadrant (neighborhood_id,quadrant_id) "
                        + " SELECT neighborhood_id,'0' FROM neighborhoods WHERE neighborhood_id NOT IN "
                        + " (SELECT distinct neighborhood_id FROM neighborhood_quadrant) ");
                //5. cuando hay barrios con dos cuadrantes y alguno es sin dato se quita el sin dato
                ResultSet rs = connectionJdbcMB.consult(""
                        + "SELECT * from ( "
                        + "SELECT count(neighborhood_id) AS num,neighborhood_id "
                        + "FROM neighborhood_quadrant group by neighborhood_id "
                        + ") AS a WHERE a.num > 1;");
                try {
                    while (rs.next()) {
                        connectionJdbcMB.non_query(""
                                + " DELETE  FROM neighborhood_quadrant "
                                + " WHERE neighborhood_id = " + rs.getString("neighborhood_id") + " AND "
                                + " quadrant_id = 0 ");
                    }
                } catch (SQLException ex) {
                }
                //5. para los barrios SIN DATO RURAL y SIN DATO URBANO el cuadrante es cero
                connectionJdbcMB.non_query(""
                        + " UPDATE non_fatal_injuries SET quadrant_id = 0 WHERE injury_neighborhood_id = 52001; "
                        + " UPDATE fatal_injuries SET quadrant_id = 0 WHERE injury_neighborhood_id = 52002; ");

                createDynamicTable();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        //reinicio controles
        quadrantName = "";
        currentQuadrant = null;
        selectedRowDataTable = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    /**
     * save a record but in turn verifies that the record name already exists.
     */
    public void saveRegistry() {
        /*
         * Almacenar un nuevo cuadrantre dentro de la base de datos
         */

        boolean continueProcess = true;

        if (newQuadrantName.trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
            continueProcess = false;
        }
        if (continueProcess) {
            newQuadrantName = newQuadrantName.toUpperCase();
            try {
                //buscar si el nombre de cuadrante ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM quadrants WHERE quadrant_name LIKE '" + newQuadrantName + "'");
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un cuadrante con un nombre igual"));
                    continueProcess = false;
                }
            } catch (SQLException ex) {
            }
        }
        if (continueProcess) {
            try {
                //buscar si el codigo o cuadrante ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods WHERE neighborhood_id = " + newQuadrantId);
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un cuadrante con un codigo igual"));
                    continueProcess = false;
                }
            } catch (SQLException ex) {
            }
        }
        if (continueProcess) {
            try {
                String sql = "INSERT INTO quadrants VALUES (";
                String geom = "null";
                if (poligonText != null && poligonText.trim().length() != 0) {
                    geom = "'" + poligonText + "'";
                }
                sql = sql
                        + newQuadrantId + ",'"//codigo
                        + newQuadrantName + "',"//nombre
                        + newQuadrantPopuation + ","//poblacion
                        + geom + "); \n";//geometria
                //se inserta los diferentes cuadrantes que se haya indicado
                if (newAvailableAddNeighborhoods != null && !newAvailableAddNeighborhoods.isEmpty()) {
                    HashSet<String> hashSet = new HashSet<>(newAvailableAddNeighborhoods);
                    newAvailableAddNeighborhoods.clear();
                    newAvailableAddNeighborhoods.addAll(hashSet);
                    for (int i = 0; i < newAvailableAddNeighborhoods.size(); i++) {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + "SELECT neighborhood_id FROM neighborhoods WHERE neighborhood_name LIKE '" + newAvailableAddNeighborhoods.get(i) + "'");
                        while (rs.next()) {
                            sql = sql
                                    + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                                    + rs.getString(1) + ","//id_barrio
                                    + newQuadrantId + "); \n";//id_cuadrante
                        }
                    }
                } else {//se agrega sin dato si no se ha seleccionado algun barrio
                    sql = sql
                            + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                            + "52001,"//id_barrio
                            + newQuadrantId + "); \n";//id_cuadrante
                }
                connectionJdbcMB.non_query(sql);
            } catch (Exception e) {
            }


            //2.  actualizo el cuadrante de los eventos en base a barrio donde cuadrante sea null
            connectionJdbcMB.non_query(""
                    + " UPDATE non_fatal_injuries "
                    + " SET quadrant_id = "
                    + " (SELECT quadrant_id FROM neighborhood_quadrant where neighborhood_quadrant.neighborhood_id = non_fatal_injuries.injury_neighborhood_id limit 1) "
                    + " WHERE quadrant_id = 0; "
                    + " UPDATE fatal_injuries "
                    + " SET quadrant_id = "
                    + " (SELECT quadrant_id FROM neighborhood_quadrant where neighborhood_quadrant.neighborhood_id = fatal_injuries.injury_neighborhood_id limit 1) "
                    + " WHERE quadrant_id = 0; ");
            //4. cuando el cuadrante sea null colocar cuadrante 0
            connectionJdbcMB.non_query(""
                    + " UPDATE non_fatal_injuries SET quadrant_id = 0 WHERE quadrant_id is null; "
                    + " UPDATE fatal_injuries SET quadrant_id = 0 WHERE quadrant_id is null; ");
            //pueden haber barrios sin cuadrante, se debe asociarles sin dato         
            connectionJdbcMB.non_query(""
                    + " INSERT INTO neighborhood_quadrant (neighborhood_id,quadrant_id) "
                    + " SELECT neighborhood_id,'0' FROM neighborhoods WHERE neighborhood_id NOT IN "
                    + " (SELECT distinct neighborhood_id FROM neighborhood_quadrant) ");
            //cuando hay barrios con dos cuadrantes y uno de esos cuadrantes es sin dato se quita
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT * from ( "
                    + "SELECT count(neighborhood_id) AS num,neighborhood_id "
                    + "FROM neighborhood_quadrant group by neighborhood_id "
                    + ") AS a WHERE a.num > 1;");
            try {
                while (rs.next()) {
                    connectionJdbcMB.non_query(""
                            + " DELETE  FROM neighborhood_quadrant "
                            + " WHERE neighborhood_id = " + rs.getString("neighborhood_id") + " AND "
                            + " quadrant_id = 0 ");
                }
            } catch (SQLException ex) {
            }
            //5. para los barrios SIN DATO RURAL y SIN DATO URBANO el cuadrante es cero
            connectionJdbcMB.non_query(""
                    + " UPDATE non_fatal_injuries SET quadrant_id = 0 WHERE injury_neighborhood_id = 52001; "
                    + " UPDATE fatal_injuries SET quadrant_id = 0 WHERE injury_neighborhood_id = 52002; ");

            currentQuadrant = null;
            selectedRowDataTable = null;
            createDynamicTable();
            newRegistry();//limpiar formulario
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Nuevo cuadrante almacenado.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    /**
     * change the value to a new population, this value numeric must be greater
     * or equal to zero
     */
    public void changeNewPopulation() {
        /*
         * Funcion llamada cuando se modifica el campo de poblacion al crear un nuevo cuadrante
         */
        try {
            int c = Integer.parseInt(newQuadrantPopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                newQuadrantPopuation = "0";
            }
        } catch (Exception e) {
            if (newQuadrantPopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            newQuadrantPopuation = "0";
        }
    }

    /**
     * change the value to population, this value numeric must be greater or
     * equal to zero.
     */
    public void changePopulation() {
        /*
         * Funcion llamada cuando se modifica el campo de poblacion al modificar cuadrante existente
         */
        try {
            int c = Integer.parseInt(quadrantPopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                quadrantPopuation = "0";
            }
        } catch (Exception e) {
            if (quadrantPopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            quadrantPopuation = "0";
        }
    }

    /**
     * This method is responsible to realize a filtering of available quadrant,
     * by the search for an input string.
     */
    public void changeNewNeighborhoodFilter() {
        /*
         * funcion llamada cuando se modifica el campo que filtra los barrios que se agregaran a un nuevo cuadrante
         */
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        boolean foundQuadrant;
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods ORDER BY neighborhood_name ");
            while (rs.next()) {
                if (newNeighborhoodFilter != null && newNeighborhoodFilter.trim().length() != 0) {//hay cadena a buscar
                    if (rs.getString("neighborhood_name").toUpperCase().indexOf(newNeighborhoodFilter.toUpperCase()) != -1) {
                        foundQuadrant = false;
                        for (int i = 0; i < newAvailableAddNeighborhoods.size(); i++) {
                            if (rs.getString("neighborhood_name").compareTo(newAvailableAddNeighborhoods.get(i)) == 0) {
                                foundQuadrant = true;
                            }
                        }
                        if (!foundQuadrant) {
                            newAvailableNeighborhoods.add(rs.getString("neighborhood_name"));
                        }
                    }
                } else {//no hay cadena a buscar
                    foundQuadrant = false;
                    for (int i = 0; i < newAvailableAddNeighborhoods.size(); i++) {
                        if (rs.getString("neighborhood_name").compareTo(newAvailableAddNeighborhoods.get(i)) == 0) {
                            foundQuadrant = true;
                        }
                    }
                    if (!foundQuadrant) {
                        newAvailableNeighborhoods.add(rs.getString("neighborhood_name"));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * This method is responsible to realize a filtering of available quadrant,
     * by the search for an input string.
     */
    public void changeNeighborhoodFilter() {
        /*
         * funcion llamada cuando se modifica el campo que filtra los barrios 
         * que se agregaran cuando se esta editano un cadrante
         */
        availableNeighborhoods = new ArrayList<>();
        selectedAvailableNeighborhoods = new ArrayList<>();
        boolean foundQuadrant;
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods ORDER BY neighborhood_name ");
            while (rs.next()) {
                if (neighborhoodFilter != null && neighborhoodFilter.trim().length() != 0) {//hay cadena a buscar
                    if (rs.getString("neighborhood_name").toUpperCase().indexOf(neighborhoodFilter.toUpperCase()) != -1) {
                        foundQuadrant = false;
                        for (int i = 0; i < availableAddNeighborhoods.size(); i++) {
                            if (rs.getString("neighborhood_name").compareTo(availableAddNeighborhoods.get(i)) == 0) {
                                foundQuadrant = true;
                            }
                        }
                        if (!foundQuadrant) {
                            availableNeighborhoods.add(rs.getString("neighborhood_name"));
                        }
                    }
                } else {//no hay cadena a buscar
                    foundQuadrant = false;
                    for (int i = 0; i < availableAddNeighborhoods.size(); i++) {
                        if (rs.getString("neighborhood_name").compareTo(availableAddNeighborhoods.get(i)) == 0) {
                            foundQuadrant = true;
                        }
                    }
                    if (!foundQuadrant) {
                        availableNeighborhoods.add(rs.getString("neighborhood_name"));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Create a dynamic table with the results of a search.
     */
    public void createDynamicTable() {
        /*
         * Se crea la tabla que inicial que muestra todos los cuadrantes existentes 
         * o filtrados por el criterio de busqueda dado por el usuario.
         */
        if (currentSearchValue == null || currentSearchValue.trim().length() == 0) {
            currentSearchValue = "";
        }
        currentSearchValue = currentSearchValue.toUpperCase();
        rowDataTableList = new ArrayList<>();
        try {
            ResultSet rs;
            if (currentSearchCriteria == 2) {
                rs = connectionJdbcMB.consult("select * from quadrants where quadrant_name like '%" + currentSearchValue + "%'");
            } else {
                rs = connectionJdbcMB.consult("select * from quadrants where quadrant_id::text like '%" + currentSearchValue + "%'");
            }
            while (rs.next()) {
                rowDataTableList.add(new RowDataTable(rs.getString("quadrant_id"), rs.getString("quadrant_name")));
            }
        } catch (SQLException ex) {
        }
        if (rowDataTableList.isEmpty()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * clean the form is initialized to add a new quadrant.
     */
    public void newRegistry() {
        /*
         * Funcion usada cuando se presiona el botón nuevo, y se encarga de inicializar
         * todos los controles para la creacion de un nuevo cuadrante
         */
        selectedRowDataTable = null;//se quita elemento seleccionado de la tabla, se inhabilitan controles
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        newQuadrantId = String.valueOf(quadrantsFacade.findMax() + 1);//se limpia el formulario de nuevo cuadrante        
        newQuadrantName = "";
        newQuadrantPopuation = "0";
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        newAvailableAddNeighborhoods = new ArrayList<>();
        newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        changeNewNeighborhoodFilter();
        nameGeomFile = "";
        geomText = "<div style=\"color: red;\"><b>Geometría no cargada</b></div>";//nombre del archivo de geometria para nuevo barrio
        poligonText = "";
        disabledShowGeomFile = true;
    }

    /**
     * Resets the values of the Dynamic Table and the variables.
     */
    public void reset() {
        /*
         * Reinicio de controles y carga de la taba con el listado completo de cuadrantes
         */
        rowDataTableList = new ArrayList<>();
        createDynamicTable();
        newQuadrantPopuation = "0";
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        newAvailableAddNeighborhoods = new ArrayList<>();
        newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        poligonText = "";
        disabledShowGeomFile = true;
        nameGeomFile = "";
        geomText = "<div style=\"color: red;\"><b>Geometría no cargada</b></div>";
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------FUNCIONES GET Y SET ----------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;

    }

    public int getCurrentSearchCriteria() {
        return currentSearchCriteria;
    }

    public void setCurrentSearchCriteria(int currentSearchCriteria) {
        this.currentSearchCriteria = currentSearchCriteria;
    }

    public String getCurrentSearchValue() {
        return currentSearchValue;
    }

    public void setCurrentSearchValue(String currentSearchValue) {
        this.currentSearchValue = currentSearchValue;
    }

    public boolean isBtnEditDisabled() {
        return btnEditDisabled;
    }

    public void setBtnEditDisabled(boolean btnEditDisabled) {
        this.btnEditDisabled = btnEditDisabled;
    }

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }

    public QuadrantsFacade getQuadrantsFacade() {
        return quadrantsFacade;
    }

    public void setQuadrantsFacade(QuadrantsFacade quadrantsFacade) {
        this.quadrantsFacade = quadrantsFacade;
    }

    public List<Quadrants> getQuadrantsList() {
        return quadrantsList;
    }

    public void setQuadrantsList(List<Quadrants> quadrantsList) {
        this.quadrantsList = quadrantsList;
    }

    public String getQuadrantName() {
        return quadrantName;
    }

    public void setQuadrantName(String quadrantName) {
        this.quadrantName = quadrantName;
    }

    public String getQuadrantId() {
        return quadrantId;
    }

    public void setQuadrantId(String quadrantId) {
        this.quadrantId = quadrantId;
    }

    public String getQuadrantPopuation() {
        return quadrantPopuation;
    }

    public void setQuadrantPopuation(String quadrantPopuation) {
        this.quadrantPopuation = quadrantPopuation;
    }

    public String getNewQuadrantName() {
        return newQuadrantName;
    }

    public void setNewQuadrantName(String newQuadrantName) {
        this.newQuadrantName = newQuadrantName;
    }

    public String getNewQuadrantId() {
        return newQuadrantId;
    }

    public void setNewQuadrantId(String newQuadrantId) {
        this.newQuadrantId = newQuadrantId;
    }

    public String getNewQuadrantPopuation() {
        return newQuadrantPopuation;
    }

    public void setNewQuadrantPopuation(String newQuadrantPopuation) {
        this.newQuadrantPopuation = newQuadrantPopuation;
    }

    public List<String> getAvailableNeighborhoods() {
        return availableNeighborhoods;
    }

    public void setAvailableNeighborhoods(List<String> availableNeighborhoods) {
        this.availableNeighborhoods = availableNeighborhoods;
    }

    public List<String> getSelectedAvailableNeighborhoods() {
        return selectedAvailableNeighborhoods;
    }

    public void setSelectedAvailableNeighborhoods(List<String> selectedAvailableNeighborhoods) {
        this.selectedAvailableNeighborhoods = selectedAvailableNeighborhoods;
    }

    public List<String> getAvailableAddNeighborhoods() {
        return availableAddNeighborhoods;
    }

    public void setAvailableAddNeighborhoods(List<String> availableAddNeighborhoods) {
        this.availableAddNeighborhoods = availableAddNeighborhoods;
    }

    public List<String> getSelectedAvailableAddNeighborhoods() {
        return selectedAvailableAddNeighborhoods;
    }

    public void setSelectedAvailableAddNeighborhoods(List<String> selectedAvailableAddNeighborhoods) {
        this.selectedAvailableAddNeighborhoods = selectedAvailableAddNeighborhoods;
    }

    public List<String> getNewSelectedAvailableNeighborhoods() {
        return newSelectedAvailableNeighborhoods;
    }

    public void setNewSelectedAvailableNeighborhoods(List<String> newSelectedAvailableNeighborhoods) {
        this.newSelectedAvailableNeighborhoods = newSelectedAvailableNeighborhoods;
    }

    public List<String> getNewSelectedAvailableAddNeighborhoods() {
        return newSelectedAvailableAddNeighborhoods;
    }

    public void setNewSelectedAvailableAddNeighborhoods(List<String> newSelectedAvailableAddNeighborhoods) {
        this.newSelectedAvailableAddNeighborhoods = newSelectedAvailableAddNeighborhoods;
    }

    public String getNewNeighborhoodFilter() {
        return newNeighborhoodFilter;
    }

    public void setNewNeighborhoodFilter(String newNeighborhoodFilter) {
        this.newNeighborhoodFilter = newNeighborhoodFilter;
    }

    public String getNeighborhoodFilter() {
        return neighborhoodFilter;
    }

    public void setNeighborhoodFilter(String neighborhoodFilter) {
        this.neighborhoodFilter = neighborhoodFilter;
    }

    public String getNeighborhoodFilter2() {
        return neighborhoodFilter2;
    }

    public void setNeighborhoodFilter2(String neighborhoodFilter2) {
        this.neighborhoodFilter2 = neighborhoodFilter2;
    }

    public String getNewNeighborhoodFilter2() {
        return newNeighborhoodFilter2;
    }

    public void setNewNeighborhoodFilter2(String newNeighborhoodFilter2) {
        this.newNeighborhoodFilter2 = newNeighborhoodFilter2;
    }

    public List<String> getNewAvailableNeighborhoods() {
        return newAvailableNeighborhoods;
    }

    public void setNewAvailableNeighborhoods(List<String> newAvailableNeighborhoods) {
        this.newAvailableNeighborhoods = newAvailableNeighborhoods;
    }

    public List<String> getNewAvailableAddNeighborhoods() {
        return newAvailableAddNeighborhoods;
    }

    public void setNewAvailableAddNeighborhoods(List<String> newAvailableAddNeighborhoods) {
        this.newAvailableAddNeighborhoods = newAvailableAddNeighborhoods;
    }

    public boolean isDisabledShowGeomFile() {
        return disabledShowGeomFile;
    }

    public void setDisabledShowGeomFile(boolean disabledShowGeomFile) {
        this.disabledShowGeomFile = disabledShowGeomFile;
    }

    public String getNameGeomFile() {
        return nameGeomFile;
    }

    public void setNameGeomFile(String nameGeomFile) {
        this.nameGeomFile = nameGeomFile;
    }

    public String getGeomText() {
        return geomText;
    }

    public void setGeomText(String geomText) {
        this.geomText = geomText;
    }

    public String getPoligonText() {
        return poligonText;
    }

    public void setPoligonText(String poligonText) {
        this.poligonText = poligonText;
    }
}
