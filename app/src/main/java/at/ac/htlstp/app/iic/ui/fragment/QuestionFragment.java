package at.ac.htlstp.app.iic.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.Answer;
import at.ac.htlstp.app.iic.model.Question;
import at.ac.htlstp.app.iic.model.QuestionGroup;
import at.ac.htlstp.app.iic.ui.activity.QuizActivity;
import at.ac.htlstp.app.iic.util.StringUtils;
import at.alexnavratil.navhelper.web.ReadabilityWebView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexnavratil on 28/12/15.
 */
public class QuestionFragment extends Fragment {
    private static final int QUESTION_TYPE_MULTI = 1;
    private static final int QUESTION_TYPE_SINGLE = 2;
    private static final int QUESTION_TYPE_TEXT = 3;

    @Bind(R.id.questionView)
    ReadabilityWebView mQuestionView;

    @Bind(R.id.answerLayout)
    LinearLayout mAnswerLayout;

    @Bind(R.id.btnPreviousQuestion)
    Button mPreviousButton;

    @Bind(R.id.btnNextQuestion)
    Button mNextButton;

    @Bind(R.id.questionNumberView)
    TextView mQuestionNumberView;

    private OnQuestionAnsweredListener mQuestionAnsweredListener;

    private OnDisplayQuestionGroupListener mDisplayQuestionGroupListener;

    private QuestionGroup mQuestionGroup;

    private int mQuestionIndex = 0;

    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.question_fragment, container, false);

        ButterKnife.bind(this, v);

        params.topMargin = params.bottomMargin = params.leftMargin = params.rightMargin = 10;

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionIndex--;
                renderQuestion();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionIndex++;
                renderQuestion();
            }
        });

        mQuestionView.enableFullscreenSupport((QuizActivity) getActivity());

        renderQuestion();

        return v;
    }

    public void setQuestionGroup(QuestionGroup questionGroup) {
        this.mQuestionGroup = questionGroup;
    }

    private void renderQuestion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mQuestionView.setVisibility(View.INVISIBLE);
        }

        if (mQuestionIndex >= 0 && mQuestionGroup.getQuestionList().size() > mQuestionIndex) {
            Question currentQuestion = mQuestionGroup.getQuestionList().get(mQuestionIndex);

            mQuestionView.loadData(currentQuestion.getQ_HTML());

            mQuestionView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Intent externalPage = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(externalPage);
                    return true;
                }

                @Override
                public void onPageCommitVisible(WebView view, String url) {
                    super.onPageCommitVisible(view, url);
                    view.setLayoutParams(params);
                    view.setVisibility(View.VISIBLE);
                }
            });

            mQuestionNumberView.setText((mQuestionIndex + 1) + " / " + mQuestionGroup.getQuestionList().size());

            renderAnswer();

            /**
             * IF last question THEN change text of nextQuestionButton
             */
            if (mQuestionIndex == (mQuestionGroup.getQuestionList().size() - 1)) {
                mNextButton.setText(R.string.back_to_questiongroups);
            } else {
                mNextButton.setText(R.string.next_question);
            }

            if (mQuestionIndex == 0) {
                mPreviousButton.setText(R.string.back_to_questiongroups);
            } else {
                mPreviousButton.setText(R.string.previous_question);
            }
        } else {
            mDisplayQuestionGroupListener.onDisplayQuestionGroupList();
        }
    }

    private void renderAnswer() {
        Question question = mQuestionGroup.getQuestionList().get(mQuestionIndex);

        mAnswerLayout.removeAllViews();

        switch (question.getQuestionType_ID()) {
            case QUESTION_TYPE_MULTI:
                renderMultiAnswer(question);
                break;
            case QUESTION_TYPE_SINGLE:
                renderSingleAnswer(question);
                break;
            case QUESTION_TYPE_TEXT:
                renderTextAnswer(question);
                break;
        }
    }

    private void renderTextAnswer(final Question question) {
        final EditText answerText = new AppCompatEditText(getContext());
        answerText.setSingleLine(true);

        if (question.getAnswerList().size() > 0 && question.getAnswerList().get(0).getAnswerOfUser() != null) {
            answerText.setText(question.getAnswerList().get(0).getAnswerOfUser());
        }

        answerText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (question.getAnswerList().size() > 0) {
                    question.getAnswerList().get(0).setAnswerOfUser(answerText.getText().toString());
                    mQuestionAnsweredListener.onQuestionAnswered();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        answerText.setLayoutParams(params);

        mAnswerLayout.addView(answerText, params);
    }

    private void renderMultiAnswer(final Question question) {
        for (final Answer answer : question.getAnswerList()) {
            final CheckBox answerCheckBox = new AppCompatCheckBox(getContext());
            answerCheckBox.setText(answer.getAnswer());

            boolean checked = StringUtils.equals(answer.getAnswerOfUser(), "true");
            answerCheckBox.setChecked(checked);

            answerCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer.setAnswerOfUser(answerCheckBox.isChecked() ? "true" : "false");
                    setMultiAnswer(answer, question.getAnswerList()); //set non-selected answers to false instead of leaving them null
                    mQuestionAnsweredListener.onQuestionAnswered();
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 10;
            answerCheckBox.setLayoutParams(params);

            mAnswerLayout.addView(answerCheckBox, params);
        }
    }

    private void renderSingleAnswer(final Question question) {
        RadioGroup answerGroup = new RadioGroup(getContext());

        RadioGroup.LayoutParams answerParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        answerParams.bottomMargin = 10;

        int checkedItem = -1;

        for (final Answer answer : question.getAnswerList()) {
            RadioButton answerButton = new AppCompatRadioButton(getContext());
            answerButton.setId(answer.getAnswer_ID());
            answerButton.setText(answer.getAnswer());

            boolean checked = StringUtils.equals(answer.getAnswerOfUser(), "true");
            if (checked) {
                checkedItem = answer.getAnswer_ID();
            }

            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSingleAnswer(answer, question.getAnswerList());
                    mQuestionAnsweredListener.onQuestionAnswered();
                }
            });

            answerButton.setLayoutParams(answerParams);
            answerGroup.addView(answerButton);
        }

        if (checkedItem != -1) {
            answerGroup.check(checkedItem);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        answerGroup.setLayoutParams(params);

        mAnswerLayout.addView(answerGroup, params);
    }

    private void setMultiAnswer(Answer selectedAnswer, List<Answer> answerList) {
        for (Answer answer : answerList) {
            if (answer.getAnswerOfUser() == null) {
                answer.setAnswerOfUser("false");
            }
        }
    }

    private void setSingleAnswer(Answer selectedAnswer, List<Answer> answerList) {
        for (Answer answer : answerList) {
            if (answer.equals(selectedAnswer)) {
                answer.setAnswerOfUser("true");
            } else {
                answer.setAnswerOfUser("false");
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnQuestionAnsweredListener) {
            mQuestionAnsweredListener = (OnQuestionAnsweredListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBeginQuestionGroupListener");
        }

        if (context instanceof OnDisplayQuestionGroupListener) {
            mDisplayQuestionGroupListener = (OnDisplayQuestionGroupListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDisplayQuestionGroupListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mQuestionAnsweredListener = null;
        mDisplayQuestionGroupListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        mQuestionView.clearCache(true);
        mQuestionView.loadData("", "text/html; charset=utf-8", "UTF-8");
    }

    public interface OnQuestionAnsweredListener {
        void onQuestionAnswered();
    }

    public interface OnDisplayQuestionGroupListener {
        void onDisplayQuestionGroupList();
    }
}