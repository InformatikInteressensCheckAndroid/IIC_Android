package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.Date;

import at.ac.htlstp.app.cocolib.annotation.DatabaseAccessor;
import at.ac.htlstp.app.iic.db.accessor.UserAccessor;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 15/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Language.class, name = "language"),
        @JsonSubTypes.Type(value = UserType.class, name = "user_type"),
        @JsonSubTypes.Type(value = UserClass.class, name = "user_class")})
@DatabaseAccessor(accessor = UserAccessor.class)
public class User extends RealmObject {
    @JsonProperty
    @PrimaryKey
    private int User_ID;

    @JsonProperty
    private String UserName;

    @JsonProperty
    private String password;

    @JsonProperty
    private String FirstName;

    @JsonProperty
    private String LastName;

    @JsonProperty
    private String EMail;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Registration;

    @JsonProperty
    private int YearInSchool;

    @JsonProperty
    private int PLZ;

    @JsonProperty
    private String Village;

    @JsonProperty
    private String Street;

    @JsonProperty
    private String Housenumber;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    @JsonProperty
    private Language language;

    @JsonProperty("user_type")
    private UserType userType;

    @JsonProperty("user_class")
    private UserClass userClass;

    @JsonIgnore
    private boolean isLocalUser;

    @JsonProperty
    private Country country;

    public User() {
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEMail() {
        return EMail;
    }

    public void setEMail(String EMail) {
        this.EMail = EMail;
    }

    public Date getRegistration() {
        return Registration;
    }

    public void setRegistration(Date registration) {
        Registration = registration;
    }

    public int getYearInSchool() {
        return YearInSchool;
    }

    public void setYearInSchool(int yearInSchool) {
        YearInSchool = yearInSchool;
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

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getHousenumber() {
        return Housenumber;
    }

    public void setHousenumber(String housenumber) {
        Housenumber = housenumber;
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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserClass getUserClass() {
        return userClass;
    }

    public void setUserClass(UserClass userClass) {
        this.userClass = userClass;
    }

    public boolean isLocalUser() {
        return isLocalUser;
    }

    public void setLocalUser(boolean localUser) {
        isLocalUser = localUser;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
