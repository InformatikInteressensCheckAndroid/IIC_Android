package at.ac.htlstp.app.iic.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.model.FinishedQuizSessionAnswer;
import at.ac.htlstp.app.iic.model.FinishedQuizSessionQuestion;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.ui.activity.QuizResultActivity;
import at.ac.htlstp.app.iic.util.StringUtils;
import at.alexnavratil.navhelper.web.ReadabilityWebView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by alexnavratil on 28/12/15.
 */
public class QuizResultQuestionFragment extends Fragment {
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

    @Bind(R.id.showSolutionButton)
    Button showSolutionButton;

    private QuizResult mQuizResult;

    private int mQuestionIndex = 0;

    private boolean solutionMode = false;

    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    public static QuizResultQuestionFragment getInstance(QuizResult quizResult) {
        QuizResultQuestionFragment instance = new QuizResultQuestionFragment();
        instance.mQuizResult = quizResult;

        return instance;
    }

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

        mQuestionView.enableFullscreenSupport((QuizResultActivity) getActivity());

        showSolutionButton.setVisibility(View.VISIBLE);

        renderQuestion();

        return v;
    }

    @OnClick(R.id.showSolutionButton)
    public void onSolutionButtonClick() {
        solutionMode = !solutionMode;
        renderAnswer();
    }

    private void renderQuestion() {
        solutionMode = false; //just show the user's answer

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mQuestionView.setVisibility(View.INVISIBLE);
        }

        if (mQuestionIndex >= 0 && mQuizResult.getQuestions().size() > mQuestionIndex) {
            FinishedQuizSessionQuestion currentQuestion = mQuizResult.getQuestions().get(mQuestionIndex);

            mQuestionView.loadData(currentQuestion.getQuestion().getQ_HTML());

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

            mQuestionNumberView.setText((mQuestionIndex + 1) + " / " + mQuizResult.getQuestions().size());

            renderAnswer();

            /**
             * IF last question THEN change text of nextQuestionButton
             */
            if (mQuestionIndex == (mQuizResult.getQuestions().size() - 1)) {
                mNextButton.setEnabled(false);
            } else {
                mNextButton.setEnabled(true);
            }

            if (mQuestionIndex == 0) {
                mPreviousButton.setEnabled(false);
            } else {
                mPreviousButton.setEnabled(true);
            }
        }
    }

    private void renderAnswer() {
        if (solutionMode) {
            showSolutionButton.setText(R.string.hide_solution);
        } else {
            showSolutionButton.setText(getContext().getString(R.string.show_solution));
        }

        FinishedQuizSessionQuestion question = mQuizResult.getQuestions().get(mQuestionIndex);

        mAnswerLayout.removeAllViews();

        switch (question.getQuestion().getQuestionType_ID()) {
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

    private void renderTextAnswer(final FinishedQuizSessionQuestion question) {
        final AppCompatEditText answerText = new AppCompatEditText(getContext());
        answerText.setEnabled(false);

        String AnswerOfUser = question.getAnswers().get(0).getAnswerOfUser();

        answerText.setText(AnswerOfUser);

        if (!solutionMode) {
            if (AnswerOfUser.equals(question.getAnswers().get(0).getAnswer().getAnswer())) {
                answerText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
            } else {
                answerText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
            }
        } else {
            final List<String> answerList = new ArrayList<>();

            Observable.from(question.getAnswers())
                    .filter(new Func1<FinishedQuizSessionAnswer, Boolean>() {
                        @Override
                        public Boolean call(FinishedQuizSessionAnswer finishedQuizSessionAnswer) {
                            return finishedQuizSessionAnswer.getAnswer().isCorrect();
                        }
                    })
                    .map(new Func1<FinishedQuizSessionAnswer, String>() {
                        @Override
                        public String call(FinishedQuizSessionAnswer finishedQuizSessionAnswer) {
                            return finishedQuizSessionAnswer.getAnswer().getAnswer();
                        }
                    })
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            answerList.add(s);
                        }
                    });

            ListAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, answerList);

            new MaterialDialog.Builder(getContext())
                    .title(R.string.solution)
                    .content(R.string.solutions_for_question)
                    .positiveText(R.string.OK)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .adapter(adapter, new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                        }
                    })
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            solutionMode = false;
                            renderAnswer();
                        }
                    })
                    .show();
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        answerText.setLayoutParams(params);

        mAnswerLayout.addView(answerText, params);
    }

    private void renderMultiAnswer(final FinishedQuizSessionQuestion question) {
        for (final FinishedQuizSessionAnswer answer : question.getAnswers()) {
            final AppCompatCheckBox answerCheckBox = new AppCompatCheckBox(getContext());
            answerCheckBox.setClickable(false);
            answerCheckBox.setText(answer.getAnswer().getAnswer());

            if (!solutionMode) {
                boolean checked = StringUtils.equals(answer.getAnswerOfUser(), "true");
                answerCheckBox.setChecked(checked);

                if (checked && checked == answer.getAnswer().isCorrect()) {
                    answerCheckBox.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
                } else if ((checked && checked != answer.getAnswer().isCorrect())) {
                    answerCheckBox.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
                } else if(!checked && answer.getAnswer().isCorrect()){
                    answerCheckBox.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
                }
            } else {
                answerCheckBox.setChecked(answer.getAnswer().isCorrect());
                if(answer.getAnswer().isCorrect()) {
                    answerCheckBox.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
                }
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 10;
            answerCheckBox.setLayoutParams(params);

            mAnswerLayout.addView(answerCheckBox, params);
        }
    }

    private void renderSingleAnswer(final FinishedQuizSessionQuestion question) {
        RadioGroup answerGroup = new RadioGroup(getContext());

        RadioGroup.LayoutParams answerParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        answerParams.bottomMargin = 10;

        int checkedItem = -1;

        for (final FinishedQuizSessionAnswer answer : question.getAnswers()) {
            AppCompatRadioButton answerButton = new AppCompatRadioButton(getContext());
            answerButton.setClickable(false);
            answerButton.setId(answer.getAnswer().getAnswer_ID());
            answerButton.setText(answer.getAnswer().getAnswer());

            if (!solutionMode) {
                boolean checked = StringUtils.equals(answer.getAnswerOfUser(), "true");
                if (checked) {
                    checkedItem = answer.getAnswer().getAnswer_ID();
                }

                //if the user has ticked the answer and it's correct
                if (checked && checked == answer.getAnswer().isCorrect()) {
                    answerButton.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
                } else if ((checked && checked != answer.getAnswer().isCorrect())) {
                    answerButton.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
                } else if(!checked && answer.getAnswer().isCorrect()){
                    answerButton.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
                }
            } else {
                if (answer.getAnswer().isCorrect()) {
                    checkedItem = answer.getAnswer().getAnswer_ID();
                    answerButton.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
                }
            }

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

    @Override
    public void onStop() {
        super.onStop();
        mQuestionView.clearCache(true);
        mQuestionView.loadData("", "text/html; charset=utf-8", "UTF-8");
    }
}