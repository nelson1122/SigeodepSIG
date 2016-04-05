/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import managedBeans.login.ApplicationControlMB;

/**
 * This class allows you to manage everything related to the backs of all the
 * information contained SIGEODEP also has functions that allow to have backups
 * in case of loss or any kind of error.
 *
 * @author santos
 */
@ManagedBean(name = "backupsMB")
@SessionScoped
public class BackupsMB {

    private ConnectionJdbcMB connectionJdbcMB;
    private ApplicationControlMB applicationControlMB;
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private List<RowDataTable> rowDataTableListDwh;
    private RowDataTable selectedRowDataTableDwh;
    private String newName = "";//Nombre del la copia de seguridad.
    private String newNameDwh = "";//Nombre del la copia de seguridad.
    private String realPath = "";

    /**
     * It is responsible to connect to the database and specify the directory to
     * use.
     */
    public BackupsMB() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        realPath = (String) servletContext.getRealPath("/"); // Sustituye "/" por el directorio ej: "/upload"
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        applicationControlMB = (ApplicationControlMB) context.getApplicationMap().get("applicationControlMB");
    }

    /**
     * is responsible to clear the fields for creating a new Backup.
     */
    public void reset() {
        removeNotFoundBackups();
        rowDataTableList = new ArrayList<>();
        selectedRowDataTable = null;
        newName = "";
        rowDataTableListDwh = new ArrayList<>();
        selectedRowDataTableDwh = null;
        newNameDwh = "";
        createDynamicTable();
        createDynamicTableDwh();
    }

    /**
     * Delete backups that do not have the file stored in the server folder.
     */
    public void removeNotFoundBackups() {
        /*
         * elimina las copias de seguridad que no tengan el archivo 
         * almacenado en la carpeta del servidor
         */
        File backupFile;
        ArrayList<String> idToRemove = new ArrayList<>();
        try {
            //elimnacion backups sigeodep
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM backups");
            while (rs.next()) {
                backupFile = new File(rs.getString("path_file") + rs.getString("name_backup") + "_od.backup");
                if (!backupFile.exists()) {//si no existe se elimina de tabla backups
                    idToRemove.add(rs.getString("id_backup"));
                }
            }
            if (!idToRemove.isEmpty()) {
                for (int i = 0; i < idToRemove.size(); i++) {
                    connectionJdbcMB.non_query("DELETE FROM backups WHERE id_backup = " + idToRemove.get(i));
                }
            }
            //elimnacion backups bodega
            rs = connectionJdbcMB.consult("SELECT * FROM backups_dwh");
            while (rs.next()) {
                backupFile = new File(rs.getString("path_file") + rs.getString("name_backup") + "_od_dwh.backup");
                if (!backupFile.exists()) {//si no existe se elimina de tabla backups
                    idToRemove.add(rs.getString("id_backup"));
                }
            }
            if (!idToRemove.isEmpty()) {
                for (int i = 0; i < idToRemove.size(); i++) {
                    connectionJdbcMB.non_query("DELETE FROM backups_dwh WHERE id_backup = " + idToRemove.get(i));
                }
            }
        } catch (Exception x) {
        }
    }

    /**
     * Create the backup od (Sigeodep) also determines whether you've entered a
     * name and if it exists and stores the information to create the copy.
     */
    public void createBackupClick() {
        /*
         * click sobre crear backup de od(sigeodep)
         */
        boolean continueProcess;
        ResultSet rs;

        if (newName != null && newName.trim().length() != 0) {//determinar si se ingreso nombre
            continueProcess = true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe escribir un nombre para la copia de seguridad"));
            continueProcess = false;
        }

        if (continueProcess) {//determinar si el nombre ya esta ingresado
            try {
                rs = connectionJdbcMB.consult("SELECT * FROM backups WHERE name_backup ILIKE '" + newName.trim() + "'");
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una copia de seguridad con el nombre ingresado"));
                    continueProcess = false;
                }
                rs = connectionJdbcMB.consult("SELECT * FROM backups_dwh WHERE name_backup ILIKE '" + newName.trim() + "'");
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una copia de seguridad con el nombre ingresado"));
                    continueProcess = false;
                }
            } catch (Exception x) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
                continueProcess = false;
            }
        }

        if (continueProcess) {//almaceno la informacion de la copia de seguridad a crear
            try {
                if (new java.io.File(realPath + "backups/").exists()) {//verificar que el directorio exista                    
                    rs = connectionJdbcMB.consult("SELECT MAX(id_backup) FROM backups");
                    if (rs.next()) {
                        int max = rs.getInt(1);
                        if (max < 11) {
                            max = 11;
                        } else {
                            max++;
                        }
                        TimeZone zonah = java.util.TimeZone.getTimeZone("GMT+1");
                        Calendar Calendario = java.util.GregorianCalendar.getInstance(zonah, new java.util.Locale("es"));
                        SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateStr = df.format(Calendario.getTime());
                        connectionJdbcMB.non_query(" INSERT INTO backups VALUES (" + String.valueOf(max) + ",'" + newName + "','" + dateStr + "','MANUAL','" + realPath + "backups/" + "')");
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Directorio 'backups' no existe en el servidor"));
                    continueProcess = false;
                }
            } catch (Exception x) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
            }
        }

        if (continueProcess) {//realizo los archivos de copia de seguridad
            try {
                Process p;
                ProcessBuilder pb;

                String backupFilePath = realPath + "backups/" + newName;
                File fiRcherofile = new java.io.File(backupFilePath + "_od.backup");//si archivo od existe Lo eliminamos 
                if (fiRcherofile.exists()) {
                    fiRcherofile.delete();
                }
                fiRcherofile = new java.io.File(backupFilePath + "_od_dwh.backup");//si archivo od_dwh existe Lo eliminamos 
                if (fiRcherofile.exists()) {
                    fiRcherofile.delete();
                }

                //copia de seguridad de od(sigeodep)
                pb = new ProcessBuilder("pg_dump", "-i", "-h", connectionJdbcMB.getServer(), "-p", "5432", "-U", connectionJdbcMB.getUser(), "-F", "c", "-b", "-v", "-f", backupFilePath + "_od.backup", connectionJdbcMB.getDb());
                pb.environment().put("PGPASSWORD", connectionJdbcMB.getPassword());
                pb.redirectErrorStream(true);
                p = pb.start();
                printOutputFromProcces(p, " crear copia de seguridad: " + backupFilePath + "_od.backup");

                //copia de seguridad de od_dwh(bodega)
                pb = new ProcessBuilder("pg_dump", "-i", "-h", connectionJdbcMB.getServer(), "-p", "5432", "-U", connectionJdbcMB.getUser(), "-F", "c", "-b", "-v", "-f", backupFilePath + "_od_dwh.backup", connectionJdbcMB.getDb_dwh());
                pb.environment().put("PGPASSWORD", connectionJdbcMB.getPassword());
                pb.redirectErrorStream(true);
                p = pb.start();
                printOutputFromProcces(p, " crear copia de seguridad: " + backupFilePath + "_od_dwh.backup");

                newName = "";
                createDynamicTable();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La copia de seguridad ha sido creada correctamente"));

            } catch (IOException x) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
            }
        }

    }

    /**
     * Create the backup od (Bodega) also determines whether you've entered a
     * name and if it exists and stores the information to create the copy.
     */
    public void createBackupClickDwh() {
        /*
         * click sobre crear copia de seguridad de od_dwh(bodega)
         */
        boolean continueProcess;
        ResultSet rs;

        if (newNameDwh != null && newNameDwh.trim().length() != 0) {//determinar si se digito nombre            
            continueProcess = true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe escribir un nombre para la copia de seguridad"));
            continueProcess = false;
        }

        if (continueProcess) {//determinar si el nombre ya esta registrado            
            try {
                rs = connectionJdbcMB.consult("SELECT * FROM backups_dwh WHERE name_backup ILIKE '" + newNameDwh.trim() + "'");
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una copia de seguridad con el nombre ingresado"));
                    continueProcess = false;
                }
                rs = connectionJdbcMB.consult("SELECT * FROM backups WHERE name_backup ILIKE '" + newName.trim() + "'");
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una copia de seguridad con el nombre ingresado"));
                    continueProcess = false;
                }
            } catch (Exception x) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
                continueProcess = false;
            }
        }

        if (continueProcess) {//creacion de archivos
            try {
                Process p;
                ProcessBuilder pb;
                if (new java.io.File(realPath + "backups/").exists()) {//verificar que el directorio exista                    
                    File backupFile = new java.io.File(realPath + "backups/" + newNameDwh + "_od.backup");
                    if (backupFile.exists()) {//eliminar si existe
                        backupFile.delete();
                    }
                    backupFile = new java.io.File(realPath + "backups/" + newNameDwh + "_od_dwh.backup");
                    if (backupFile.exists()) {//eliminar si existe
                        backupFile.delete();
                    }
                    backupFile = new java.io.File(realPath + "backups/" + newNameDwh + "_file.backup");
                    if (backupFile.exists()) {//eliminar si existe
                        backupFile.delete();
                    }
                    //copia de seguridad de tablas sta de od(sigeodep)
                    pb = new ProcessBuilder(
                            "pg_dump",
                            "-i",
                            "-h", connectionJdbcMB.getServer(),
                            "-p", "5432",
                            "-U", connectionJdbcMB.getUser(),
                            "-t", "*_sta",
                            "-F", "c", "-b", "-v", "-f",
                            realPath + "backups/" + newNameDwh + "_od.backup",
                            connectionJdbcMB.getDb());
                    pb.environment().put("PGPASSWORD", connectionJdbcMB.getPassword());
                    pb.redirectErrorStream(true);
                    p = pb.start();
                    printOutputFromProcces(p, " creacion copia seguridad: " + realPath + "backups/" + newNameDwh + "_od_dwh.backup");
                    //copia de seguridad de od_dwh(bodega)
                    pb = new ProcessBuilder(
                            "pg_dump",
                            "-i",
                            "-h", connectionJdbcMB.getServer(),
                            "-p", "5432",
                            "-U", connectionJdbcMB.getUser(),
                            "-F", "c", "-b", "-v", "-f",
                            realPath + "backups/" + newNameDwh + "_od_dwh.backup",
                            connectionJdbcMB.getDb_dwh());
                    pb.environment().put("PGPASSWORD", connectionJdbcMB.getPassword());
                    pb.redirectErrorStream(true);
                    p = pb.start();
                    printOutputFromProcces(p, " creacion copia seguridad: " + realPath + "backups/" + newNameDwh + "_od_dwh.backup");

                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encuentra la carpeta" + realPath + "backups/"));
                    continueProcess = false;
                }
            } catch (IOException x) {
                continueProcess = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
            }
        }

        if (continueProcess) {//almaceno la informacion de la copia de seguridad creada
            try {
                rs = connectionJdbcMB.consult("SELECT MAX(id_backup) FROM backups_dwh");
                if (rs.next()) {
                    int max = rs.getInt(1);
                    if (max < 11) {
                        max = 11;
                    } else {
                        max++;
                    }
                    TimeZone zonah = java.util.TimeZone.getTimeZone("GMT+1");
                    Calendar Calendario = java.util.GregorianCalendar.getInstance(zonah, new java.util.Locale("es"));
                    SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateStr = df.format(Calendario.getTime());
                    connectionJdbcMB.non_query(" INSERT INTO backups_dwh VALUES (" + String.valueOf(max) + ",'" + newNameDwh + "','" + dateStr + "','" + realPath + "backups/" + "')");
                }

                //archivo para actualizar campo closure_date de la tabla injuries
                FileWriter fw = new FileWriter(realPath + "backups/" + newNameDwh + "_file.backup");
                BufferedWriter bw = new BufferedWriter(fw);
                try (PrintWriter outFile = new PrintWriter(bw)) {
                    outFile.println("DELETE FROM fatal_injury_accident_sta;");
                    outFile.println("DELETE FROM fatal_injury_murder_sta;");
                    outFile.println("DELETE FROM fatal_injury_suicide_sta;");
                    outFile.println("DELETE FROM fatal_injury_traffic_sta;");
                    outFile.println("DELETE FROM non_fatal_domestic_violence_sta;");
                    outFile.println("DELETE FROM non_fatal_interpersonal_sta;");
                    outFile.println("DELETE FROM non_fatal_non_intentional_sta;");
                    outFile.println("DELETE FROM non_fatal_self_inflicted_sta;");
                    outFile.println("DELETE FROM non_fatal_transport_sta;");
                    outFile.println("DELETE FROM sivigila_sta;");
                    outFile.println("DELETE FROM backups_dwh;");
                    rs = connectionJdbcMB.consult("SELECT * FROM injuries");
                    while (rs.next()) {
                        outFile.println("UPDATE injuries set closure_date = '" + rs.getString("closure_date") + "' WHERE injury_id=" + rs.getString("injury_id") + ";");
                    }
                    rs = connectionJdbcMB.consult("SELECT * FROM backups_dwh");
                    while (rs.next()) {
                        outFile.println("INSERT INTO backups_dwh VALUES (" + rs.getString(1) + ",'" + rs.getString(2) + "','" + rs.getString(3) + "','" + rs.getString(4) + "')");
                    }
                    outFile.close();
                } catch (Exception ex) {
                    //continueProcess = false;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
                }
                newNameDwh = "";
                createDynamicTableDwh();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La copia de seguridad y Cierre se han realizado correctamente"));
            } catch (SQLException | IOException x) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
            }
        }
    }

    /**
     * Show console the progress of an invoked external process.
     *
     * @param p
     * @param description
     */
    private void printOutputFromProcces(Process p, String description) {
        /*
         * mostrar por consola el progreso de un proceso externo invocado
         */
        //System.out.println("\nInicia proceso " + description + " /////////////////////////////////////////");
        try {
            //CODIGO PARA MOSTRAR EL PROGESO DE LA GENERACION DEL ARCHIVO
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String ll;
            while ((ll = br.readLine()) != null) {
                System.out.println(ll);
            }
        } catch (IOException e) {
            System.out.println("Error 99 " + e.getMessage());
        }
        //System.out.println("Termina proceso " + description + " /////////////////////////////////////////");
    }

    /**
     * Restore a backup of the crime observatory (Sigeodep).
     */
    public void restoreBackupClick() {
        /*
         * click sobre restaurar una copia de seguridad de od(sigeodep)
         */
        boolean continueProcess;

        if (selectedRowDataTable != null) {
            continueProcess = true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una copia de seguridad para realizar la restauración"));
            continueProcess = false;
        }
        if (continueProcess) {//valido que el archivo exista        
            if (!new java.io.File(selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od.backup").exists()) {//Probamos a ver si existe ese ultimo dato                    
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontro el archivo: " + selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od.backup" + " en el servidor"));
                continueProcess = false;
            }
            if (!new java.io.File(selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od_dwh.backup").exists()) {//Probamos a ver si existe ese ultimo dato                    
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontro el archivo: " + selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od_dwh.backup" + " en el servidor"));
                continueProcess = false;
            }
        }

        if (continueProcess) {//realizo la restauracion de la copia de seguridad            
            try {
                Process p;
                ProcessBuilder pb;
                //restauracion de od(sigeodep) ---------------------------------
                pb = new ProcessBuilder("pg_restore", "-i", "-h", connectionJdbcMB.getServer(), "-p", "5432", "-U", connectionJdbcMB.getUser(), "-d", connectionJdbcMB.getDb(), "-v", "-F", "c", "-c", selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od.backup");
                pb.environment().put("PGPASSWORD", connectionJdbcMB.getPassword());
                pb.redirectErrorStream(true);
                p = pb.start();
                printOutputFromProcces(p, " restauracion copia seguridad: " + selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od.backup");

                //restauracion de od_dwh(bodega) ---------------------------------
                pb = new ProcessBuilder("pg_restore", "-i", "-h", connectionJdbcMB.getServer(), "-p", "5432", "-U", connectionJdbcMB.getUser(), "-d", connectionJdbcMB.getDb_dwh(), "-v", "-F", "c", "-c", selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od_dwh.backup");
                pb.environment().put("PGPASSWORD", connectionJdbcMB.getPassword());
                pb.redirectErrorStream(true);
                p = pb.start();
                printOutputFromProcces(p, " restauracion copia seguridad: " + selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od_dwh.backup");
            } catch (IOException x) {
                System.err.println("Caught: " + x.getMessage());
                continueProcess = false;
            }
        }
        if (continueProcess) {
            applicationControlMB.closeAllSessions();//finalizo todas las sessiones            
            try {//me redirijo a la pagina inicial
                ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
                String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
                ((HttpSession) ctx.getSession(false)).invalidate();//System.out.println("se redirecciona");
                ctx.redirect(ctxPath + "/index2.html");
            } catch (Exception ex) {
                System.out.println("Excepcion cuando usuario cierra sesion sesion: " + ex.toString());
            }
        }
    }

    /**
     * Restore the latest backup of the Warehouse.
     */
    public void restoreLastBackupDwh() {
        /*
         * restauración de la última copia de seguridad de la bodega
         */
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT \n"
                    + "   *\n"
                    + "FROM "
                    + "   backups_dwh \n"
                    + "WHERE \n"
                    + "   date_backup = (SELECT MIN(date_backup) \n"
                    + "                   FROM backups_dwh)");
            if (rs.next()) {
                RowDataTable newRowDataTable = new RowDataTable(rs.getString("id_backup"), rs.getString("name_backup"), rs.getString("date_backup"), rs.getString("path_file"));
                setSelectedRowDataTableDwh(newRowDataTable);
                restoreBackupClickDwh();
                setSelectedRowDataTableDwh(null);
            } else {
                //no se existe ultima copia de seguridad de bodega
            }
        } catch (SQLException ex) {
        }
    }

    /**
     * Allows restores the backup od_dwh (Bodega), also performs validation is
     * selected a backup of the table.
     */
    public void restoreBackupClickDwh() {
        /*
         * click sobre restaurar una copia de seguridad de od_dwh(bodega)
         */
        boolean continueProcess;
        java.io.File ficherofile;

        if (selectedRowDataTableDwh != null) {//validar que se aya seleccionado una copia de seguridad de la tabla
            continueProcess = true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una copia de seguridad para realizar la restauración"));
            continueProcess = false;
        }

        if (continueProcess) {//valido los archivos existan
            //archivo de restauracion de injuries
            if (!new java.io.File(selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_file.backup").exists()) {//valido que el archivo exista
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontro el archivo: " + selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_file.backup" + " en el servidor"));
                continueProcess = false;
            }
            //archivo de od
            if (!new java.io.File(selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od.backup").exists()) {//valido que el archivo exista
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontro el archivo: " + selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od.backup" + " en el servidor"));
                continueProcess = false;
            }
            //archivo de bodega
            if (!new java.io.File(selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od_dwh.backup").exists()) {//valido que el archivo exista
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontro el archivo: " + selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od_dwh.backup" + " en el servidor"));
                continueProcess = false;
            }
        }

        if (continueProcess) {
            Process p;
            ProcessBuilder pb;
            try {
                //restauracion de campo closure_date de tabla injuries 
                ficherofile = new java.io.File(selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_file.backup");
                FileReader fr = new FileReader(ficherofile);
                BufferedReader br = new BufferedReader(fr);
                String linea;
                while ((linea = br.readLine()) != null) {
                    connectionJdbcMB.non_query(linea);
                }
                //restauracion de od(sigeodep)
                pb = new ProcessBuilder("pg_restore", "-i", "-h", connectionJdbcMB.getServer(), "-p", "5432", "-U", connectionJdbcMB.getUser(), "-d", connectionJdbcMB.getDb(), "-v", "-F", "c", "-c", selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od.backup");
                pb.environment().put("PGPASSWORD", connectionJdbcMB.getPassword());
                pb.redirectErrorStream(true);
                p = pb.start();
                printOutputFromProcces(p, " restauracion copia seguridad " + selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od.backup");
                //restauracion de od_dwh(bodega)
                pb = new ProcessBuilder("pg_restore", "-i", "-h", connectionJdbcMB.getServer(), "-p", "5432", "-U", connectionJdbcMB.getUser(), "-d", connectionJdbcMB.getDb_dwh(), "-v", "-F", "c", "-c", selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od_dwh.backup");
                pb.environment().put("PGPASSWORD", connectionJdbcMB.getPassword());
                pb.redirectErrorStream(true);
                p = pb.start();
                printOutputFromProcces(p, " restauracion copia seguridad " + selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od_dwh.backup");
            } catch (IOException x) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
            }
        }

        if (continueProcess) {
            createDynamicTableDwh();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La copia de seguridad se ha restaurado correctamente"));
        }
    }

    /**
     * Deletes a backup Crime Observatory (Sigeodep)
     */
    public void deleteBackupClick() {
        /*
         * click sobre eliminar un backup de od(sigeodep)
         */
        File backupFile;
        if (selectedRowDataTable != null) {
            backupFile = new java.io.File(selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od.backup");
            if (backupFile.exists()) {
                backupFile.delete();//elimino el archivo
            }
            backupFile = new java.io.File(selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + "_od_dwh.backup");
            if (backupFile.exists()) {
                backupFile.delete();//elimino el archivo
            }
            connectionJdbcMB.non_query("DELETE FROM backups WHERE id_backup = " + selectedRowDataTable.getColumn1());
            createDynamicTable();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La copia de seguridad se ha eliminado correctamente"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una copia de seguridad para realizar la eliminación"));
        }
    }

    /**
     * Deletes a backup Crime Observatory (Warehouse).
     */
    public void deleteBackupClickDwh() {
        /*
         * click sobre eliminar un backup de od_dwh(bodega)
         */
        File backupFile;
        if (selectedRowDataTableDwh != null) {
            backupFile = new java.io.File(selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od.backup");
            if (backupFile.exists()) {
                backupFile.delete();//elimino el archivo
            }
            backupFile = new java.io.File(selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_od_dwh.backup");
            if (backupFile.exists()) {
                backupFile.delete();//elimino el archivo
            }
            backupFile = new java.io.File(selectedRowDataTableDwh.getColumn4() + selectedRowDataTableDwh.getColumn2() + "_file.backup");
            if (backupFile.exists()) {
                backupFile.delete();//elimino el archivo
            }
            connectionJdbcMB.non_query("DELETE FROM backups_dwh WHERE id_backup = " + selectedRowDataTableDwh.getColumn1());
            createDynamicTableDwh();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La copia de seguridad se ha eliminado correctamente"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una copia de seguridad para realizar la eliminación"));
        }
    }

    /**
     * Create a table with listings backups od (sigeodep).
     */
    private void createDynamicTable() {
        /*
         * creacion de la tabla con el listado de backups de od(sigeodep)
         */
        ResultSet rs = connectionJdbcMB.consult("SELECT * FROM backups ORDER BY id_backup");
        try {
            rowDataTableList = new ArrayList<>();
            while (rs.next()) {
                rowDataTableList.add(new RowDataTable(
                        rs.getString("id_backup"),
                        rs.getString("name_backup"),
                        rs.getString("date_backup"),
                        rs.getString("type_backup"),
                        rs.getString("path_file")));

            }
        } catch (Exception e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.getMessage());
        }
    }

    /**
     * Create a table with listings od_dwh backups (Warehouse).
     */
    private void createDynamicTableDwh() {
        /*
         * creacion de la tabla con el listado de backups de od_dwh(bodega)
         */
        ResultSet rs = connectionJdbcMB.consult("SELECT * FROM backups_dwh ORDER BY id_backup");
        try {
            rowDataTableListDwh = new ArrayList<>();
            while (rs.next()) {
                rowDataTableListDwh.add(new RowDataTable(
                        rs.getString("id_backup"),
                        rs.getString("name_backup"),
                        rs.getString("date_backup"),
                        rs.getString("path_file")));
            }
        } catch (Exception e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.getMessage());
        }
    }

    // --------------------------    
    // -- METODOS GET Y SET -----
    // --------------------------    
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

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public List<RowDataTable> getRowDataTableListDwh() {
        return rowDataTableListDwh;
    }

    public void setRowDataTableListDwh(List<RowDataTable> rowDataTableListDwh) {
        this.rowDataTableListDwh = rowDataTableListDwh;
    }

    public RowDataTable getSelectedRowDataTableDwh() {
        return selectedRowDataTableDwh;
    }

    public void setSelectedRowDataTableDwh(RowDataTable selectedRowDataTableDwh) {
        this.selectedRowDataTableDwh = selectedRowDataTableDwh;
    }

    public String getNewNameDwh() {
        return newNameDwh;
    }

    public void setNewNameDwh(String newNameDwh) {
        this.newNameDwh = newNameDwh;
    }
}
