package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 25/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinishedQuizSessionAnswer extends RealmObject {
    @PrimaryKey
    @JsonProperty
    private int SessionAnswer_ID;

    @JsonProperty
    private String AnswerOfUser;

    @JsonProperty
    private Answer answer;

    @JsonProperty
    private int SessionQuestion_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    public int getSessionAnswer_ID() {
        return SessionAnswer_ID;
    }

    public void setSessionAnswer_ID(int sessionAnswer_ID) {
        SessionAnswer_ID = sessionAnswer_ID;
    }

    public String getAnswerOfUser() {
        return AnswerOfUser;
    }

    public void setAnswerOfUser(String answerOfUser) {
        AnswerOfUser = answerOfUser;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public int getSessionQuestion_ID() {
        return SessionQuestion_ID;
    }

    public void setSessionQuestion_ID(int sessionQuestion_ID) {
        SessionQuestion_ID = sessionQuestion_ID;
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
