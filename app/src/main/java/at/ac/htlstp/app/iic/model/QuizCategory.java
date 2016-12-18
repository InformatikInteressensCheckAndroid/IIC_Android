package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.Date;

import at.ac.htlstp.app.cocolib.annotation.BatchDatabaseAccessor;
import at.ac.htlstp.app.iic.db.accessor.QuizCategoryAccessor;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 22/12/15.
 */

@JsonSubTypes({
        @JsonSubTypes.Type(value = Language.class, name = "language")
})
@BatchDatabaseAccessor(accessor = QuizCategoryAccessor.class)
public class QuizCategory extends RealmObject {
    @PrimaryKey
    @JsonProperty
    private int QuizCategory_Language_ID;

    @JsonProperty
    private int QuizCategory_ID = -1;

    @JsonProperty
    private Language language;

    @JsonProperty
    private String Description;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    private int updateid = 0;

    public QuizCategory() {
    }

    public int getQuizCategory_Language_ID() {
        return QuizCategory_Language_ID;
    }

    public void setQuizCategory_Language_ID(int quizCategory_Language_ID) {
        QuizCategory_Language_ID = quizCategory_Language_ID;
    }

    public int getQuizCategory_ID() {
        return QuizCategory_ID;
    }

    public void setQuizCategory_ID(int quizCategory_ID) {
        QuizCategory_ID = quizCategory_ID;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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

    public int getUpdateid() {
        return updateid;
    }

    public void setUpdateid(int updateid) {
        this.updateid = updateid;
    }
}
