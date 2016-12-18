package at.ac.htlstp.app.iic.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.adapter.LanguageAdapter;
import at.ac.htlstp.app.iic.adapter.QuizAdapter;
import at.ac.htlstp.app.iic.adapter.QuizCategorySpinnerAdapter;
import at.ac.htlstp.app.iic.controller.LanguageController;
import at.ac.htlstp.app.iic.controller.QuizController;
import at.ac.htlstp.app.iic.db.UserDAO;
import at.ac.htlstp.app.iic.error.ErrorHandler;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.Language;
import at.ac.htlstp.app.iic.model.Quiz;
import at.ac.htlstp.app.iic.model.QuizCategory;
import at.ac.htlstp.app.iic.model.User;
import at.ac.htlstp.app.iic.ui.activity.MainActivity;
import at.alexnavratil.navhelper.data.RecyclerViewAdvanced;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexnavratil on 28/12/15.
 */
public class QuizListFragment extends IICFragment {
    @Bind(R.id.quizListRecycler)
    RecyclerViewAdvanced mQuizListRecycler;

    @Bind(R.id.quizCategorySpinner)
    Spinner mQuizCategorySpinner;

    @Bind(R.id.btnLanguage)
    ImageButton mLanguageButton;

    @Bind(R.id.quizListProgress)
    ProgressBar quizListProgress;

    @Bind(R.id.quizListEmptyView)
    TextView emptyView;

    private CocoLib mCocoLib;
    private QuizController mQuizController;
    private LanguageController mLanguageController;
    private User mCurrentUser;
    private Language mSelectedLanguage;

    private List<Quiz> mQuizList = new ArrayList<>();
    private List<QuizCategory> mQuizCategoryList = new ArrayList<>();
    private List<Language> mLanguageList = new ArrayList<>();
    private QuizCategory mSelectedQuizCategory = null;

    private LanguageAdapter mLanguageAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.quiz_list_fragment, container, false);

        ButterKnife.bind(this, v);

        super.title = getContext().getString(R.string.quizzes);
        super.action_key = MainActivity.FRAGMENT_KEY_QUIZ_LIST;

        mCocoLib = CocoLibSingleton.getInstance(getContext());
        mQuizController = mCocoLib.create(QuizController.class);
        mLanguageController = mCocoLib.create(LanguageController.class);
        mCurrentUser = UserDAO.getCurrentUser(getContext());

        mSelectedLanguage = mCurrentUser.getLanguage();

        mQuizCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedQuizCategory = mQuizCategoryList.get(position);
                loadQuizzes(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageSelectionDialog();
            }
        });

        mQuizListRecycler.setIndeterminateProgressBar(quizListProgress);
        mQuizListRecycler.setEmptyView(emptyView);

        mQuizListRecycler.setLoading(true);

        renderQuizCategories(); //setting adapter with empty list

        loadQuizCategories();

        renderQuizzes(); //setting adapter with empty list to work around error message: E/RecyclerView: No adapter attached; skipping layout

        return v;
    }

    private void loadQuizCategories() {
        Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                loadQuizCategories();
            }
        };

        mQuizListRecycler.setLoading(true);

        mQuizController.getQuizCategories(mSelectedLanguage.getLanguage_ID())
                .setResultHandler(new IICResultHandler<List<QuizCategory>>(getContext(), tryAgainAction) {
                    @Override
                    public void onCacheResult(List<QuizCategory> param) {
                        mQuizCategoryList = param;
                        renderQuizCategories();
                    }

                    @Override
                    public void onSuccess(List<QuizCategory> param) {
                        if (param.size() == 0) {
                            mQuizListRecycler.setLoading(false);
                        }
                        mQuizCategoryList = param;
                        renderQuizCategories();
                    }

                    @Override
                    public boolean onError(IICError error) {
                        return false;
                    }
                });
    }

    private void loadQuizzes(final boolean forceOnline) {
        //remove quizzes from other categories
        mQuizList.clear();
        renderQuizzes();

        final Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                loadQuizzes(true);
            }
        };

        //check for language with no quiz categories selected
        if (mSelectedQuizCategory != null)
            mQuizListRecycler.setLoading(true);

        mQuizController.getQuizzesOfCategory(mSelectedQuizCategory.getQuizCategory_Language_ID())
                .setResultHandler(new IICResultHandler<List<Quiz>>(this.getContext(), tryAgainAction) {
                    @Override
                    public void onCacheResult(List<Quiz> param) {
                        if (param.size() > 0) {
                            mQuizListRecycler.setLoading(false);
                            mQuizList = param;
                            renderQuizzes();
                        }
                    }

                    @Override
                    public void onSuccess(List<Quiz> param) {
                        mQuizListRecycler.setLoading(false);
                        if (mSelectedQuizCategory != null &&
                                param.size() > 0 &&
                                param.get(0).getQuizCategory_Language_ID() == mSelectedQuizCategory.getQuizCategory_Language_ID()) {
                            mQuizList = param;
                            renderQuizzes();
                        }
                    }

                    @Override
                    public boolean onError(IICError error) {
                        if (!forceOnline) {
                            return false;
                        }

                        ErrorHandler.handleError(getContext(), error, true, tryAgainAction);
                        return true;
                    }
                });
    }

    private void showLanguageSelectionDialog() {
        mLanguageAdapter = new LanguageAdapter(getContext(), mLanguageList);

        loadLanguages();

        MaterialDialog.Builder languageDialogBuilder = new MaterialDialog.Builder(getContext())
                .title(R.string.language_selection)
                .adapter(mLanguageAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        mSelectedLanguage = mLanguageList.get(which);

                        loadQuizCategories(); //load QuizCategories of selected language

                        mQuizList.clear(); //clear QuizList, it (might) contains still quizzes of other languages
                        renderQuizzes(); //render RecyclerView (with empty list)

                        mLanguageAdapter = null;

                        dialog.dismiss();
                    }
                });

        languageDialogBuilder.show();
    }

    private void loadLanguages() {
        Runnable tryAgainRunnable = new Runnable() {
            @Override
            public void run() {
                loadLanguages();
            }
        };

        mLanguageController.getLanguages().setResultHandler(new IICResultHandler<List<Language>>(getContext(), tryAgainRunnable) {
            @Override
            public void onCacheResult(List<Language> param) {
                onSuccess(param);
            }

            @Override
            public void onSuccess(List<Language> param) {
                mLanguageList = param;
                if (mLanguageAdapter != null) {
                    mLanguageAdapter.setLanguageList(mLanguageList);
                }
            }

            @Override
            public boolean onError(IICError error) {
                return false;
            }
        });
    }

    private void renderQuizCategories() {
        QuizCategorySpinnerAdapter adapter = new QuizCategorySpinnerAdapter(getContext(), mQuizCategoryList);
        mQuizCategorySpinner.setAdapter(adapter);
    }

    private void renderQuizzes() {
        if (ErrorHandler.isOfflineMode()) {
            emptyView.setText(R.string.offline_mode_no_quizzes_available);
        } else {
            emptyView.setText(R.string.no_quizzes_available);
        }

        QuizAdapter quizAdapter = new QuizAdapter(getContext(), mQuizList);
        mQuizListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuizListRecycler.setAdapter(quizAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCocoLib != null)
            mCocoLib.cancelAll();
    }
}
