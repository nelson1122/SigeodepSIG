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
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import model.dao.CorridorsFacade;
import model.dao.NeighborhoodsFacade;
import model.pojo.Corridors;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * The CorridorsVariableMB class is responsible to manage everything related to
 * the corridors, which are associated with it a set of neighborhoods to specify
 * what adding or adding lists of available neighborhoods and districts added to
 * the current broker.
 *
 * @author SANTOS
 */
@ManagedBean(name = "corridorsVariableMB")
@SessionScoped
public class CorridorsVariableMB implements Serializable {

    /*
     * COMUNAS
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 2;
    private String currentSearchValue = "";
    @EJB
    CorridorsFacade corridorsFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private List<Corridors> corridorsList;
    private Corridors currentCorridor;
    private String corridorName = "";//Nombre del corredor.
    private String corridorId = "";//Código del corredor.
    private String corridorPopuation = "0";
    private String newCorridorName = "";//Nombre del corredor.
    private String newCorridorId = "";//Código del corredor.
    private String newCorridorPopuation = "0";
    private String poligonText = "";//poligono para el nuevo barrio    
    private boolean disabledShowGeomFile = true;//activar/desactivar boton de ver archivo KML
    private String geomText = "<div style=\"color: red;\"><b>Geometría no cargada</b></div>";//aviso de si la geometria esta o no cargada
    private String nameGeomFile = "";//nombre del archivo de geometria para barrio existente
    private String newNeighborhoodFilter = "";
    private String neighborhoodFilter = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private UploadedFile file;
    private List<String> availableNeighborhoods = new ArrayList<>();
    private List<String> selectedAvailableNeighborhoods = new ArrayList<>();
    private List<String> availableAddNeighborhoods = new ArrayList<>();
    private List<String> selectedAvailableAddNeighborhoods = new ArrayList<>();
    private List<String> newAvailableNeighborhoods = new ArrayList<>();
    private List<String> newSelectedAvailableNeighborhoods = new ArrayList<>();
    private List<String> newAvailableAddNeighborhoods = new ArrayList<>();
    private List<String> newSelectedAvailableAddNeighborhoods = new ArrayList<>();
    ConnectionJdbcMB connectionJdbcMB;
    private String realPath = "";

    /**
     * Establishes the connection to the database, along with its directory.
     */
    public CorridorsVariableMB() {
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
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
        cell.setCellStyle(cellStyle);
    }

    /**
     * It is responsible for creating a cell within the row.
     *
     * @param fila: row where create the cell
     * @param position: Determines the position where anger cell within the row.
     * @param value: Sets the value that will be created within the cell.
     */
    private void createCell(HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    /**
     * runs a xls file where the user insert a row within a worksheet where two
     * fields are set: CODE, NAME.
     *
     * @param document: Document to modify the name and code field.
     */
    public void postProcessXLS(Object document) {
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
        corridorsList = corridorsFacade.findAll();
        for (int i = 0; i < corridorsList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, corridorsList.get(i).getCorridorId().toString());//CODIGO
            createCell(row, 1, corridorsList.get(i).getCorridorName());//NOMBRE            

        }
    }

