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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import model.dao.CorridorsFacade;
import model.dao.NeighborhoodsFacade;
import model.pojo.Corridors;
import model.pojo.Neighborhoods;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * The NeighborhoodsVariableMB class is responsible for managing everything
 * related to neighborhoods, allowing user to have available a list of
 * neighborhoods available which can be added, edited, deleted and exported in
 * xls list.
 *
 * @author SANTOS
 */
@ManagedBean(name = "neighborhoodsVariableMB")
@SessionScoped
public class NeighborhoodsVariableMB implements Serializable {

    /*
     * BARRIOS
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    //@EJB
    //CommunesFacade communesFacade;
    @EJB
    CorridorsFacade corridorsFacade;
    private List<Neighborhoods> neighborhoodsList;
    private SelectItem[] communesList;
    private SelectItem[] newCommunesList;
    private SelectItem[] corridorsList;
    private Neighborhoods currentNeighborhood;
    private String type = "";
    private String poligonText = "";//poligono para el nuevo barrio    
    private boolean disabledShowGeomFile = true;//activar/desactivar boton de ver archivo KML
    private String geomText = "<div style=\"color: red;\"><b>Geometría no cargada</b></div>";//aviso de si la geometria esta o no cargada
    private String nameGeomFile = "";//nombre del archivo de geometria para barrio existente
    private String neighborhoodName = "";//Nombre del barrio.
    private String neighborhoodId = "";//Código del barrio.
    private String neighborhoodSuburbId = "";//Código de la comuna.
    private String neighborhoodLevel = "";//Estrato socioeconómico del barrio.
    private String neighborhoodType = "";//Tipo de barrio (zona)
    private String neighborhoodCorridor = "";
    private String newNeighborhoodName = "";//Nombre del barrio.
    private String newNeighborhoodId = "";//Código del barrio.
    private String newNeighborhoodSuburbId = "";//Código de la comuna.
    private String newNeighborhoodLevel = "";//Estrato socioeconómico del barrio.
    private String newNeighborhoodType = "";//Tipo de barrio (zona)
    private String newNeighborhoodCorridor = "";
    private String newQuadrantsFilter = "";
    private String quadrantsFilter = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private UploadedFile file;
    private List<String> availableQuadrants = new ArrayList<>();
    private List<String> selectedAvailableQuadrants = new ArrayList<>();
    private List<String> availableAddQuadrants = new ArrayList<>();
    private List<String> selectedAvailableAddQuadrants = new ArrayList<>();
    private List<String> newAvailableQuadrants = new ArrayList<>();
    private List<String> newSelectedAvailableQuadrants = new ArrayList<>();
    private List<String> newAvailableAddQuadrants = new ArrayList<>();
    private List<String> newSelectedAvailableAddQuadrants = new ArrayList<>();
    ConnectionJdbcMB connectionJdbcMB;
    private String newPopuation = "0";
    private String popuation = "0";
    private String realPath = "";//ruta real en el servidor de la aplicacion

    /**
     * This method is responsible to create the connection to the database and
     * set the path.
     */
    public NeighborhoodsVariableMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        realPath = (String) servletContext.getRealPath("/");
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
        createCell(cellStyle, row, 2, "ZONA");
        neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, neighborhoodsList.get(i).getNeighborhoodId().toString());//CODIGO
            createCell(row, 1, neighborhoodsList.get(i).getNeighborhoodName());//NOMBRE            
            if (neighborhoodsList.get(i).getNeighborhoodArea() != null) {
                neighborhoodType = String.valueOf(currentNeighborhood.getNeighborhoodArea());// character(1), -- Tipo de barrio.
                if (neighborhoodsList.get(i).getNeighborhoodArea().toString().compareTo("1") == 0) {//ZONA URBANA
                    createCell(row, 2, "ZONA URBANA");//NOMBRE            
                } else {//ZONA RURAL
                    createCell(row, 2, "ZONA RURAL");//NOMBRE            
                }
            }
        }
    }

    /**
     * copy the file to the geometry of the neighborhood.
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
     * upload the geometry of the neighborhood.
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
     * This method is responsible to add a quadrant in a new neighborhood.
     */
    public void addQuadrantInNewNeighborhoodClick() {
        /*
         * adicionar un cuadrante en un nuevo barrio
         */
        if (newSelectedAvailableQuadrants != null && !newSelectedAvailableQuadrants.isEmpty()) {
            for (int i = 0; i < newSelectedAvailableQuadrants.size(); i++) {
                //lo adiciono a la lista de agregados
                newAvailableAddQuadrants.add(newSelectedAvailableQuadrants.get(i));
                //lo elimino de disponibles
                for (int j = 0; j < newAvailableQuadrants.size(); j++) {
                    if (newAvailableQuadrants.get(j).compareTo(newSelectedAvailableQuadrants.get(i)) == 0) {
                        newAvailableQuadrants.remove(j);
                        break;
                    }
                }
            }
            newSelectedAvailableAddQuadrants = new ArrayList<>();
        }
        //newQuadrantsFilter = "";
        //changeNewQuadrantsFilter();
    }

    /**
     * This method is responsible to add one quadrant to the list of aggregates,
     * when you are editing an existing neighborhood.
     */
    public void addQuadrantInExistingNeighborhoodClick() {
        /*
         * adicionar un cuadrante a la lista de agregados, cuando se esta editando un barrio existente
         */
        if (selectedAvailableQuadrants != null && !selectedAvailableQuadrants.isEmpty()) {
            for (int i = 0; i < selectedAvailableQuadrants.size(); i++) {
                //lo adiciono a la lista de agregados
                availableAddQuadrants.add(selectedAvailableQuadrants.get(i));
                //lo elimino de disponibles
                for (int j = 0; j < availableQuadrants.size(); j++) {
                    if (availableQuadrants.get(j).compareTo(selectedAvailableQuadrants.get(i)) == 0) {
                        availableQuadrants.remove(j);
                        break;
                    }
                }
            }
            selectedAvailableAddQuadrants = new ArrayList<>();
        }
        //quadrantsFilter = "";
        //changeQuadrantsFilter();
    }

    /**
     * Remove one quadrant of the aggregate list when you are creating a new
     * neighborhood.
     */
    public void removeQuadrantInNewNeighborhoodClick() {
        /*
         * quitar un cuadrante de la lista de agregados, cuando se esta creando un nuevo barrio
         */
        if (newSelectedAvailableAddQuadrants != null && !newSelectedAvailableAddQuadrants.isEmpty()) {
            for (int i = 0; i < newSelectedAvailableAddQuadrants.size(); i++) {
                //lo adiciono a la lista de disonibles
                newAvailableQuadrants.add(newSelectedAvailableAddQuadrants.get(i));
                //lo elimino de agregados
                for (int j = 0; j < newAvailableAddQuadrants.size(); j++) {
                    if (newAvailableAddQuadrants.get(j).compareTo(newSelectedAvailableAddQuadrants.get(i)) == 0) {
                        newAvailableAddQuadrants.remove(j);
                        break;
                    }
                }
            }
            newSelectedAvailableAddQuadrants = new ArrayList<>();
        }
        //newQuadrantsFilter = "";
        //changeNewQuadrantsFilter();
    }

    /**
     * Remove one quadrant of the list of aggregates, when you are editing an
     * existing neighborhood.
     */
    public void removeQuadrantInExistingNeighborhoodClick() {
        /*
         * quitar un cuadrante de la lista de agregados, cuando se esta editando un barrio existente
         */
        if (selectedAvailableAddQuadrants != null && !selectedAvailableAddQuadrants.isEmpty()) {
            for (int i = 0; i < selectedAvailableAddQuadrants.size(); i++) {
                //lo adiciono a la lista de disonibles
                availableQuadrants.add(selectedAvailableAddQuadrants.get(i));
                //lo elimino de agregados
                for (int j = 0; j < availableAddQuadrants.size(); j++) {
                    if (availableAddQuadrants.get(j).compareTo(selectedAvailableAddQuadrants.get(i)) == 0) {
                        availableAddQuadrants.remove(j);
                        break;
                    }
                }
            }
            selectedAvailableAddQuadrants = new ArrayList<>();
        }
    }

    /**
     * load the data of a record when the user selects a row of the table that
     * show the existing neighborhoods
     */
    public void loadRegistry() {
        /*
         * carga de los datos de un registro cuando se selecciona una fila de 
         * la tabla que muestra los barrios existentes
         */
        disabledShowGeomFile = true;
        currentNeighborhood = null;
        if (selectedRowDataTable != null) {
            currentNeighborhood = neighborhoodsFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
        if (currentNeighborhood != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentNeighborhood.getNeighborhoodName() != null) {
                neighborhoodName = currentNeighborhood.getNeighborhoodName();
            } else {
                neighborhoodName = "";
            }
            if (currentNeighborhood.getNeighborhoodId() != null) {
                neighborhoodId = currentNeighborhood.getNeighborhoodId().toString();// integer NOT NULL, -- Código del barrio.
            } else {
                neighborhoodId = "";
            }
            if (currentNeighborhood.getNeighborhoodArea() != null) {
                neighborhoodType = String.valueOf(currentNeighborhood.getNeighborhoodArea());// character(1), -- Tipo de barrio.
                if (neighborhoodType.compareTo("1") == 0) {//ZONA URBANA
                } else {//ZONA RURAL
                    neighborhoodSuburbId = "2";
                }
            } else {
                neighborhoodType = "1";
            }
            if (currentNeighborhood.getNeighborhoodLevel() != null) {
                neighborhoodLevel = String.valueOf(currentNeighborhood.getNeighborhoodLevel());// character(1), -- Estrato socioeconómico del barrio.
            } else {
                neighborhoodLevel = "";
            }

            if (currentNeighborhood.getNeighborhoodSuburb() != -1) {
                neighborhoodSuburbId = String.valueOf(currentNeighborhood.getNeighborhoodSuburb());// character(1), -- Tipo de barrio.
            } else {
                neighborhoodSuburbId = "";
            }
            if (currentNeighborhood.getPopulation() != null) {
                popuation = String.valueOf(currentNeighborhood.getPopulation());
            } else {
                popuation = "0";
            }
            if (currentNeighborhood.getNeighborhoodCorridor() != null) {
                neighborhoodCorridor = String.valueOf(currentNeighborhood.getNeighborhoodCorridor());
            } else {
                neighborhoodCorridor = "0";
            }
            changeArea();//sacar lista de comunas
            if (currentNeighborhood.getNeighborhoodSuburb() != -1) {
                neighborhoodSuburbId = String.valueOf(currentNeighborhood.getNeighborhoodSuburb());// character(1), -- Tipo de barrio.
            } else {
                neighborhoodSuburbId = "";
            }

            //determino si la geometria ya esta cargada
            if (currentNeighborhood.getGeom() != null && currentNeighborhood.getGeom().trim().length() != 0) {
                geomText = "<div style=\"color: blue;\"><b>Tiene geometría</b></div>";
            } else {
                geomText = "<div style=\"color: red;\"><b>No tiene geometría</b></div>";
            }

            availableQuadrants = new ArrayList<>();
            selectedAvailableQuadrants = new ArrayList<>();
            availableAddQuadrants = new ArrayList<>();
            selectedAvailableAddQuadrants = new ArrayList<>();
            quadrantsFilter = "";

            //determino los cuadrantes
            availableQuadrants = new ArrayList<>();
            selectedAvailableQuadrants = new ArrayList<>();
            availableAddQuadrants = new ArrayList<>();
            selectedAvailableAddQuadrants = new ArrayList<>();
            try {
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT * FROM quadrants WHERE quadrant_id NOT IN "
                        + " (SELECT quadrant_id FROM neighborhood_quadrant "
                        + " WHERE neighborhood_id = " + currentNeighborhood.getNeighborhoodId().toString() + ") ");
                while (rs.next()) {
                    availableQuadrants.add(rs.getString("quadrant_name"));
                }
                rs = connectionJdbcMB.consult(""
                        + " SELECT * FROM quadrants WHERE quadrant_id IN "
                        + " (SELECT quadrant_id FROM neighborhood_quadrant "
                        + " WHERE neighborhood_id = " + currentNeighborhood.getNeighborhoodId().toString() + ") ");
                while (rs.next()) {
                    availableAddQuadrants.add(rs.getString("quadrant_name"));
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Initialize the variable that receives the text of the loaded polygon.
     */
    public void showGeomFileClick() {
        poligonText = "";
    }

    /**
     * load the selected geometry.
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
     * removes a neighborhood, but before making their removal verify that it is
     * not in use.
     */
    public void deleteRegistry() {
        if (currentNeighborhood != null) {
            //se elimina de la tabla cuadrantes 
            connectionJdbcMB.setShowMessages(false);
            connectionJdbcMB.non_query(""
                    + " DELETE FROM neighborhood_quadrant WHERE neighborhood_id = " + currentNeighborhood.getNeighborhoodId() + "; \n"
                    + " DELETE FROM neighborhoods WHERE neighborhood_id = " + currentNeighborhood.getNeighborhoodId() + "; \n");
            if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El sistema esta haciendo uso de este barrio por lo cual no puede ser eliminado"));
            } else {
                currentNeighborhood = null;
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
     * updates a record corresponding to a neighborhood, in updating
     * neighborhood_quadrant inserted into the table, the id of the neighborhood
     * and the corridor.
     */
    public void updateRegistry() {
        if (currentNeighborhood != null) {
            boolean continueProcess = true;
            if (neighborhoodName.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
                continueProcess = false;
            }
            if (continueProcess) {
                if (neighborhoodId.trim().length() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un codigo"));
                    continueProcess = false;
                }
            }
            if (continueProcess) {
                neighborhoodName = neighborhoodName.toUpperCase();
                try {
                    //buscar si el nombre de barrio ya esta registrado
                    ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods "
                            + " WHERE neighborhood_name LIKE '" + neighborhoodName + "' AND "
                            + " neighborhood_name NOT LIKE '" + currentNeighborhood.getNeighborhoodName() + "'");
                    if (rs.next()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un barrio con un nombre igual"));
                        continueProcess = false;
                    }
                } catch (SQLException ex) {
                }
            }
            if (continueProcess) {
                neighborhoodName = neighborhoodName.toUpperCase();
                currentNeighborhood.setNeighborhoodName(neighborhoodName);
                //currentNeighborhood.setNeighborhoodId(Integer.parseInt(neighborhoodId));
                currentNeighborhood.setNeighborhoodArea(Short.parseShort(neighborhoodType));
                currentNeighborhood.setNeighborhoodSuburb(Short.parseShort(neighborhoodSuburbId));
                currentNeighborhood.setNeighborhoodLevel(Short.parseShort(neighborhoodLevel));
                currentNeighborhood.setPopulation(Integer.parseInt(popuation));
                currentNeighborhood.setNeighborhoodCorridor(Short.parseShort(neighborhoodCorridor));
                neighborhoodsFacade.edit(currentNeighborhood);

                String sql = "";
                //elimino los cuadrantes de este barrio
                connectionJdbcMB.non_query("DELETE FROM neighborhood_quadrant WHERE neighborhood_id = " + currentNeighborhood.getNeighborhoodId());
                //se inserta los diferentes cuadrantes que se haya indicado
                if (availableAddQuadrants != null && !availableAddQuadrants.isEmpty()) {
                    if (availableAddQuadrants.size() > 1) {//quitar sin dato si existe mas de un cuadrante agregado
                        for (int i = 0; i < availableAddQuadrants.size(); i++) {
                            if (availableAddQuadrants.get(i).compareToIgnoreCase("SIN DATO") == 0) {
                                availableAddQuadrants.remove(i);
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < availableAddQuadrants.size(); i++) {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + "SELECT quadrant_id FROM quadrants WHERE quadrant_name LIKE '" + availableAddQuadrants.get(i) + "'");
                        try {
                            if (rs.next()) {
                                sql = sql
                                        + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                                        + neighborhoodId + ","//id_barrio
                                        + rs.getString(1) + "); \n";//corredor
                            }
                        } catch (SQLException e) {
                        }
                    }
                } else {//se agrega sin dato si no se ha seleccionado algun corredor
                    sql = sql
                            + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                            + neighborhoodId + ","//id_barrio
                            + "0); \n";//id_corredor
                }
                connectionJdbcMB.non_query(sql);

                //1. modifico los eventos para que no tengan ningun cuadrante
                connectionJdbcMB.non_query(""
                        + " update non_fatal_injuries set quadrant_id = 0 WHERE injury_neighborhood_id = " + neighborhoodId + "; \n"
                        + " update fatal_injuries set quadrant_id = 0 WHERE injury_neighborhood_id = " + neighborhoodId + "; \n");
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
                //5. para los barrios SIN DATO RURAL y SIN DATO URBANO el cuadrante es cero
                connectionJdbcMB.non_query(""
                        + " UPDATE non_fatal_injuries SET quadrant_id = 0 WHERE injury_neighborhood_id = 52001; "
                        + " UPDATE fatal_injuries SET quadrant_id = 0 WHERE injury_neighborhood_id = 52002; ");
                //reinicio controles
                neighborhoodName = "";
                currentNeighborhood = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    /**
     * Save the registration of a new neighborhood and verify that the new row
     * to be inserted does not exist in the neighborhoods table.
     */
    public void saveRegistry() {
        /*
         * 
         */
        boolean continueProcess = true;

        if (newNeighborhoodName.trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
            continueProcess = false;
        }
        if (continueProcess) {
            if (newNeighborhoodId.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un codigo"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            newNeighborhoodName = newNeighborhoodName.toUpperCase();
            try {
                //buscar si el codigo o barrio ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods WHERE neighborhood_name LIKE '" + newNeighborhoodName + "'");
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un barrio con un nombre igual"));
                    continueProcess = false;
                }
            } catch (SQLException ex) {
            }
        }
        if (continueProcess) {
            try {
                //buscar si el codigo o barrio ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods WHERE neighborhood_id = " + newNeighborhoodId);
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un barrio con un codigo igual"));
                    continueProcess = false;
                }
            } catch (SQLException ex) {
            }
        }
        if (continueProcess) {
            try {
                String sql = "INSERT INTO neighborhoods VALUES (";
                String geom = "null";
                if (poligonText != null && poligonText.trim().length() != 0) {
                    geom = "'" + poligonText + "'";
                }
                sql = sql
                        + newNeighborhoodId + ",'"//codigo
                        + newNeighborhoodName + "',"//nombre
                        + newNeighborhoodSuburbId + ","//comuna
                        + newPopuation + ","//poblacion
                        + geom + ","//geometria
                        + "0,"//cuadrante
                        + newNeighborhoodType + ","//area
                        + newNeighborhoodLevel + ","//estrato
                        + newNeighborhoodCorridor + "); \n";//corredor
                //se inserta los diferentes cuadrantes que se haya indicado
                if (newAvailableAddQuadrants != null && !newAvailableAddQuadrants.isEmpty()) {

                    if (newAvailableAddQuadrants.size() > 1) {//quitar sin dato si existe mas de un cuadrante agregado
                        for (int i = 0; i < newAvailableAddQuadrants.size(); i++) {
                            if (newAvailableAddQuadrants.get(i).compareToIgnoreCase("SIN DATO") == 0) {
                                newAvailableAddQuadrants.remove(i);
                                break;
                            }
                        }
                    }

                    for (int i = 0; i < newAvailableAddQuadrants.size(); i++) {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + "SELECT quadrant_id FROM quadrants WHERE quadrant_name LIKE '" + newAvailableAddQuadrants.get(i) + "'");
                        if (rs.next()) {
                            sql = sql
                                    + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                                    + newNeighborhoodId + ","//id_nombre
                                    + rs.getString(1) + "); \n";//corredor
                        }
                    }
                } else {//se agrega sin dato si no se ha seleccionado algun cuadrante
                    sql = sql
                            + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                            + newNeighborhoodId + ","//id_barrio
                            + "0); \n";//id_corredor
                }
                //System.out.println("\n &&&&&&&&&& \n "+sql);
                connectionJdbcMB.non_query(sql);
            } catch (Exception e) {
            }




            reset();
            newRegistry();//limpiar formulario
            currentNeighborhood = null;
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Nuevo barrio almacenado.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * clean the form is initialized to add a new neighborhood.
     */
    public void newRegistry() {
        //se quita elemento seleccionado de la tabla, se inhabilitan controles
        selectedRowDataTable = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        //se limpia el formulario
        newNeighborhoodType = "1";//zona urbana
        newNeighborhoodName = "";//Nombre del barrio.
        newNeighborhoodSuburbId = "";//Código de la comuna.
        newNeighborhoodLevel = "";//Estrato socioeconómico del barrio.   
        newNeighborhoodSuburbId = "1";
        newNeighborhoodLevel = "0";
        newPopuation = "0";
        newNeighborhoodCorridor = "0";
        changeNewArea();//se determina el codigo
        changeNewQuadrantsFilter();//determinar cuadrantes

        nameGeomFile = "";
        geomText = "<div style=\"color: red;\"><b>Geometría no cargada</b></div>";//nombre del archivo de geometria para nuevo barrio
        poligonText = "";
        disabledShowGeomFile = true;
    }

    /**
     * change the type of neighborhood considering if it is rural or urban area.
     */
    public void changeArea() {//cambia tipo de barrio
        String sql;
        if (neighborhoodType.compareTo("1") == 0) {//ZONA URBANA
            sql = "SELECT * FROM communes WHERE area_id = 1 ORDER BY commune_id";
        } else {//ZONA RURAL
            sql = "SELECT * FROM communes WHERE area_id = 2 ORDER BY commune_id";
        }
        ResultSet rs = connectionJdbcMB.consult(sql);
        ArrayList<SelectItem> comList = new ArrayList<>();
        try {
            while (rs.next()) {
                comList.add(new SelectItem(rs.getInt(1), rs.getString(2)));
            }
            communesList = new SelectItem[comList.size()];
            for (int i = 0; i < comList.size(); i++) {
                communesList[i] = new SelectItem(comList.get(i).getValue(), comList.get(i).getLabel());
                neighborhoodSuburbId = comList.get(0).getValue().toString();
            }
        } catch (Exception e) {
        }
    }

    /**
     * change the new type of neighborhood is ignored unless it is rural or
     * urban area.
     */
    public void changeNewArea() {//cambia nuevo tipo de barrio
        String sql;
        if (newNeighborhoodType.compareTo("1") == 0) {//ZONA URBANA
            sql = "SELECT * FROM communes WHERE area_id = 1 ORDER BY commune_id";
        } else {//ZONA RURAL
            sql = "SELECT * FROM communes WHERE area_id = 2 ORDER BY commune_id";
        }
        ResultSet rs = connectionJdbcMB.consult(sql);
        ArrayList<SelectItem> comList = new ArrayList<>();
        try {
            while (rs.next()) {
                comList.add(new SelectItem(rs.getInt(1), rs.getString(2)));
            }
            newCommunesList = new SelectItem[comList.size()];
            for (int i = 0; i < comList.size(); i++) {
                newCommunesList[i] = new SelectItem(comList.get(i).getValue(), comList.get(i).getLabel());
                newNeighborhoodSuburbId = comList.get(0).getValue().toString();
            }
        } catch (Exception e) {
        }
        calculateCode();
    }

    /**
     * This method permit to calculate the code of a neighborhood
     */
    public void calculateCode() {
        if (newNeighborhoodType == null || newNeighborhoodType.length() == 0) {
            newNeighborhoodType = "1";
        }
        if (newNeighborhoodSuburbId == null || newNeighborhoodSuburbId.length() == 0) {
            newNeighborhoodSuburbId = "0";
        }
        boolean codeDeterminated = false;
        int max = neighborhoodsFacade.findMax(newNeighborhoodType, newNeighborhoodSuburbId);
        while (!codeDeterminated) {
            max++;
            newNeighborhoodId = String.valueOf(max);
            try {
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods WHERE neighborhood_id = " + newNeighborhoodId);
                if (!rs.next()) {
                    codeDeterminated = true;
                }
            } catch (Exception e) {
                newNeighborhoodId = "";
                codeDeterminated = true;
            }
        }
    }

    /**
     * This method is used to change a new code.
     */
    public void changeNewCode() {
        try {
            int c = Integer.parseInt(newNeighborhoodId);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "El código debe ser un numero positivo");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                newNeighborhoodId = "";
            }
        } catch (Exception e) {
            if (newNeighborhoodId.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El código debe ser un valor numerico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            newNeighborhoodId = "";
        }
    }

    /**
     * change the value to a new population.
     */
    public void changeNewPopulation() {
        try {
            int c = Integer.parseInt(newPopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                newPopuation = "0";
            }
        } catch (Exception e) {
            if (newPopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            newPopuation = "0";
        }
    }

    /**
     * changes the value of the population.
     */
    public void changePopulation() {
        try {
            int c = Integer.parseInt(popuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                popuation = "0";
            }
        } catch (Exception e) {
            if (popuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            popuation = "0";
        }
    }

    /**
     * change the code used and the number must be positive.
     */
    public void changeCode() {
        try {
            int c = Integer.parseInt(neighborhoodId);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El código debe ser un numero positivo");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                neighborhoodId = "";
            }

        } catch (Exception e) {
            if (neighborhoodId.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El código debe ser un valor numerico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            neighborhoodId = "";
        }
    }

    /**
     * This method is used to filters the quadrants available to change a new
     * one
     */
    public void changeNewQuadrantsFilter() {
        newAvailableQuadrants = new ArrayList<>();
        newSelectedAvailableQuadrants = new ArrayList<>();
        boolean foundQuadrant;
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM quadrants ORDER BY quadrant_id ");
            while (rs.next()) {
                if (newQuadrantsFilter != null && newQuadrantsFilter.trim().length() != 0) {//hay cadena a buscar
                    if (rs.getString("quadrant_name").toUpperCase().indexOf(newQuadrantsFilter.toUpperCase()) != -1) {
                        foundQuadrant = false;
                        for (int i = 0; i < newAvailableAddQuadrants.size(); i++) {
                            if (rs.getString("quadrant_name").compareTo(newAvailableAddQuadrants.get(i)) == 0) {
                                foundQuadrant = true;
                            }
                        }
                        if (!foundQuadrant) {
                            newAvailableQuadrants.add(rs.getString("quadrant_name"));
                        }
                    }
                } else {//no hay cadena a buscar
                    foundQuadrant = false;
                    for (int i = 0; i < newAvailableAddQuadrants.size(); i++) {
                        if (rs.getString("quadrant_name").compareTo(newAvailableAddQuadrants.get(i)) == 0) {
                            foundQuadrant = true;
                        }
                    }
                    if (!foundQuadrant) {
                        newAvailableQuadrants.add(rs.getString("quadrant_name"));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * This method is used to filter the quadrants available to change.
     */
    public void changeQuadrantsFilter() {
        availableQuadrants = new ArrayList<>();
        selectedAvailableQuadrants = new ArrayList<>();
        boolean foundQuadrant;
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT * FROM quadrants ORDER BY quadrant_id ");
            while (rs.next()) {
                if (quadrantsFilter != null && quadrantsFilter.trim().length() != 0) {//hay cadena a buscar
                    if (rs.getString("quadrant_name").toUpperCase().indexOf(quadrantsFilter.toUpperCase()) != -1) {
                        foundQuadrant = false;
                        for (int i = 0; i < availableAddQuadrants.size(); i++) {
                            if (rs.getString("quadrant_name").compareTo(availableAddQuadrants.get(i)) == 0) {
                                foundQuadrant = true;
                            }
                        }
                        if (!foundQuadrant) {
                            availableQuadrants.add(rs.getString("quadrant_name"));
                        }
                    }
                } else {//bo hay cadena a buscar
                    foundQuadrant = false;
                    for (int i = 0; i < availableAddQuadrants.size(); i++) {
                        if (rs.getString("quadrant_name").compareTo(availableAddQuadrants.get(i)) == 0) {
                            foundQuadrant = true;
                        }
                    }
                    if (!foundQuadrant) {
                        availableQuadrants.add(rs.getString("quadrant_name"));
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
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<>();
            try {
                ResultSet rs;
                if (currentSearchCriteria == 2) {
                    rs = connectionJdbcMB.consult("select * from neighborhoods where neighborhood_name like '%" + currentSearchValue + "%'");
                } else {
                    rs = connectionJdbcMB.consult("select * from neighborhoods where neighborhood_id::text like '%" + currentSearchValue + "%'");
                }
                while (rs.next()) {
                    if (rs.getString("neighborhood_area") != null) {
                        if (rs.getInt("neighborhood_area") == 1) {
                            type = "ZONA URBANA";
                        } else {
                            type = "ZONA RURAL";
                        }
                    } else {
                        type = "";
                    }
                    rowDataTableList.add(new RowDataTable(rs.getString("neighborhood_id"), rs.getString("neighborhood_name"), type));
                }
            } catch (SQLException ex) {
            }
            if (rowDataTableList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    /**
     * This method is used to reset values of the variables.
     */
    public void reset() {
        rowDataTableList = new ArrayList<>();
        neighborhoodsList = neighborhoodsFacade.findAll();
        newPopuation = "0";
        newNeighborhoodCorridor = "0";

        poligonText = "";
        disabledShowGeomFile = true;
        nameGeomFile = "Archivo no cargado";//nombre del archivo de geometria para nuevo barrio
        geomText = "Geometría no seleccionada";//nombre del archivo de geometria para nuevo barrio
        nameGeomFile = "";//nombre del archivo de geometria para barrio existente



        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodArea() == 1) {
                type = "ZONA URBANA";
            } else {
                type = "ZONA RURAL";
            }
            rowDataTableList.add(
                    new RowDataTable(
                    neighborhoodsList.get(i).getNeighborhoodId().toString(),
                    neighborhoodsList.get(i).getNeighborhoodName(),
                    type));
        }

        //cargo los corredores
        List<Corridors> corridorsAll = corridorsFacade.findAll();
        if (corridorsAll == null || corridorsAll.isEmpty()) {
            corridorsList = new SelectItem[1];
            corridorsList[0] = new SelectItem(0, "SIN DATO");
        } else {
            corridorsList = new SelectItem[corridorsAll.size()];
            for (int i = 0; i < corridorsAll.size(); i++) {
                corridorsList[i] = new SelectItem(corridorsAll.get(i).getCorridorId(), corridorsAll.get(i).getCorridorName());
            }
        }
        calculateCode();
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

    public Neighborhoods getCurrentNeighborhood() {
        return currentNeighborhood;
    }

    public void setCurrentNeighborhood(Neighborhoods currentNeighborhood) {
        this.currentNeighborhood = currentNeighborhood;
    }

    public String getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(String neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public String getNeighborhoodLevel() {
        return neighborhoodLevel;
    }

    public void setNeighborhoodLevel(String neighborhoodLevel) {
        this.neighborhoodLevel = neighborhoodLevel;
    }

    public String getNeighborhoodName() {
        return neighborhoodName;
    }

    public void setNeighborhoodName(String neighborhoodName) {
        this.neighborhoodName = neighborhoodName;
    }

    public String getNeighborhoodSuburbId() {
        return neighborhoodSuburbId;
    }

    public void setNeighborhoodSuburbId(String neighborhoodSuburbId) {
        this.neighborhoodSuburbId = neighborhoodSuburbId;
    }

    public String getNewNeighborhoodId() {
        return newNeighborhoodId;
    }

    public void setNewNeighborhoodId(String newNeighborhoodId) {
        this.newNeighborhoodId = newNeighborhoodId;
    }

    public String getNewNeighborhoodLevel() {
        return newNeighborhoodLevel;
    }

    public void setNewNeighborhoodLevel(String newNeighborhoodLevel) {
        this.newNeighborhoodLevel = newNeighborhoodLevel;
    }

    public String getNewNeighborhoodName() {
        return newNeighborhoodName;
    }

    public void setNewNeighborhoodName(String newNeighborhoodName) {
        this.newNeighborhoodName = newNeighborhoodName;
    }

    public String getNewNeighborhoodSuburbId() {
        return newNeighborhoodSuburbId;
    }

    public void setNewNeighborhoodSuburbId(String newNeighborhoodSuburbId) {
        this.newNeighborhoodSuburbId = newNeighborhoodSuburbId;
    }

    public String getNewNeighborhoodType() {
        return newNeighborhoodType;
    }

    public void setNewNeighborhoodType(String newNeighborhoodType) {
        this.newNeighborhoodType = newNeighborhoodType;
    }

    public String getNeighborhoodType() {
        return neighborhoodType;
    }

    public void setNeighborhoodType(String neighborhoodType) {
        this.neighborhoodType = neighborhoodType;
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

    public SelectItem[] getCommunesList() {
        return communesList;
    }

    public void setCommunesList(SelectItem[] communesList) {
        this.communesList = communesList;
    }

    public SelectItem[] getNewCommunesList() {
        return newCommunesList;
    }

    public void setNewCommunesList(SelectItem[] newCommunesList) {
        this.newCommunesList = newCommunesList;
    }

    public SelectItem[] getCorridorsList() {
        return corridorsList;
    }

    public void setCorridorsList(SelectItem[] corridorsList) {
        this.corridorsList = corridorsList;
    }

    public List<String> getAvailableQuadrants() {
        return availableQuadrants;
    }

    public void setAvailableQuadrants(List<String> availableQuadrants) {
        this.availableQuadrants = availableQuadrants;
    }

    public List<String> getSelectedAvailableQuadrants() {
        return selectedAvailableQuadrants;
    }

    public void setSelectedAvailableQuadrants(List<String> selectedAvailableQuadrants) {
        this.selectedAvailableQuadrants = selectedAvailableQuadrants;
    }

    public List<String> getAvailableAddQuadrants() {
        return availableAddQuadrants;
    }

    public void setAvailableAddQuadrants(List<String> availableAddQuadrants) {
        this.availableAddQuadrants = availableAddQuadrants;
    }

    public List<String> getSelectedAvailableAddQuadrants() {
        return selectedAvailableAddQuadrants;
    }

    public void setSelectedAvailableAddQuadrants(List<String> selectedAvailableAddQuadrants) {
        this.selectedAvailableAddQuadrants = selectedAvailableAddQuadrants;
    }

    public List<String> getNewAvailableQuadrants() {
        return newAvailableQuadrants;
    }

    public void setNewAvailableQuadrants(List<String> newAvailableQuadrants) {
        this.newAvailableQuadrants = newAvailableQuadrants;
    }

    public List<String> getNewSelectedAvailableQuadrants() {
        return newSelectedAvailableQuadrants;
    }

    public void setNewSelectedAvailableQuadrants(List<String> newSelectedAvailableQuadrants) {
        this.newSelectedAvailableQuadrants = newSelectedAvailableQuadrants;
    }

    public List<String> getNewAvailableAddQuadrants() {
        return newAvailableAddQuadrants;
    }

    public void setNewAvailableAddQuadrants(List<String> newAvailableAddQuadrants) {
        this.newAvailableAddQuadrants = newAvailableAddQuadrants;
    }

    public List<String> getNewSelectedAvailableAddQuadrants() {
        return newSelectedAvailableAddQuadrants;
    }

    public void setNewSelectedAvailableAddQuadrants(List<String> newSelectedAvailableAddQuadrants) {
        this.newSelectedAvailableAddQuadrants = newSelectedAvailableAddQuadrants;
    }

    public String getNewPopuation() {
        return newPopuation;
    }

    public void setNewPopuation(String newPopuation) {
        this.newPopuation = newPopuation;
    }

    public String getNewNeighborhoodCorridor() {
        return newNeighborhoodCorridor;
    }

    public void setNewNeighborhoodCorridor(String newNeighborhoodCorridor) {
        this.newNeighborhoodCorridor = newNeighborhoodCorridor;
    }

    public String getNeighborhoodCorridor() {
        return neighborhoodCorridor;
    }

    public void setNeighborhoodCorridor(String neighborhoodCorridor) {
        this.neighborhoodCorridor = neighborhoodCorridor;
    }

    public String getPopuation() {
        return popuation;
    }

    public void setPopuation(String popuation) {
        this.popuation = popuation;
    }

    public String getNewQuadrantsFilter() {
        return newQuadrantsFilter;
    }

    public void setNewQuadrantsFilter(String newQuadrantsFilter) {
        this.newQuadrantsFilter = newQuadrantsFilter;
    }

    public String getQuadrantsFilter() {
        return quadrantsFilter;
    }

    public void setQuadrantsFilter(String quadrantsFilter) {
        this.quadrantsFilter = quadrantsFilter;
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
