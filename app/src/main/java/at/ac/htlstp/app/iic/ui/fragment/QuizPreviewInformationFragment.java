package at.ac.htlstp.app.iic.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.Quiz;
import at.ac.htlstp.app.iic.ui.activity.QuizPreviewActivity;
import at.alexnavratil.navhelper.web.ReadabilityWebView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class QuizPreviewInformationFragment extends Fragment {

    @Bind(R.id.quizDescription)
    ReadabilityWebView mQuizDescription;

    private Quiz mQuiz;

    public QuizPreviewInformationFragment() {
        // Required empty public constructor
    }

    public static QuizPreviewInformationFragment getInstance(Quiz quiz) {
        QuizPreviewInformationFragment instance = new QuizPreviewInformationFragment();
        instance.mQuiz = quiz;

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_preview_information, container, false);

        ButterKnife.bind(this, view);

        mQuizDescription.enableFullscreenSupport((QuizPreviewActivity) getActivity());

        renderQuiz();

        return view;
    }

    private void renderQuiz() {
        if (mQuiz.getDescription().isEmpty()) {
            mQuizDescription.loadData(String.format("<div>%s</div>", getContext().getString(R.string.no_further_information_available)));
        } else {
            mQuizDescription.loadData(mQuiz.getDescription());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mQuizDescription.clearCache(true);
        mQuizDescription.loadData("", "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    public void onResume() {
        super.onResume();
        renderQuiz();
    }

    @Override
    public void onStop() {
        super.onStop();
        mQuizDescription.loadData("", "text/html; charset=utf-8", "UTF-8");
    }

}
