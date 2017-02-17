package at.ac.htlstp.app.iic.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.adapter.QuizResultAdapter;
import at.ac.htlstp.app.iic.controller.QuizController;
import at.ac.htlstp.app.iic.error.ErrorHandler;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.ui.activity.MainActivity;
import at.alexnavratil.navhelper.data.RecyclerViewAdvanced;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexnavratil on 28/12/15.
 */
public class QuizResultListFragment extends IICFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.quizListRecycler)
    RecyclerViewAdvanced mQuizListRecycler;

    @Bind(R.id.quizResultListEmptyView)
    TextView emptyView;

    @Bind(R.id.quizResultRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    private CocoLib mCocoLib;
    private QuizController mQuizController;

    private List<QuizResult> mQuizResultList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mCocoLib = CocoLibSingleton.getInstance(getContext());
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.quiz_result_list_fragment, container, false);

        ButterKnife.bind(this, v);

        super.title = getContext().getString(R.string.quiz_results);
        super.action_key = MainActivity.FRAGMENT_KEY_QUIZ_RESULT_LIST;

        //mCocoLib = CocoLibSingleton.getInstance(getContext());
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute();

        mQuizController = mCocoLib.create(QuizController.class);

        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        refreshLayout.setOnRefreshListener(this);

        mQuizListRecycler.setEmptyView(emptyView);

        renderQuizResults(); //setting adapter with empty list to work around error message: E/RecyclerView: No adapter attached; skipping layout

        //postpone loading by 200ms because realms findAllAsync method currently blocks some milliseconds (maybe a bug of realm) which results in stuttering
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadQuizResults(false);
            }
        }, 200);

        return v;
    }

    /**
     * @param waitForResync when true the refresh listener refreshes till all data is synced from the server (cache db gets ignored)
     */
    private void loadQuizResults(final boolean waitForResync) {
        refreshLayout.setRefreshing(true);

        mQuizController.getFinishedQuizzes().setResultHandler(new IICResultHandler<List<QuizResult>>(this.getContext()) {
            @Override
            public void onCacheResult(List<QuizResult> param) {
                if (!waitForResync && param.size() > 0) {
                    onSuccess(param);
                }
            }

            @Override
            public void onSuccess(List<QuizResult> param) {
                refreshLayout.setRefreshing(false);
                mQuizResultList = param;
                if (getContext() != null) {
                    renderQuizResults();
                }
            }

            @Override
            public boolean onError(IICError error) {
                refreshLayout.setRefreshing(false);
                if (waitForResync) {
                    ErrorHandler.handleError(QuizResultListFragment.this.getContext(), error, true, new Runnable() {
                        @Override
                        public void run() {
                            loadQuizResults(true);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

    private void renderQuizResults() {
        if (ErrorHandler.isOfflineMode()) {
            emptyView.setText(R.string.offline_mode_no_quizresults_available);
        } else {
            emptyView.setText(R.string.no_quiz_results);
        }

        QuizResultAdapter quizResultAdapter = new QuizResultAdapter(getContext(), mQuizResultList);
        mQuizListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuizListRecycler.setAdapter(quizResultAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCocoLib != null)
            mCocoLib.cancelAll();
    }

    @Override
    public void onRefresh() {
        loadQuizResults(true);
    }
}
