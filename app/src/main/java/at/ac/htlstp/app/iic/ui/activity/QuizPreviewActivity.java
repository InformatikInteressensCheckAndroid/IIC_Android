package at.ac.htlstp.app.iic.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.adapter.QuizResultViewPagerAdapter;
import at.ac.htlstp.app.iic.controller.QuizController;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.Quiz;
import at.ac.htlstp.app.iic.ui.fragment.QuizPreviewDetailFragment;
import at.ac.htlstp.app.iic.ui.fragment.QuizPreviewInformationFragment;
import at.alexnavratil.navhelper.web.WebViewFullscreenSupport;
import butterknife.Bind;
import butterknife.ButterKnife;

public class QuizPreviewActivity extends AppCompatActivity implements WebViewFullscreenSupport {
    public static final String KEY_QUIZ_ID = "quizid";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.previewCollapsingToolbarLayout)
    CollapsingToolbarLayout mPreviewCollapsingToolbarLayout;

    @Bind(R.id.btnStartQuiz)
    Button mStartQuiz;

    @Bind(R.id.quizPreviewViewPager)
    ViewPager mViewPager;

    @Bind(R.id.quizPreviewTabLayout)
    TabLayout mQuizPreviewTabLayout;

    private int mQuizId;
    private CocoLib mCocoLib;
    private QuizController mQuizController;
    private Quiz mQuiz;

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
        setContentView(R.layout.activity_quiz_preview);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        if (getIntent().hasExtra(KEY_QUIZ_ID)) {
            mQuizId = getIntent().getIntExtra(KEY_QUIZ_ID, -1);
        } else {
            finish();
        }

        mCocoLib = CocoLibSingleton.getInstance(this);
        mQuizController = mCocoLib.create(QuizController.class);

        mPreviewCollapsingToolbarLayout.setTitle("...");

        loadQuiz();

        mStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quizIntent = new Intent(QuizPreviewActivity.this, QuizActivity.class);
                quizIntent.putExtra(QuizActivity.KEY_QUIZ_ID, mQuizId);
                startActivity(quizIntent);
                finish();
            }
        });
    }

    private void loadQuiz() {
        mQuizController.getQuizzes().setResultHandler(new IICResultHandler<List<Quiz>>(this) {
            @Override
            public void onCacheResult(List<Quiz> param) {
                mQuiz = filterQuiz(param);
                if (mQuiz == null) {
                    MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(QuizPreviewActivity.this)
                            .title(R.string.error)
                            .content(R.string.quiz_not_existing)
                            .positiveText(R.string.OK)
                            .dismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    QuizPreviewActivity.this.finish();
                                }
                            });

                    dialogBuilder.show();
                } else {
                    mPreviewCollapsingToolbarLayout.setTitle(mQuiz.getName());
                    initViewPager();
                    mQuizPreviewTabLayout.setupWithViewPager(mViewPager);
                }
            }

            @Override
            public void onSuccess(List<Quiz> param) {
                //do nothing, only react when onCacheResult is called because Quiz must exist already
            }

            @Override
            public boolean onError(IICError error) {
                return true;
            }
        });
    }

    private void initViewPager() {
        QuizResultViewPagerAdapter adapter = new QuizResultViewPagerAdapter(getSupportFragmentManager());

        /**
         * fragments need to retain the instance because the quizResult won't be passed after orientation change => NullPointerException
         * Arguments should be used but currently not possible with RealmObjects :(
         */
        QuizPreviewDetailFragment detailFragment = QuizPreviewDetailFragment.getInstance(mQuiz);
        detailFragment.setRetainInstance(true);

        QuizPreviewInformationFragment informationFragment = QuizPreviewInformationFragment.getInstance(mQuiz);
        informationFragment.setRetainInstance(true);

        adapter.addFragment(detailFragment, getString(R.string.details));
        adapter.addFragment(informationFragment, getString(R.string.information));

        mViewPager.setAdapter(adapter);
    }

    private Quiz filterQuiz(List<Quiz> quizList) {
        for (Quiz q : quizList) {
            if (q.getQuiz_ID() == mQuizId) {
                return q;
            }
        }
        return null;
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
        mCustomViewContainer = new FrameLayout(QuizPreviewActivity.this);
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
