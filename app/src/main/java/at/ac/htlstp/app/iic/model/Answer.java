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
public class Answer extends RealmObject {
    @PrimaryKey
    @JsonProperty
    private int Answer_ID;

    @JsonProperty
    private String Answer;

    @JsonProperty
    private int Question_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    @JsonProperty
    private boolean Correct;

    @JsonProperty("AnswerOfUser")
    private String answerOfUser;

    public int getAnswer_ID() {
        return Answer_ID;
    }

    public void setAnswer_ID(int answer_ID) {
        Answer_ID = answer_ID;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public int getQuestion_ID() {
        return Question_ID;
    }

    public void setQuestion_ID(int question_ID) {
        Question_ID = question_ID;
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

    public boolean isCorrect() {
        return Correct;
    }

    public void setCorrect(boolean correct) {
        Correct = correct;
    }

    public String getAnswerOfUser() {
        return answerOfUser;
    }

    public void setAnswerOfUser(String answerOfUser) {
        this.answerOfUser = answerOfUser;
    }
}
