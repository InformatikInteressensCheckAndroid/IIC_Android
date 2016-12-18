package at.ac.htlstp.app.iic.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.Quiz;
import butterknife.Bind;
import butterknife.ButterKnife;

public class QuizPreviewDetailFragment extends Fragment {

    @Bind(R.id.textTimeAvailable)
    TextView mTextTimeAvailable;

    @Bind(R.id.textPassPercent)
    TextView mTextPassPercent;

    @Bind(R.id.textShortDescription)
    TextView mTextShortDescription;

    private Quiz mQuiz;

    public QuizPreviewDetailFragment() {
        // Required empty public constructor
    }

    public static QuizPreviewDetailFragment getInstance(Quiz quiz) {
        QuizPreviewDetailFragment instance = new QuizPreviewDetailFragment();
        instance.mQuiz = quiz;

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_preview_detail, container, false);

        ButterKnife.bind(this, view);

        renderQuiz();

        return view;
    }

    private void renderQuiz() {
        if (mQuiz != null) {
            mTextTimeAvailable.setText(getTimeAvailable());
            mTextPassPercent.setText(mQuiz.getPassPercent() + "%");
            mTextShortDescription.setText(mQuiz.getShortDescription());
        } else {
            mTextTimeAvailable.setText("...");
            mTextPassPercent.setText("...");
            mTextShortDescription.setText("...");
        }
    }

    private String getTimeAvailable() {
        String output = "";

        int hours = mQuiz.getMaxSeconds() / 3600;
        int minutes = (mQuiz.getMaxSeconds() - hours * 3600) / 60;
        int seconds = mQuiz.getMaxSeconds() - (hours * 3600) - (minutes * 60);

        if (hours > 0) {
            if (hours == 1) {
                output += String.format(getContext().getString(R.string.hour), hours);
            } else {
                output += String.format(getContext().getString(R.string.hours), hours);
            }
        }

        if (minutes > 0) {
            if (!output.isEmpty()) {
                output += ", ";
            }
            if (minutes == 1) {
                output += String.format(getContext().getString(R.string.minute), minutes);
            } else {
                output += String.format(getContext().getString(R.string.minutes), minutes);
            }
        }

        if (seconds > 0) {
            if (!output.isEmpty()) {
                output += ", ";
            }
            if (seconds == 1) {
                output += String.format(getContext().getString(R.string.second), seconds);
            } else {
                output += String.format(getContext().getString(R.string.seconds), seconds);
            }
        }

        return output;
    }

}
