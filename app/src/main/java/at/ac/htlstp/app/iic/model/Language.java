package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import at.ac.htlstp.app.cocolib.annotation.BatchDatabaseAccessor;
import at.ac.htlstp.app.iic.db.accessor.LanguageAccessor;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 16/12/15.
 */
@BatchDatabaseAccessor(accessor = LanguageAccessor.class)
public class Language extends RealmObject {
    @JsonProperty
    @PrimaryKey
    private int Language_ID;

    @JsonProperty
    private String Description;

    @JsonProperty
    @Index
    private String InLanguage;

    @JsonProperty
    private String Shortcode;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    private int updateid = 0;

    public Language() {
    }

    public Language(String description, String inLanguage, int language_ID, String shortcode, Date CHANGED, Date deleted_at) {
        Description = description;
        InLanguage = inLanguage;
        Language_ID = language_ID;
        Shortcode = shortcode;
        this.CHANGED = CHANGED;
        Deleted_at = deleted_at;
    }

    public int getLanguage_ID() {
        return Language_ID;
    }

    public void setLanguage_ID(int language_ID) {
        Language_ID = language_ID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getInLanguage() {
        return InLanguage;
    }

    public void setInLanguage(String inLanguage) {
        InLanguage = inLanguage;
    }

    public String getShortcode() {
        return Shortcode;
    }

    public void setShortcode(String shortcode) {
        Shortcode = shortcode;
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

    public int getUpdateid() {
        return updateid;
    }

    public void setUpdateid(int updateid) {
        this.updateid = updateid;
    }
}
