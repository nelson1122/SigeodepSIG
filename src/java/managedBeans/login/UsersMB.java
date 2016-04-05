/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import beans.util.StringEncryption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.UsersFacade;
import model.pojo.Users;

/**
 *
 * @author SANTOS
 */
/**
 * This class is responsible to controll the user permissions, in addition to
 * create a connection to the database.
 *
 */
@ManagedBean(name = "usersMB")
@SessionScoped
public class UsersMB {

    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    UsersFacade usersFacade;
    private List<Users> usersList;
    private String stateUser = "Activa";
    private String newStateUser = "Activa";
    private Users currentUser;
    private String name = "";
    private String newName = "";
    private String job = "";
    private String newJob = "";
    private String institution = "";
    private String newInstitution = "";
    private String telephone = "";
    private String newtelephone = "";
    private String email = "";
    private String newEmail = "";
    private String password = "";
    private String passwordTmp = "";//pasword temporal
    private String newPasword = "";
    private String confirmPassword = "";
    private String newConfirmPasword = "";
    private String address = "";
    private String newAddress = "";
    private String login = "";
    private String newLogin = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private boolean permission1 = true;
    private boolean permission2 = true;
    private boolean permission3 = true;
    private boolean permission4 = true;
    private boolean permission5 = true;
    private boolean newPermission1 = true;
    private boolean newPermission2 = true;
    private boolean newPermission3 = true;
    private boolean newPermission4 = true;
    private boolean newPermission5 = true;
    private ConnectionJdbcMB connectionJdbcMB;
    StringEncryption stringEncryption = new StringEncryption();

