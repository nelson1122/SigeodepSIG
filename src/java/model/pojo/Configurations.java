/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "configurations", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Configurations.findAll", query = "SELECT c FROM Configurations c"),
    @NamedQuery(name = "Configurations.findByUserDb", query = "SELECT c FROM Configurations c WHERE c.userDb = :userDb"),
    @NamedQuery(name = "Configurations.findByPasswordDb", query = "SELECT c FROM Configurations c WHERE c.passwordDb = :passwordDb"),
    @NamedQuery(name = "Configurations.findByNameDb", query = "SELECT c FROM Configurations c WHERE c.nameDb = :nameDb"),
    @NamedQuery(name = "Configurations.findByServerDb", query = "SELECT c FROM Configurations c WHERE c.serverDb = :serverDb"),
    @NamedQuery(name = "Configurations.findByNameDbDwh", query = "SELECT c FROM Configurations c WHERE c.nameDbDwh = :nameDbDwh")})
public class Configurations implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "user_db", nullable = false, length = 2147483647)
    private String userDb;
    @Size(max = 2147483647)
    @Column(name = "password_db", length = 2147483647)
    private String passwordDb;
    @Size(max = 2147483647)
    @Column(name = "name_db", length = 2147483647)
    private String nameDb;
    @Size(max = 2147483647)
    @Column(name = "server_db", length = 2147483647)
    private String serverDb;
    @Size(max = 2147483647)
    @Column(name = "name_db_dwh", length = 2147483647)
    private String nameDbDwh;

    public Configurations() {
    }

    public Configurations(String userDb) {
        this.userDb = userDb;
    }

    public String getUserDb() {
        return userDb;
    }

    public void setUserDb(String userDb) {
        this.userDb = userDb;
    }

    public String getPasswordDb() {
        return passwordDb;
    }

    public void setPasswordDb(String passwordDb) {
        this.passwordDb = passwordDb;
    }

    public String getNameDb() {
        return nameDb;
    }

    public void setNameDb(String nameDb) {
        this.nameDb = nameDb;
    }

    public String getServerDb() {
        return serverDb;
    }

    public void setServerDb(String serverDb) {
        this.serverDb = serverDb;
    }

    public String getNameDbDwh() {
        return nameDbDwh;
    }

    public void setNameDbDwh(String nameDbDwh) {
        this.nameDbDwh = nameDbDwh;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userDb != null ? userDb.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Configurations)) {
            return false;
        }
        Configurations other = (Configurations) object;
        if ((this.userDb == null && other.userDb != null) || (this.userDb != null && !this.userDb.equals(other.userDb))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Configurations[ userDb=" + userDb + " ]";
    }
    
}
