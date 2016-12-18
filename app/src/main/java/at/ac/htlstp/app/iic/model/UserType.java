package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 22/12/15.
 */
public class UserType extends RealmObject {
    @PrimaryKey
    @JsonProperty
    private int Usertype_ID;

    @JsonProperty
    private String Description;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    public UserType() {
    }

    public UserType(int usertype_ID, String description, Date CHANGED, Date deleted_at) {
        Usertype_ID = usertype_ID;
        Description = description;
        this.CHANGED = CHANGED;
        Deleted_at = deleted_at;
    }

    public int getUsertype_ID() {
        return Usertype_ID;
    }

    public void setUsertype_ID(int usertype_ID) {
        Usertype_ID = usertype_ID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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
