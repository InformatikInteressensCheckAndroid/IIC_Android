package at.ac.htlstp.app.iic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.Date;

import at.ac.htlstp.app.cocolib.annotation.BatchDatabaseAccessor;
import at.ac.htlstp.app.cocolib.annotation.DatabaseAccessor;
import at.ac.htlstp.app.iic.db.accessor.QuizResultAccessor;
import at.ac.htlstp.app.iic.db.accessor.QuizResultListAccessor;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexnavratil on 25/12/15.
 */
@JsonSubTypes({
        @JsonSubTypes.Type(name = "answers", value = Answer.class)
})
@DatabaseAccessor(accessor = QuizResultAccessor.class)
@BatchDatabaseAccessor(accessor = QuizResultListAccessor.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizResult extends RealmObject {
    @JsonProperty
    @PrimaryKey
    private int Session_ID;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Index
    private Date StartStamp;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Index
    private Date EndStamp;

    @JsonProperty
    private int User_ID = -1;

    @JsonProperty
    private boolean Finished;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CHANGED;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Deleted_at;

    @JsonProperty
    private boolean passed;

    @JsonProperty
    private int percent;

    @JsonProperty
    private Quiz quiz;

    @JsonProperty
    private RealmList<FinishedQuizSessionQuestion> questions;

    public int getSession_ID() {
        return Session_ID;
    }

    public void setSession_ID(int session_ID) {
        Session_ID = session_ID;
    }

    public Date getStartStamp() {
        return StartStamp;
    }

    public void setStartStamp(Date startStamp) {
        StartStamp = startStamp;
    }

    public Date getEndStamp() {
        return EndStamp;
    }

    public void setEndStamp(Date endStamp) {
        EndStamp = endStamp;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public boolean isFinished() {
        return Finished;
    }

    public void setFinished(boolean finished) {
        Finished = finished;
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

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public RealmList<FinishedQuizSessionQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(RealmList<FinishedQuizSessionQuestion> questions) {
        this.questions = questions;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
