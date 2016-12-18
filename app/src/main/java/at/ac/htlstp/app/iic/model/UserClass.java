package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.Date;

import at.ac.htlstp.app.cocolib.annotation.BatchDatabaseAccessor;
import at.ac.htlstp.app.iic.db.accessor.ClassAccessor;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 11/12/15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = School.class, name = "school")
})
@BatchDatabaseAccessor(accessor = ClassAccessor.class)
public class UserClass extends RealmObject {
    @JsonProperty
    @PrimaryKey
    private int Class_ID;

    @JsonProperty
    private String Classname;

    @JsonProperty
    private int School_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    @JsonProperty
    private School school;

    public UserClass() {
    }

    public UserClass(int class_ID, String classname, Date CHANGED, Date deleted_at) {
        Class_ID = class_ID;
        Classname = classname;
        this.CHANGED = CHANGED;
        Deleted_at = deleted_at;
    }

    public int getClass_ID() {
        return Class_ID;
    }

    public void setClass_ID(int class_ID) {
        Class_ID = class_ID;
    }

    public String getClassname() {
        return Classname;
    }

    public void setClassname(String classname) {
        Classname = classname;
    }

    public int getSchool_ID() {
        return School_ID;
    }

    public void setSchool_ID(int school_ID) {
        School_ID = school_ID;
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

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}
