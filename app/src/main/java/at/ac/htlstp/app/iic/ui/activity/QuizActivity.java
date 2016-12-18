package at.ac.htlstp.app.iic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.controller.QuizController;
import at.ac.htlstp.app.iic.error.ErrorHandler;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.Answer;
import at.ac.htlstp.app.iic.model.Question;
import at.ac.htlstp.app.iic.model.QuestionGroup;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.model.QuizSessionQuiz;
import at.ac.htlstp.app.iic.ui.fragment.QuestionFragment;
import at.ac.htlstp.app.iic.ui.fragment.QuestionGroupFragment;
import at.ac.htlstp.app.iic.ui.fragment.QuestionGroupListFragment;
import at.alexnavratil.navhelper.web.WebViewFullscreenSupport;
import butterknife.Bind;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity implements QuestionGroupListFragment.OnQuestionGroupSelectedListener,
        QuestionGroupFragment.OnBeginQuestionGroupListener,
        QuestionFragment.OnDisplayQuestionGroupListener,
        QuestionFragment.OnQuestionAnsweredListener,
        WebViewFullscreenSupport {
    public static final String KEY_QUIZ_ID = "quizId";
    public static final String STATE_START_SECONDS = "start_seconds";
    public static final String STATE_QUIZ_SESSION = "quiz_session";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.textRemainingTime)
    TextView mRemainingTextView;

    @Bind(R.id.btnShowQuestionGroup)
    Button mShowQuestionGroupsButton;

    @Bind(R.id.btnSubmitQuiz)
    Button mSubmitQuizButton;

    @Bind(R.id.submitRelativeLayout)
    RelativeLayout mSubmitRelativeLayout;

    @Bind(R.id.remainingTimeLabel)
    TextView mRemainingTimeView;

    private boolean overLimit = false;

    private int mQuizId;
    private CocoLib mCocoLib;
    private QuizController mQuizController;

    private QuizSessionQuiz mQuizSession;

    private Long startSeconds;

    /**
     * Fullscreen video variables
     */
    //Stores the custom view passed back by the WebChromeClient
    private View mCustomView;
    //Stores the main content view of the activity
    private View mContentView;
    //Container in which to place the custom view.
    private FrameLayout mCustomViewContainer;
    //Stores the CustomViewCallback interface.
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        if (getIntent().hasExtra(KEY_QUIZ_ID)) {
            mQuizId = getIntent().getIntExtra(KEY_QUIZ_ID, -1);
        } else {
            finish();
        }

        mShowQuestionGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragmentChooser();
            }
        });

        mSubmitQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitQuiz();
            }
        });

        mCocoLib = CocoLibSingleton.getInstance(this);
        mQuizController = mCocoLib.create(QuizController.class);

        loadQuizSession();
    }

    private void submitQuiz() {
        final MaterialDialog forceSubmitDialog = new MaterialDialog.Builder(QuizActivity.this)
                .title(R.string.quiz_submition)
                .content(R.string.quiz_submitting)
                .progress(true, 0)
                .cancelable(false)
                .show();

        final Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                submitQuiz();
            }
        };

        mQuizController.submitQuiz(mQuizSession.getSession_ID(), mQuizSession).setResultHandler(new IICResultHandler<QuizResult>(this, tryAgainAction) {
            @Override
            public void onSuccess(QuizResult param) {
                forceSubmitDialog.dismiss();

                Intent resultIntent = new Intent(QuizActivity.this, QuizResultActivity.class);
                resultIntent.putExtra(QuizResultActivity.KEY_QUIZ_SESSION_ID, mQuizSession.getSession_ID());
                startActivity(resultIntent);

                QuizActivity.this.finish();
            }

            @Override
            public boolean onError(IICError error) {
                forceSubmitDialog.dismiss();

                error.setMessage(getString(R.string.error_quiz_submit));
                ErrorHandler.handleError(QuizActivity.this, error, true, tryAgainAction);

                return true;
            }
        });
    }

    private void loadQuizSession() {
        final MaterialDialog progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.quiz_starting_title)
                .content(R.string.quiz_starting)
                .cancelable(false)
                .progress(true, 0)
                .show();

        mQuizController.startQuiz(mQuizId).setResultHandler(new IICResultHandler<QuizSessionQuiz>(this) {
            @Override
            public void onSuccess(QuizSessionQuiz param) {
                progressDialog.dismiss();
                mQuizSession = param;

                new MaterialDialog.Builder(QuizActivity.this)
                        .title(R.string.quiz_starting_title)
                        .content(R.string.quiz_loaded_pause)
                        .cancelable(false)
                        .positiveText(R.string.start_quiz)
                        .negativeText(R.string.cancel_quiz)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mQuizSession.setAppStartStamp(new Date());
                                initUserInterface();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                QuizActivity.this.finish();
                            }
                        })
                        .show();
            }

            @Override
            public boolean onError(IICError error) {
                progressDialog.dismiss();

                ErrorHandler.handleError(QuizActivity.this, error, true, new Runnable() {
                    @Override
                    public void run() {
                        loadQuizSession();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        QuizActivity.this.finish();
                    }
                });

                return true;
            }
        });
    }

    private void initUserInterface() {
        mToolbar.setSubtitle(mQuizSession.getName());

        initTimeRemaining();
        showFragmentChooser();
    }

    private void initTimeRemaining() {
        startSeconds = System.currentTimeMillis() / 1000;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (!overLimit) {
                    long currentSeconds = System.currentTimeMillis() / 1000;
                    long secondsSpent = currentSeconds - startSeconds;

                    final long timeRemaining = mQuizSession.getMaxSeconds() - secondsSpent;

                    if (timeRemaining >= 0) {

                        QuizActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRemainingTextView.setText(formatTimeRemaining((int) timeRemaining));
                            }
                        });
                    } else {
                        overLimit = true;
                        QuizActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRemainingTimeView.setText(R.string.exceeded_time);

                                mRemainingTimeView.setTextColor(ContextCompat.getColor(QuizActivity.this, android.R.color.holo_red_light));
                                mRemainingTextView.setTextColor(ContextCompat.getColor(QuizActivity.this, android.R.color.holo_red_light));
                            }
                        });

                        run(); //render time again without waiting one second
                    }
                } else {
                    long currentSeconds = System.currentTimeMillis() / 1000;
                    long secondsSpent = currentSeconds - startSeconds;

                    final long timeRemaining = secondsSpent - mQuizSession.getMaxSeconds();

                    QuizActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRemainingTextView.setText(formatTimeRemaining((int) timeRemaining));
                        }
                    });
                }
            }
        };

        new Timer().scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private String formatTimeRemaining(int secondsRemaining) {
        String output = "";

        int hours = secondsRemaining / 3600;
        int minutes = (secondsRemaining - hours * 3600) / 60;
        int seconds = secondsRemaining - (hours * 3600) - (minutes * 60);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void showFragmentChooser() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        QuestionGroupListFragment fragment = new QuestionGroupListFragment();
        fragment.setQuestionGroupList(mQuizSession.getQuestionGroupList());

        fragmentTransaction.replace(R.id.quizFrameLayout, fragment);

        if (fragmentManager.getFragments() != null) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.commit();
    }

    private void showFragmentQuestionGroup(QuestionGroup group) {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        QuestionGroupFragment fragment = new QuestionGroupFragment();
        fragment.setQuestionGroup(group);

        fragmentTransaction.replace(R.id.quizFrameLayout, fragment);

        if (fragmentManager.getFragments() != null) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.commit();
    }

    private void showFragmentQuestion(QuestionGroup group) {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        QuestionFragment fragment = new QuestionFragment();
        fragment.setQuestionGroup(group);

        fragmentTransaction.replace(R.id.quizFrameLayout, fragment);

        if (fragmentManager.getFragments() != null) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.commit();
    }

    private void cancelQuiz() {
        new MaterialDialog.Builder(this)
                .title(R.string.quiz_cancelation)
                .content(R.string.do_you_want_to_cancel_the_quiz)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        QuizActivity.this.finish();
                    }
                })
                .negativeText(R.string.no)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelQuiz();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelQuiz();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onQuestionGroupSelected(QuestionGroup group) {
        showFragmentQuestionGroup(group);
    }

    /**
     * FULLSCREEN VIDEO HELPER CODE
     * <p/>
     * special thanks to: http://stackoverflow.com/a/15127046/681493
     */

    @Override
    public void onBeginQuestionGroup(QuestionGroup group) {
        showFragmentQuestion(group);
    }

    @Override
    public void onDisplayQuestionGroupList() {
        showFragmentChooser();
    }

    @Override
    public void onQuestionAnswered() {
        int answeredQuizGroups = 0;
        for (QuestionGroup group : mQuizSession.getQuestionGroupList()) {
            int answeredQuestions = 0;
            for (Question question : group.getQuestionList()) {
                for (Answer answer : question.getAnswerList()) {
                    if (answer.getAnswerOfUser() != null) {
                        answeredQuestions++;
                        break;
                    }
                }
            }

            if (answeredQuestions == group.getQuestionList().size()) {
                answeredQuizGroups++;
            }
        }

        /**
         * IF all Questions of the quiz answered THEN show submit button
         */
        if (answeredQuizGroups == mQuizSession.getQuestionGroupList().size()) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSubmitRelativeLayout.getLayoutParams();

            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
            params.height = height;

            mSubmitRelativeLayout.setVisibility(View.VISIBLE);

            mSubmitRelativeLayout.setLayoutParams(params);
            mSubmitRelativeLayout.requestLayout();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCocoLib != null)
            mCocoLib.cancelAll();
    }

    /**
     * Method to mirror onShowCustomView from the WebChrome client, allowing WebViews in a Fragment
     * to show custom views.
     *
     * @param view     - The custom view.
     * @param callback - The callback interface for the custom view.
     */
    public void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        //If there's already a custom view, this is a duplicate call, and we should
        // terminate the new view, then bail out.
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        //Create a reusable set of FrameLayout.LayoutParams
        FrameLayout.LayoutParams fullscreenParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        //Save the drawer view into an instance variable, then hide it.
        mContentView = findViewById(R.id.quiz_layout);
        mContentView.setVisibility(View.GONE);

        //Create a new custom view container
        mCustomViewContainer = new FrameLayout(QuizActivity.this);
        mCustomViewContainer.setLayoutParams(fullscreenParams);
        mCustomViewContainer.setBackgroundResource(android.R.color.black);

        //Set view to instance variable, then add to container.
        mCustomView = view;
        view.setLayoutParams(fullscreenParams);
        mCustomViewContainer.addView(mCustomView);
        mCustomViewContainer.setVisibility(View.VISIBLE);

        //Save the callback an instance variable.
        mCustomViewCallback = callback;

        //Hide the action bar
        getSupportActionBar().hide();

        //Set the custom view container as the activity's content view.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(mCustomViewContainer);
    }

    /**
     * Method to mirror onShowCustomView from the WebChrome client, allowing WebViews in a Fragment
     * to hide custom views.
     */
    public void hideCustomView() {
        if (mCustomView == null) {
            //Nothing to hide - return.
            return;
        } else {
            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            mCustomViewContainer.removeView(mCustomView);
            mCustomViewContainer.setVisibility(View.GONE);
            mCustomViewCallback.onCustomViewHidden();
            mCustomView = null;

            // Show the ActionBar
            getSupportActionBar().show();

            // Show the content view.
            mContentView.setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(mContentView);
        }
    }
}
