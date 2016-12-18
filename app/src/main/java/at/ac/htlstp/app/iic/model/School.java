package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import at.ac.htlstp.app.cocolib.annotation.BatchDatabaseAccessor;
import at.ac.htlstp.app.iic.db.accessor.SchoolAccessor;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 03/12/15.
 */
@BatchDatabaseAccessor(accessor = SchoolAccessor.class)
public class School extends RealmObject {
    @JsonProperty
    @PrimaryKey
    private int School_ID;

    @JsonProperty
    private int SKZ;

    @JsonProperty
    private String Schoolname;

    @JsonProperty
    private int PLZ;

    @JsonProperty
    private String Village;

    @JsonProperty
    private String Housenumber;

    @JsonProperty
    private String Street;

    @JsonProperty
    private String Stair;

    @JsonProperty
    private String Door;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    public School() {
    }

    public School(int school_ID, String schoolname, int PLZ, String village, String housenumber, String street, String stair, String door, Date CHANGED, Date deleted_at) {
        School_ID = school_ID;
        Schoolname = schoolname;
        this.PLZ = PLZ;
        Village = village;
        Housenumber = housenumber;
        Street = street;
        Stair = stair;
        Door = door;
        this.CHANGED = CHANGED;
        Deleted_at = deleted_at;
    }

    public int getSchool_ID() {
        return School_ID;
    }

    public void setSchool_ID(int school_ID) {
        School_ID = school_ID;
    }

    public int getSKZ() {
        return SKZ;
    }

    public void setSKZ(int SKZ) {
        this.SKZ = SKZ;
    }

    public String getSchoolname() {
        return Schoolname;
    }

    public void setSchoolname(String schoolname) {
        Schoolname = schoolname;
    }

    public int getPLZ() {
        return PLZ;
    }

    public void setPLZ(int PLZ) {
        this.PLZ = PLZ;
    }

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getHousenumber() {
        return Housenumber;
    }

    public void setHousenumber(String housenumber) {
        Housenumber = housenumber;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getStair() {
        return Stair;
    }

    public void setStair(String stair) {
        Stair = stair;
    }

    public String getDoor() {
        return Door;
    }

    public void setDoor(String door) {
        Door = door;
    }

    public Date getCHANGED() {
        return CHANGED;
    }

    public void setCHANGED(Date CHANGED) {
        this.CHANGED = CHANGED;
    }

    public Date getDeleted_at() {
        return Deleted_at;
    }

    public void setDeleted_at(Date deleted_at) {
        Deleted_at = deleted_at;
    }
}
