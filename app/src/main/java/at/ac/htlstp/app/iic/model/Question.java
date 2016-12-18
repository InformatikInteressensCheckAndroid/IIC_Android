package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 25/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question extends RealmObject {
    @PrimaryKey
    @JsonProperty
    private int Question_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Makestamp;

    @JsonProperty
    private String Q_HTML;

    @JsonProperty
    private int QuestionType_ID;

    @JsonProperty
    private int Points;

    @JsonProperty
    private int QuestionGroup_ID;

    @JsonProperty
    private int SessionQuestion_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    @JsonProperty("answers")
    private RealmList<Answer> answerList;

    public int getQuestion_ID() {
        return Question_ID;
    }

    public void setQuestion_ID(int question_ID) {
        Question_ID = question_ID;
    }

    public Date getMakestamp() {
        return Makestamp;
    }

    public void setMakestamp(Date makestamp) {
        Makestamp = makestamp;
    }

    public String getQ_HTML() {
        return Q_HTML;
    }

    public void setQ_HTML(String q_HTML) {
        Q_HTML = q_HTML;
    }

    public int getQuestionType_ID() {
        return QuestionType_ID;
    }

    public void setQuestionType_ID(int questionType_ID) {
        QuestionType_ID = questionType_ID;
    }

    public int getPoints() {
        return Points;
    }

    public void setPoints(int points) {
        Points = points;
    }

    public int getQuestionGroup_ID() {
        return QuestionGroup_ID;
    }

    public void setQuestionGroup_ID(int questionGroup_ID) {
        QuestionGroup_ID = questionGroup_ID;
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

    public RealmList<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(RealmList<Answer> answerList) {
        this.answerList = answerList;
    }

    public int getSessionQuestion_ID() {
        return SessionQuestion_ID;
    }

    public void setSessionQuestion_ID(int sessionQuestion_ID) {
        SessionQuestion_ID = sessionQuestion_ID;
    }
}
