package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 30/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionGroup extends RealmObject {
    @JsonProperty
    @PrimaryKey
    private int QuestionGroup_ID;

    @JsonProperty
    private String Title;

    @JsonProperty
    private String Description;

    @JsonProperty
    private int Quiz_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    @JsonProperty("questions")
    private RealmList<Question> questionList;

    public int getQuestionGroup_ID() {
        return QuestionGroup_ID;
    }

    public void setQuestionGroup_ID(int questionGroup_ID) {
        QuestionGroup_ID = questionGroup_ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getQuiz_ID() {
        return Quiz_ID;
    }

    public void setQuiz_ID(int quiz_ID) {
        Quiz_ID = quiz_ID;
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

    public RealmList<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(RealmList<Question> questionList) {
        this.questionList = questionList;
    }
}
