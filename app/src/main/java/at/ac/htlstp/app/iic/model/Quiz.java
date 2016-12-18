package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import at.ac.htlstp.app.cocolib.annotation.BatchDatabaseAccessor;
import at.ac.htlstp.app.iic.db.accessor.QuizAccessor;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 24/12/15.
 */
@BatchDatabaseAccessor(accessor = QuizAccessor.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quiz extends RealmObject {
    @JsonProperty
    @PrimaryKey
    private int Quiz_ID;

    @JsonProperty
    @Index
    private String Name;

    @JsonProperty
    private int MaxSeconds;

    @JsonProperty
    private String ShortDescription;

    @JsonProperty
    private String Description;

    @JsonProperty
    @Index
    private int QuizCategory_Language_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    @JsonProperty
    private int QuestionGroup_Count;

    @JsonProperty
    private int PassPercent;

    @JsonProperty
    private int Depending_Quiz_ID = -1;

    private int updateid = 0;

    public int getQuiz_ID() {
        return Quiz_ID;
    }

    public void setQuiz_ID(int quiz_ID) {
        Quiz_ID = quiz_ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getMaxSeconds() {
        return MaxSeconds;
    }

    public void setMaxSeconds(int maxSeconds) {
        MaxSeconds = maxSeconds;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getQuizCategory_Language_ID() {
        return QuizCategory_Language_ID;
    }

    public void setQuizCategory_Language_ID(int quizCategory_Language_ID) {
        QuizCategory_Language_ID = quizCategory_Language_ID;
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

    public int getQuestionGroup_Count() {
        return QuestionGroup_Count;
    }

    public void setQuestionGroup_Count(int questionGroup_Count) {
        QuestionGroup_Count = questionGroup_Count;
    }

    public int getPassPercent() {
        return PassPercent;
    }

    public void setPassPercent(int passPercent) {
        PassPercent = passPercent;
    }

    public int getDepending_Quiz_ID() {
        return Depending_Quiz_ID;
    }

    public void setDepending_Quiz_ID(int depending_Quiz_ID) {
        Depending_Quiz_ID = depending_Quiz_ID;
    }

    public String getShortDescription() {
        return ShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        ShortDescription = shortDescription;
    }

    public int getUpdateid() {
        return updateid;
    }

    public void setUpdateid(int updateid) {
        this.updateid = updateid;
    }
}
