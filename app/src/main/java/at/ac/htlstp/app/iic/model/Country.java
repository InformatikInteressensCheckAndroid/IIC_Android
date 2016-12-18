package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import at.ac.htlstp.app.cocolib.annotation.BatchDatabaseAccessor;
import at.ac.htlstp.app.iic.db.accessor.CountryAccessor;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 16/12/15.
 */
@BatchDatabaseAccessor(accessor = CountryAccessor.class)
public class Country extends RealmObject {
    @JsonProperty
    @PrimaryKey
    private int Country_ID;

    @JsonProperty
    private String code;

    @JsonProperty
    private String en;

    @JsonProperty
    private String de;

    @JsonProperty
    private String es;

    @JsonProperty
    private String fr;

    @JsonProperty
    private String it;

    @JsonProperty
    private String ru;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    public Country() {
    }

    public Country(int country_id, String code, String en, String de, String es, String fr, String it, String ru) {
        Country_ID = country_id;
        this.code = code;
        this.en = en;
        this.de = de;
        this.es = es;
        this.fr = fr;
        this.it = it;
        this.ru = ru;
    }

    public int getCountry_ID() {
        return Country_ID;
    }

    public void setCountry_ID(int country_ID) {
        Country_ID = country_ID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public String getIt() {
        return it;
    }

    public void setIt(String it) {
        this.it = it;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
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