    /**
     * This method is responsible to Create a connection to the database.
     */
    public UsersMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    /**
     * This method loads all the values of the users that are registered in the
     * system.
     */
    public void load() {
        currentUser = null;
        if (selectedRowDataTable != null) {
            currentUser = usersFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
        if (currentUser != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;

            if (currentUser.getUserName() != null) {
                name = currentUser.getUserName();
            } else {
                name = "";
            }
            if (currentUser.getUserJob() != null) {
                job = currentUser.getUserJob();
            } else {
                job = "";
            }
            if (currentUser.getUserInstitution() != null) {
                institution = currentUser.getUserInstitution();
            } else {
                institution = "";
            }
            if (currentUser.getUserTelephone() != null) {
                telephone = currentUser.getUserTelephone();
            } else {
                telephone = "";
            }
            if (currentUser.getUserEmail() != null) {
                email = currentUser.getUserEmail();
            } else {
                email = "";
            }
            if (currentUser.getUserPassword() != null) {
                passwordTmp = currentUser.getUserPassword();
                password = currentUser.getUserPassword();
                confirmPassword = currentUser.getUserPassword();
            } else {
                password = "";
                confirmPassword = "";
            }
            if (currentUser.getUserAddress() != null) {
                address = currentUser.getUserAddress();
            } else {
                address = "";
            }
            if (currentUser.getUserLogin() != null) {
                login = currentUser.getUserLogin();
            } else {
                login = "";
            }

            if (currentUser.getActive()) {
                stateUser = "Activa";
            } else {
                stateUser = "Inactiva";
            }
            permission1 = false;
            permission2 = false;
            permission3 = false;
            permission4 = false;
            permission5 = false;
            if (currentUser.getPermissions() != null && currentUser.getPermissions().length() != 0) {
                String[] splitPermissions = currentUser.getPermissions().split("\t");
                for (int i = 0; i < splitPermissions.length; i++) {
                    if (splitPermissions[i].compareTo("1") == 0) {
                        permission1 = true;
                    }
                    if (splitPermissions[i].compareTo("2") == 0) {
                        permission2 = true;
                    }
                    if (splitPermissions[i].compareTo("3") == 0) {
                        permission3 = true;
                    }
                    if (splitPermissions[i].compareTo("4") == 0) {
                        permission4 = true;
                    }
                    if (splitPermissions[i].compareTo("5") == 0) {
                        permission5 = true;
                    }
                }

            }
        }
    }

    /**
     * This method removes a user of the system, for it is necessary to select a
     * user from the list and this user is not active at this moment.
     */
    public void deleteRegistry() {
        boolean continueProcess = true;
        if (currentUser == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NO REALIZADO", "Se debe seleccionar un usuario de la lista");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            continueProcess = false;
        }
        if (continueProcess == true) {
            try {
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM projects WHERE user_id = " + currentUser.getUserId());
                if (rs.next()) {
                    continueProcess = false;
                } else {
                    rs = connectionJdbcMB.consult("SELECT * FROM fatal_injuries WHERE user_id = " + currentUser.getUserId());
                    if (rs.next()) {
                        continueProcess = false;
                    } else {
                        rs = connectionJdbcMB.consult("SELECT * FROM non_fatal_injuries WHERE user_id = " + currentUser.getUserId());
                        if (rs.next()) {
                            continueProcess = false;
                        }
                    }
                }
            } catch (Exception e) {
                continueProcess = false;
            }
            if (continueProcess == false) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NO REALIZADO", "El usuario que se intenta eliminar tiene registro de actividad dentro del sistema por lo cual no puede ser eliminado.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }

        if (continueProcess) {
            try {
                usersFacade.remove(currentUser);
                currentUser = null;

                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Exception e) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NO REALIZADO", "El usuario que se intenta eliminar tiene actividades dentro del sistema; por lo cual no puede ser eliminado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }

        selectedRowDataTable = null;
        createDynamicTable();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    /**
     * This method allows the administrator to update user information, the
     * required fields must be completed as name, login and password and finally
     * the user name must be available.
     */
    public void updateRegistry() {

        boolean continueProcess = true;
        if (currentUser == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Seleccionar usuario", "Se de be seleccionar un usuario de la lista");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            continueProcess = false;
        }

        if (continueProcess) {
            if (name.trim().length() == 0 || login.trim().length() == 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Faltan datos", "los campos: LOGIN y NOMBRES son obligatorios");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (password.trim().length() == 0 && confirmPassword.trim().length() == 0 && passwordTmp.trim().length() == 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Faltan datos", "Se debe digitar una clave y confirmacion de clave");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                continueProcess = false;
            } else {
                if (password.trim().length() == 0 && confirmPassword.trim().length() == 0 && passwordTmp.trim().length() != 0) {
                    password = passwordTmp;
                    confirmPassword = passwordTmp;
                }
            }
        }

        if (continueProcess) {
            if (password.compareTo(confirmPassword) != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Confirmacion de clave no coincide", "La clave y la confirmación de clave no coinciden");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                continueProcess = false;
            }
        }
        if (continueProcess) {
            Users u = usersFacade.findByLogin(login);
            if (u != null && currentUser.getUserLogin().compareTo(u.getUserLogin()) != 0) {

                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El login digitado ya esta en uso");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                continueProcess = false;
            }
        }
        if (continueProcess) {
            currentUser.setUserName(name);
            currentUser.setUserLogin(login);
            currentUser.setUserName(name);
            currentUser.setUserJob(job);
            currentUser.setUserInstitution(institution);
            currentUser.setUserTelephone(telephone);
            currentUser.setUserEmail(email);
            currentUser.setUserAddress(address);
            if (password.compareTo(passwordTmp) == 0) {//se mantiene el mismo pasword
                currentUser.setUserPassword(password);
            } else {//se digito un nuevo pasword
                currentUser.setUserPassword(stringEncryption.getStringMessageDigest(password, "SHA-1"));
            }

            String permissions = "";
            if (permission1) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t1";
                } else {
                    permissions = permissions + "1";
                }
            }
            if (permission2) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t2";
                } else {
                    permissions = permissions + "2";
                }
            }
            if (permission3) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t3";
                } else {
                    permissions = permissions + "3";
                }
            }
            if (permission4) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t4";
                } else {
                    permissions = permissions + "4";
                }
            }
            if (permission5) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t5";
                } else {
                    permissions = permissions + "5";
                }
            }
            currentUser.setPermissions(permissions);
            if (stateUser.compareTo("Activa") == 0) {
                currentUser.setActive(true);
            } else {
                currentUser.setActive(false);
            }
            usersFacade.edit(currentUser);
            name = "";
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * This method allows to register a new user, the user must complete
     * required fields such as name, login and password, finally the user name
     * must be available.
     */
    public void saveRegistry() {
        boolean continueProcess = true;
        if (newName.trim().length() == 0 || newPasword.trim().length() == 0 || newLogin.trim().length() == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Faltan datos", "los campos: NOMBRE, CLAVE y LOGIN son obligatorios");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            continueProcess = false;
        }
        if (continueProcess) {
            if (newPasword.compareTo(newConfirmPasword) != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Confirmacion de clave no coincide", "La clave y la confirmación de clave no coinciden");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                continueProcess = false;
            }
        }
        if (continueProcess) {
            Users u = usersFacade.findByLogin(newLogin);
            if (u != null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El login digitado ya esta en uso");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        if (continueProcess) {
            newName = newName.toUpperCase();
            Users newRegistry = new Users();
            newRegistry.setUserId(usersFacade.findMax() + 1);
            newRegistry.setUserLogin(newLogin);
            newRegistry.setUserName(newName);
            newRegistry.setUserJob(newJob);
            newRegistry.setUserInstitution(newInstitution);
            newRegistry.setUserTelephone(newtelephone);
            newRegistry.setUserEmail(newEmail);
            newRegistry.setUserAddress(newAddress);
            newRegistry.setUserPassword(stringEncryption.getStringMessageDigest(newPasword, "SHA-1"));
            String permissions = "";
            if (permission1) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t1";
                } else {
                    permissions = permissions + "1";
                }
            }
            if (permission2) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t2";
                } else {
                    permissions = permissions + "2";
                }
            }
            if (permission3) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t3";
                } else {
                    permissions = permissions + "3";
                }
            }
            if (permission4) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t4";
                } else {
                    permissions = permissions + "4";
                }
            }
            if (permission5) {
                if (permissions.length() != 0) {
                    permissions = permissions + "\t5";
                } else {
                    permissions = permissions + "5";
                }
            }
            newRegistry.setPermissions(permissions);
            if (newStateUser.compareTo("Activa") == 0) {
                newRegistry.setActive(true);
            } else {
                newRegistry.setActive(false);
            }
            usersFacade.create(newRegistry);
            newRegistry();
            currentUser = null;
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * This method is used to display the registration form completely empty to
     * register a new user.
     */
    public void newRegistry() {
        name = "";
        newName = "";
        job = "";
        newJob = "";
        institution = "";
        newInstitution = "";
        telephone = "";
        newtelephone = "";
        email = "";
        newEmail = "";
        password = "";
        newPasword = "";
        address = "";
        newAddress = "";
        login = "";
        newLogin = "";
        permission1 = true;
        permission2 = true;
        permission3 = true;
        permission4 = true;
        permission5 = true;
        stateUser = "Activa";
        newStateUser = "Activa";
    }

    /**
     * This method creates a table to show all users that are registered in the
     * database.
     */
    public void createDynamicTable() {
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<>();
            ResultSet rs;

            try {
                if (currentSearchCriteria == 1) {
                    rs = connectionJdbcMB.consult("select * from users where user_name ilike '%" + currentSearchValue + "%'");
                } else if (currentSearchCriteria == 2) {
                    rs = connectionJdbcMB.consult("select * from users where user_login ilike '%" + currentSearchValue + "%'");
                } else {
                    rs = connectionJdbcMB.consult("select * from users where user_job ilike '%" + currentSearchValue + "%'");
                }
                String active;
                while (rs.next()) {
                    if (rs.getBoolean("active") == false) {
                        active = "Inactiva";
                    } else {
                        active = "Activa";
                    }
                    rowDataTableList.add(new RowDataTable(
                            rs.getString("user_id"),
                            active,
                            rs.getString("user_login"),
                            rs.getString("user_name"),
                            rs.getString("user_job"),
                            rs.getString("user_institution"),
                            rs.getString("user_telephone"),
                            rs.getString("user_email"),
                            rs.getString("user_address")));
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
     * This method resets all the values found in the table, is called from the
     * method “createDynamicTable”.
     */
    public void reset() {
        rowDataTableList = new ArrayList<>();
        usersList = usersFacade.findAll();
        String active;
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getActive() == null || usersList.get(i).getActive() == false) {
                active = "Inactiva";
            } else {
                active = "Activa";
            }
            rowDataTableList.add(new RowDataTable(
                    usersList.get(i).getUserId().toString(),
                    active,
                    usersList.get(i).getUserLogin(),
                    usersList.get(i).getUserName(),
                    usersList.get(i).getUserJob(),
                    usersList.get(i).getUserInstitution(),
                    usersList.get(i).getUserTelephone(),
                    usersList.get(i).getUserEmail(),
                    usersList.get(i).getUserAddress()));
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changePermission1() {
        /*
         * determinar automaticamente permisos aceptados seleccione o desseleccione
         * las casillas de permisos 
         */
        if (permission1 == false) {
            permission5 = false;
        } else if (permission1 && permission2 && permission3 && permission4) {
            permission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changePermission2() {
        /*
         * determinar automaticamente permisos aceptados seleccione o desseleccione
         * las casillas de permisos 
         */
        if (permission2 == false) {
            permission5 = false;
        } else if (permission1 && permission2 && permission3 && permission4) {
            permission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changePermission3() {
        if (permission3 == false) {
            permission5 = false;
        } else if (permission1 && permission2 && permission3 && permission4) {
            permission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changePermission4() {
        if (permission4 == false) {
            permission5 = false;
        } else if (permission1 && permission2 && permission3 && permission4) {
            permission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changePermission5() {
        if (permission5 == true) {
            permission1 = true;
            permission2 = true;
            permission3 = true;
            permission4 = true;
            permission5 = true;
        } else if (permission1 && permission2 && permission3 && permission4) {
            permission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changeNewPermission1() {
        if (newPermission1 == false) {
            newPermission5 = false;
        } else if (newPermission1 && newPermission2 && newPermission3 && newPermission4) {
            newPermission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changeNewPermission2() {
        if (newPermission2 == false) {
            newPermission5 = false;
        } else if (newPermission1 && newPermission2 && newPermission3 && newPermission4) {
            newPermission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changeNewPermission3() {
        if (newPermission3 == false) {
            newPermission5 = false;
        } else if (newPermission1 && newPermission2 && newPermission3 && newPermission4) {
            newPermission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changeNewPermission4() {
        if (newPermission4 == false) {
            newPermission5 = false;
        } else if (newPermission1 && newPermission2 && newPermission3 && newPermission4) {
            newPermission5 = true;
        }
    }

    /**
     * This method automatically determines the permissions accepted when the
     * user select or deselect a checkbox permission.
     */
    public void changeNewPermission5() {
        if (newPermission5 == true) {
            newPermission1 = true;
            newPermission2 = true;
            newPermission3 = true;
            newPermission4 = true;
            newPermission5 = true;
        } else if (newPermission1 && newPermission2 && newPermission3 && newPermission4) {
            newPermission5 = true;
        }
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

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentActivitiy(Users currentUser) {
        this.currentUser = currentUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(String newInstitution) {
        this.newInstitution = newInstitution;
    }

    public String getNewJob() {
        return newJob;
    }

    public void setNewJob(String newJob) {
        this.newJob = newJob;
    }

    public String getNewPasword() {
        return newPasword;
    }

    public void setNewPasword(String newPasword) {
        this.newPasword = newPasword;
    }

    public String getNewtelephone() {
        return newtelephone;
    }

    public void setNewtelephone(String newtelephone) {
        this.newtelephone = newtelephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(String newLogin) {
        this.newLogin = newLogin;
    }

    public boolean isPermission1() {
        return permission1;
    }

    public void setPermission1(boolean permission1) {
        this.permission1 = permission1;
    }

    public boolean isPermission2() {
        return permission2;
    }

    public void setPermission2(boolean permission2) {
        this.permission2 = permission2;
    }

    public boolean isPermission3() {
        return permission3;
    }

    public void setPermission3(boolean permission3) {
        this.permission3 = permission3;
    }

    public boolean isPermission4() {
        return permission4;
    }

    public void setPermission4(boolean permission4) {
        this.permission4 = permission4;
    }

    public boolean isPermission5() {
        return permission5;
    }

    public void setPermission5(boolean permission5) {
        this.permission5 = permission5;
    }

    public boolean isNewPermission1() {
        return newPermission1;
    }

    public void setNewPermission1(boolean newPermission1) {
        this.newPermission1 = newPermission1;
    }

    public boolean isNewPermission2() {
        return newPermission2;
    }

    public void setNewPermission2(boolean newPermission2) {
        this.newPermission2 = newPermission2;
    }

    public boolean isNewPermission3() {
        return newPermission3;
    }

    public void setNewPermission3(boolean newPermission3) {
        this.newPermission3 = newPermission3;
    }

    public boolean isNewPermission4() {
        return newPermission4;
    }

    public void setNewPermission4(boolean newPermission4) {
        this.newPermission4 = newPermission4;
    }

    public boolean isNewPermission5() {
        return newPermission5;
    }

    public void setNewPermission5(boolean newPermission5) {
        this.newPermission5 = newPermission5;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNewConfirmPasword() {
        return newConfirmPasword;
    }

    public void setNewConfirmPasword(String newConfirmPasword) {
        this.newConfirmPasword = newConfirmPasword;
    }

    public String getStateUser() {
        return stateUser;
    }

    public void setStateUser(String stateUser) {
        this.stateUser = stateUser;
    }

    public String getNewStateUser() {
        return newStateUser;
    }

    public void setNewStateUser(String newStateUser) {
        this.newStateUser = newStateUser;
    }
}
