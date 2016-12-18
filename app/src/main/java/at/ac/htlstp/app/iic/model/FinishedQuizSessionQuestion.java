package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 26/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinishedQuizSessionQuestion extends RealmObject {
    @PrimaryKey
    @JsonProperty
    private int SessionQuestion_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Stamp;

    @JsonProperty
    private Question question;

    @JsonProperty
    private RealmList<FinishedQuizSessionAnswer> answers;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    public int getSessionQuestion_ID() {
        return SessionQuestion_ID;
    }

    public void setSessionQuestion_ID(int sessionQuestion_ID) {
        SessionQuestion_ID = sessionQuestion_ID;
    }

    public Date getStamp() {
        return Stamp;
    }

    public void setStamp(Date stamp) {
        Stamp = stamp;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public RealmList<FinishedQuizSessionAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(RealmList<FinishedQuizSessionAnswer> answers) {
        this.answers = answers;
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