    /**
     * copy the file geometry.
     *
     * @param fileName
     * @param in
     */
    private void copyFile(String fileName, InputStream in) {


        disabledShowGeomFile = true;
        nameGeomFile = "Archivo no cargado";
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
                } catch (IOException e) {
                    System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
                }
            } else {
                System.out.println("No se encuentra la carpeta");
            }
        } catch (Exception e) {
            System.out.println("No se pudo procesar el archivo");
        }
    }

    /**
     * Allows upload the file of geometry of the neighborhood.
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        /*
         * cargar el archivo de geometria del varrio
         */
        nameGeomFile = "";//nombre del archivo de geometria para nuevo barrio
        try {
            //realizo la copia de este archivo a la carpeta correspondiente a geometrias
            file = event.getFile();
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
            //newNameGeomFile = event.getFile().getFileName();
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo realizar la carga de este archivo"));
        }
    }

    /**
     * Add a neighborhood to a new corridor.
     */
    public void addNeighborhoodInNewQuadrantClick() {
        /*
         * adicionar un barrio en un nuevo corredor
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
        //newQuadrantsFilter = "";
        //changeNewQuadrantsFilter();
    }

    /**
     * This method is responsible to add a neighborhood to the list added , when
     * editing an existing corridor.
     */
    public void addNeighborhoodInExistingQuadrantClick() {
        /*
         * adicionar un barrio a la lista de agregados, cuando se esta editando un corredor existente
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
        //quadrantsFilter = "";
        //changeQuadrantsFilter();
    }

    /**
     * permits to remove a neighborhood the list of aggregates, when you are
     * creating a new corridor
     */
    public void removeNeighborhoodInNewQuadrantClick() {
        /*
         * quitar un barrio de la lista de agregados, cuando se esta creando un nuevo corredor
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
        //newQuadrantsFilter = "";
        //changeNewQuadrantsFilter();
    }

    /**
     * permits to remove of a neighborhood the list of aggregates, when you are
     * editing an existing corridor.
     */
    public void removeNeighborhoodInExistingQuadrantClick() {
        /*
         * quitar un barrio de la lista de agregados, cuando se esta editando un corredor existente
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
        //quadrantsFilter = "";
        //changeQuadrantsFilter();
    }

    /**
     * load data of a record when select a row of the table that show the
     * existent corridor
     */
    public void loadRegistry() {
        /*
         * carga de los datos de un registro cuando se selecciona una fila de 
         * la tabla que muestra los corredors existentes
         */
        disabledShowGeomFile = true;
        currentCorridor = null;
        if (selectedRowDataTable != null) {
            currentCorridor = corridorsFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentCorridor != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentCorridor.getCorridorName() != null) {
                corridorName = currentCorridor.getCorridorName();
            } else {
                corridorName = "";
            }
            if (currentCorridor.getCorridorId() != null) {
                corridorId = currentCorridor.getCorridorId().toString();// integer NOT NULL, -- Código del corredor.
            } else {
                corridorId = "";
            }
            if (currentCorridor.getPopulation() != null) {
                corridorPopuation = String.valueOf(currentCorridor.getPopulation());
            } else {
                corridorPopuation = "0";
            }
            //determino si la geometria ya esta cargada
            if (currentCorridor.getGeom() != null && currentCorridor.getGeom().trim().length() != 0) {
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
                        + " (SELECT neighborhood_id FROM neighborhoods "
                        + " WHERE neighborhood_corridor = " + currentCorridor.getCorridorId().toString() + ") ");
                while (rs.next()) {
                    availableNeighborhoods.add(rs.getString("neighborhood_name"));
                }
                rs = connectionJdbcMB.consult(""
                        + " SELECT * FROM neighborhoods WHERE neighborhood_id IN "
                        + " (SELECT neighborhood_id FROM neighborhoods "
                        + " WHERE neighborhood_corridor = " + currentCorridor.getCorridorId().toString() + ") ");
                while (rs.next()) {
                    availableAddNeighborhoods.add(rs.getString("neighborhood_name"));
                }
            } catch (Exception e) {
            }
        }
    }

    public void showGeomFileClick() {
        poligonText = "";
    }

    /**
     * Load the geometry of a selected commune
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
     * Deletes a record from the table corridor.
     */
    public void deleteRegistry() {
        if (currentCorridor != null) {
            //se elimina de la tabla corredors 
            connectionJdbcMB.setShowMessages(false);
            connectionJdbcMB.non_query(""
                    + " DELETE FROM corridors WHERE corridor_id = " + currentCorridor.getCorridorId() + "; \n");
            if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Existen barrios que pertenecen a este ccorredor, edite este corredor y quite los barrios agregados para poder realizar esta eliminación"));
            } else {
                currentCorridor = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El registro fue eliminado"));
            }
            connectionJdbcMB.setShowMessages(true);
        }
    }

    /**
     * Updates a record in table corridors.
     */
    public void updateRegistry() {
        if (currentCorridor != null) {
            boolean continueProcess = true;
            if (corridorName.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
                continueProcess = false;
            }
            if (continueProcess) {
                if (corridorId.trim().length() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un codigo"));
                    continueProcess = false;
                }
            }
            if (continueProcess) {
                corridorName = corridorName.toUpperCase();
                try {
                    //buscar si el nombre del corredor  ya esta registrado
                    ResultSet rs = connectionJdbcMB.consult("SELECT * FROM corridors "
                            + " WHERE corridor_name LIKE '" + corridorName + "' AND "
                            + " corridor_name NOT LIKE '" + currentCorridor.getCorridorName() + "'");
                    if (rs.next()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un corredor con un nombre igual"));
                        continueProcess = false;
                    }
                } catch (SQLException ex) {
                }
            }
            if (continueProcess) {
                corridorName = corridorName.toUpperCase();
                currentCorridor.setCorridorName(corridorName);
                currentCorridor.setPopulation(Integer.parseInt(corridorPopuation));
                if (poligonText != null && poligonText.trim().length() != 0) {
                    currentCorridor.setGeom(poligonText);
                }
                corridorsFacade.edit(currentCorridor);
                String sql = "";
                //a los barrios que contengan este corredor les asigno sin dato, por que no se sabe si quedaran todas 
                connectionJdbcMB.non_query("UPDATE neighborhoods SET neighborhood_corridor = 0  WHERE neighborhood_corridor = " + currentCorridor.getCorridorId());
                //se inserta los diferentes barrios que se haya indicado
                if (availableAddNeighborhoods != null && !availableAddNeighborhoods.isEmpty()) {
                    for (int i = 0; i < availableAddNeighborhoods.size(); i++) {
                        sql = sql
                                + " UPDATE neighborhoods "
                                + " SET neighborhood_corridor = " + currentCorridor.getCorridorId() + " "
                                + " WHERE neighborhood_name LIKE '" + availableAddNeighborhoods.get(i) + "'; \n";
                    }
                }
                connectionJdbcMB.non_query(sql);
                //reinicio controles
                corridorName = "";
                currentCorridor = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    /**
     * Save the registration of a new corridor and verify that the new row to be
     * inserted does not exist in the table corridor.
     */
    public void saveRegistry() {
        boolean continueProcess = true;

        if (newCorridorName.trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
            continueProcess = false;
        }
        if (continueProcess) {
            newCorridorName = newCorridorName.toUpperCase();
            try {
                //buscar si el nombre de corredor ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM corridors WHERE corridor_name LIKE '" + newCorridorName + "'");
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un corredor con un nombre igual"));
                    continueProcess = false;
                }
            } catch (SQLException ex) {
            }
        }
        if (continueProcess) {
            try {
                //buscar si el codigo o corredor ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM corridors WHERE corridor_id = " + newCorridorId);
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un corredor con un codigo igual"));
                    continueProcess = false;
                }
            } catch (SQLException ex) {
            }
        }
        if (continueProcess) {
            try {
                String sql = "INSERT INTO corridors VALUES (";
                String geom = "null";
                if (poligonText != null && poligonText.trim().length() != 0) {
                    geom = "'" + poligonText + "'";
                }
                sql = sql
                        + newCorridorId + ",'"//codigo
                        + newCorridorName + "',"//nombre
                        + newCorridorPopuation + ","//poblacion
                        + geom + "); \n";//geometria

//                sql = sql
//                        + " UPDATE neighborhoods "
//                        + " SET neighborhood_corridor = null "
//                        + " WHERE neighborhood_corridor = " + newCorridorId + " \n";
                //se inserta los diferentes barrios que se haya indicado
                if (newAvailableAddNeighborhoods != null && !newAvailableAddNeighborhoods.isEmpty()) {
                    for (int i = 0; i < newAvailableAddNeighborhoods.size(); i++) {
                        sql = sql
                                + " UPDATE neighborhoods "
                                + " SET neighborhood_corridor = " + newCorridorId + " "
                                + " WHERE neighborhood_name LIKE '" + newAvailableAddNeighborhoods.get(i) + "' \n";
                    }
                }
                connectionJdbcMB.non_query(sql);
            } catch (Exception e) {
            }

            currentCorridor = null;
            selectedRowDataTable = null;
            newNeighborhoodFilter = "";
            createDynamicTable();
            newRegistry();//limpiar formulario
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Nuevo Corredor almacenado.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * This method allows add a new record
     */
    public void newRegistry() {
        //se quita elemento seleccionado de la tabla, se inhabilitan controles
        selectedRowDataTable = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        //se limpia el formulario        
        newCorridorId = String.valueOf(corridorsFacade.findMax() + 1);//id del corredor.
        newCorridorName = "";//Nombre del corredor
        newCorridorPopuation = "0";
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        newAvailableAddNeighborhoods = new ArrayList<>();
        newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        changeNewNeighborhoodFilter();//determinar barrios

        nameGeomFile = "";
        geomText = "<div style=\"color: red;\"><b>Geometría no cargada</b></div>";//nombre del archivo de geometria para nuevo barrio
        poligonText = "";
        disabledShowGeomFile = true;
    }

    /**
     * changes the value of population field by a value numeric new greater or
     * equal zero
     */
    public void changeNewPopulation() {
        try {
            int c = Integer.parseInt(newCorridorPopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                newCorridorPopuation = "0";
            }
        } catch (Exception e) {
            if (newCorridorPopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            newCorridorPopuation = "0";
        }
    }

    /**
     * changes the value of population field by a value numeric greater or equal
     * zero
     */
    public void changePopulation() {
        try {
            int c = Integer.parseInt(corridorPopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                corridorPopuation = "0";
            }
        } catch (Exception e) {
            if (corridorPopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            corridorPopuation = "0";
        }
    }

    /**
     * Make filter of neighborhoods available through the search for a string
     * entered.
     */
    public void changeNewNeighborhoodFilter() {
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
     * Make filter of neighborhoods available through the search for a string
     * entered.
     */
    public void changeNeighborhoodFilter() {
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

        if (currentSearchValue == null || currentSearchValue.trim().length() == 0) {
            currentSearchValue = "";
        }
        currentSearchValue = currentSearchValue.toUpperCase();
        rowDataTableList = new ArrayList<>();
        ResultSet rs;
        try {
            if (currentSearchCriteria == 2) {
                rs = connectionJdbcMB.consult("select * from corridors where corridor_name like '%" + currentSearchValue + "%'");
            } else {
                rs = connectionJdbcMB.consult("select * from corridors where corridor_id::text like '%" + currentSearchValue + "%'");
            }
            while (rs.next()) {
                rowDataTableList.add(new RowDataTable(rs.getString("corridor_id"), rs.getString("corridor_name")));
            }
        } catch (SQLException ex) {
        }
        if (rowDataTableList.isEmpty()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Resets the values of the DynamicTable.
     */
    public void reset() {
        rowDataTableList = new ArrayList<>();
        createDynamicTable();
        //quadrantsList = quadrantsFacade.findAll();
        newCorridorPopuation = "0";
        //cargo los barrios
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        newAvailableAddNeighborhoods = new ArrayList<>();
        newSelectedAvailableAddNeighborhoods = new ArrayList<>();

        poligonText = "";
        disabledShowGeomFile = true;
        nameGeomFile = "Archivo no cargado";//nombre del archivo de geometria para nuevo barrio
        geomText = "Geometría no cargada";//nombre del archivo de geometria para nuevo barrio
        nameGeomFile = "";//nombre del archivo de geometria para barrio existente
    }

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

    public String getCorridorName() {
        return corridorName;
    }

    public void setCorridorName(String corridorName) {
        this.corridorName = corridorName;
    }

    public String getCorridorId() {
        return corridorId;
    }

    public void setCorridorId(String corridorId) {
        this.corridorId = corridorId;
    }

    public String getCorridorPopuation() {
        return corridorPopuation;
    }

    public void setCorridorPopuation(String corridorPopuation) {
        this.corridorPopuation = corridorPopuation;
    }

    public String getNewCorridorName() {
        return newCorridorName;
    }

    public void setNewCorridorName(String newCorridorName) {
        this.newCorridorName = newCorridorName;
    }

    public String getNewCorridorId() {
        return newCorridorId;
    }

    public void setNewCorridorId(String newCorridorId) {
        this.newCorridorId = newCorridorId;
    }

    public String getNewCorridorPopuation() {
        return newCorridorPopuation;
    }

    public void setNewCorridorPopuation(String newCorridorPopuation) {
        this.newCorridorPopuation = newCorridorPopuation;
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

    public List<String> getNewAvailableNeighborhoods() {
        return newAvailableNeighborhoods;
    }

    public void setNewAvailableNeighborhoods(List<String> newAvailableNeighborhoods) {
        this.newAvailableNeighborhoods = newAvailableNeighborhoods;
    }

    public List<String> getNewSelectedAvailableNeighborhoods() {
        return newSelectedAvailableNeighborhoods;
    }

    public void setNewSelectedAvailableNeighborhoods(List<String> newSelectedAvailableNeighborhoods) {
        this.newSelectedAvailableNeighborhoods = newSelectedAvailableNeighborhoods;
    }

    public List<String> getNewAvailableAddNeighborhoods() {
        return newAvailableAddNeighborhoods;
    }

    public void setNewAvailableAddNeighborhoods(List<String> newAvailableAddNeighborhoods) {
        this.newAvailableAddNeighborhoods = newAvailableAddNeighborhoods;
    }

    public List<String> getNewSelectedAvailableAddNeighborhoods() {
        return newSelectedAvailableAddNeighborhoods;
    }

    public void setNewSelectedAvailableAddNeighborhoods(List<String> newSelectedAvailableAddNeighborhoods) {
        this.newSelectedAvailableAddNeighborhoods = newSelectedAvailableAddNeighborhoods;
    }

    public String getNameGeomFile() {
        return nameGeomFile;
    }

    public void setNameGeomFile(String nameGeomFile) {
        this.nameGeomFile = nameGeomFile;
    }

    public boolean isDisabledShowGeomFile() {
        return disabledShowGeomFile;
    }

    public void setDisabledShowGeomFile(boolean disabledShowGeomFile) {
        this.disabledShowGeomFile = disabledShowGeomFile;
    }

    public String getPoligonText() {
        return poligonText;
    }

    public void setPoligonText(String poligonText) {
        this.poligonText = poligonText;
    }

    public String getGeomText() {
        return geomText;
    }

    public void setGeomText(String geomText) {
        this.geomText = geomText;
    }
}
