package at.ac.htlstp.app.iic.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.Timer;
import java.util.TimerTask;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.adapter.QuizResultViewPagerAdapter;
import at.ac.htlstp.app.iic.controller.QuizController;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.ui.fragment.QuizResultDetailFragment;
import at.ac.htlstp.app.iic.ui.fragment.QuizResultQuestionFragment;
import at.alexnavratil.navhelper.web.WebViewFullscreenSupport;
import butterknife.Bind;
import butterknife.ButterKnife;

public class QuizResultActivity extends AppCompatActivity implements WebViewFullscreenSupport {
    public static final String KEY_QUIZ_SESSION_ID = "quizsessionid";

    @Bind(R.id.mToolbar)
    Toolbar mToolbar;

    @Bind(R.id.quizResultTabLayout)
    TabLayout mTabLayout;

    @Bind(R.id.quizResultViewPager)
    ViewPager mViewPager;

    @Bind(R.id.resultCollapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.quizResultAppBar)
    AppBarLayout appBarLayout;

    @Bind(R.id.percent_progress)
    DonutProgress percentProgress;

    private int mQuizSessionId;
    private CocoLib mCocoLib;
    private QuizController mQuizController;
    private QuizResult mQuizResult;
    private boolean loaded = false;

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

    public static float easeOut(float t, float b, float c, float d) {
        return -c * (t /= d) * (t - 2) + b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(KEY_QUIZ_SESSION_ID)) {
            mQuizSessionId = getIntent().getIntExtra(KEY_QUIZ_SESSION_ID, -1);
        } else {
            finish();
        }

        mCocoLib = CocoLibSingleton.getInstance(this);
        mQuizController = mCocoLib.create(QuizController.class);

        percentProgress.setFinishedStrokeColor(Color.WHITE);
        percentProgress.setUnfinishedStrokeColor(ContextCompat.getColor(this, R.color.colorPrimary));
        percentProgress.setTextColor(Color.WHITE);
        percentProgress.setFinishedStrokeWidth(8);
        percentProgress.setUnfinishedStrokeWidth(8);

        init();
    }

    private void init() {
        Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                init();
            }
        };

        mQuizController.getFinishedQuizSession(mQuizSessionId).setResultHandler(new IICResultHandler<QuizResult>(this, tryAgainAction) {
            @Override
            public boolean onError(IICError error) {
                return false;
            }

            @Override
            public void onCacheResult(QuizResult param) {
                if (param != null) {
                    loaded = true;
                    mQuizResult = param;

                    mCollapsingToolbarLayout.setTitle(mQuizResult.getQuiz().getName());

                    initViewPager();
                    mTabLayout.setupWithViewPager(mViewPager);
                }
            }

            @Override
            public void onSuccess(QuizResult param) {
                if (!loaded) {
                    loaded = true;

                    mQuizResult = param;

                    mCollapsingToolbarLayout.setTitle(mQuizResult.getQuiz().getName());

                    initViewPager();
                    mTabLayout.setupWithViewPager(mViewPager);
                }
            }
        });
    }

    private void animatePercentProgress() {
        final Timer t = new Timer();

        final TimerTask task = new TimerTask() {
            private int i = 0;

            @Override
            public void run() {
                if (i < mQuizResult.getPercent()) {
                    final int progress = ++i;
                    QuizResultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            percentProgress.setProgress(progress);
                        }
                    });
                } else {
                    t.cancel();
                }
            }
        };

        t.scheduleAtFixedRate(task, 0, 25);
    }

    private void initViewPager() {
        QuizResultViewPagerAdapter adapter = new QuizResultViewPagerAdapter(getSupportFragmentManager());

        /**
         * fragments need to retain the instance because the quizResult won't be passed after orientation change => NullPointerException
         * Arguments should be used but currently not possible with RealmObjects :(
         */
        QuizResultDetailFragment detailFragment = QuizResultDetailFragment.getInstance(mQuizResult);
        detailFragment.setRetainInstance(true);

        QuizResultQuestionFragment questionFragment = QuizResultQuestionFragment.getInstance(mQuizResult);
        questionFragment.setRetainInstance(true);

        adapter.addFragment(detailFragment, getString(R.string.details));
        adapter.addFragment(questionFragment, getString(R.string.questions));

        mViewPager.setAdapter(adapter);

        animatePercentProgress();
    }

    public void showQuestions() {
        appBarLayout.setExpanded(false, true);
        mViewPager.setCurrentItem(1, true);
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
        mCustomViewContainer = new FrameLayout(QuizResultActivity.this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCocoLib != null)
            mCocoLib.cancelAll();
    }
}
