package at.ac.htlstp.app.iic.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Date;

import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.ui.activity.QuizResultActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizResultDetailFragment extends Fragment {

    @Bind(R.id.textTimeAvailable)
    TextView mTimeAvailableView;

    @Bind(R.id.textPassPercent)
    TextView mPassPercentView;

    @Bind(R.id.textDate)
    TextView mDateView;

    @Bind(R.id.certificateLayout)
    LinearLayout certificateLayout;

    private QuizResult mQuizResult;

    public QuizResultDetailFragment() {
        // Required empty public constructor
    }

    public static QuizResultDetailFragment getInstance(QuizResult quizResult) {
        QuizResultDetailFragment instance = new QuizResultDetailFragment();
        instance.mQuizResult = quizResult;

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_result_detail, container, false);

        ButterKnife.bind(this, view);

        mPassPercentView.setText(formattedPercentText());
        mDateView.setText(formattedDateTime());

        mTimeAvailableView.setText(String.format(getContext().getString(R.string.result_time), timeNeededDifference(), timeAvailable()));

        if (mQuizResult.isPassed()) {
            certificateLayout.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @OnClick(R.id.showQuestionsButton)
    public void onShowQuestionsButtonClicked() {
        ((QuizResultActivity) getActivity()).showQuestions();
    }

    @OnClick(R.id.showCertificateButton)
    public void onShowCertificateButtonClicked() {
        String url = CocoLibSingleton.URL + "quiz/cert/" + mQuizResult.getSession_ID();
        Intent certificateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(certificateIntent);
    }

    private String formattedDateTime() {
        LocalDateTime dateTime = toLocalDateTime(mQuizResult.getStartStamp());
        return dateTime.format(DateTimeFormatter.ofPattern("dd. MMMM yyyy, HH:mm"));
    }

    private String formattedPercentText() {
        String text;
        if (mQuizResult.isPassed()) {
            text = getContext().getString(R.string.quiz_success);
        } else {
            text = getContext().getString(R.string.quiz_fail);
        }

        return String.format("%d%%, %s", mQuizResult.getPercent(), text);
    }

    private String timeAvailable() {
        LocalTime startTime = LocalTime.MIDNIGHT;
        LocalTime availableTime = startTime.plusSeconds(mQuizResult.getQuiz().getMaxSeconds());

        Duration difference = Duration.between(startTime, availableTime);

        int hours = (int) difference.toHours();
        difference = difference.minusHours(hours);

        int minutes = (int) difference.toMinutes();
        difference = difference.minusMinutes(minutes);

        int seconds = (int) (difference.toMillis() / 1000);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private String timeNeededDifference() {
        LocalDateTime quizStart = toLocalDateTime(mQuizResult.getStartStamp());
        LocalDateTime quizEnd = toLocalDateTime(mQuizResult.getEndStamp());

        Duration difference = Duration.between(quizStart, quizEnd);

        int hours = (int) difference.toHours();
        difference = difference.minusHours(hours);

        int minutes = (int) difference.toMinutes();
        difference = difference.minusMinutes(minutes);

        int seconds = (int) (difference.toMillis() / 1000);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /*private boolean isOverLimit(){
        LocalDateTime quizStart = toLocalDateTime(mQuizResult.getStartStamp());
        LocalDateTime quizRegularEnd = quizStart.plusSeconds(mQuizResult.getQuiz().getMaxSeconds());
        LocalDateTime quizEnd = toLocalDateTime(mQuizResult.getEndStamp());

        return quizEnd.isAfter(quizRegularEnd);
    }*/

    private String timeLimitDifference() {
        LocalDateTime quizStart = toLocalDateTime(mQuizResult.getStartStamp());
        LocalDateTime quizRegularEnd = quizStart.plusSeconds(mQuizResult.getQuiz().getMaxSeconds());
        LocalDateTime quizEnd = toLocalDateTime(mQuizResult.getEndStamp());

        Duration difference = Duration.between(quizRegularEnd, quizEnd);

        LocalTime differenceTime = LocalTime.MIDNIGHT;
        differenceTime.plus(difference);

        return differenceTime.format(DateTimeFormatter.ofPattern("kk:mm:ss"));
    }

    private LocalDateTime toLocalDateTime(Date date) {
        Instant tmpInstant = Instant.ofEpochMilli(date.getTime());
        return LocalDateTime.ofInstant(tmpInstant, ZoneId.systemDefault());
    }

}
