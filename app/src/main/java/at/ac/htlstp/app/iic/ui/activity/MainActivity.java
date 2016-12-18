package at.ac.htlstp.app.iic.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.cocolib.network.VolleySingleton;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.controller.UserController;
import at.ac.htlstp.app.iic.error.ErrorHandler;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.Answer;
import at.ac.htlstp.app.iic.model.Country;
import at.ac.htlstp.app.iic.model.FinishedQuizSessionAnswer;
import at.ac.htlstp.app.iic.model.FinishedQuizSessionQuestion;
import at.ac.htlstp.app.iic.model.Language;
import at.ac.htlstp.app.iic.model.Question;
import at.ac.htlstp.app.iic.model.QuestionGroup;
import at.ac.htlstp.app.iic.model.Quiz;
import at.ac.htlstp.app.iic.model.QuizCategory;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.model.QuizSessionQuiz;
import at.ac.htlstp.app.iic.model.School;
import at.ac.htlstp.app.iic.model.User;
import at.ac.htlstp.app.iic.model.UserClass;
import at.ac.htlstp.app.iic.model.UserType;
import at.ac.htlstp.app.iic.ui.fragment.AccountFragment;
import at.ac.htlstp.app.iic.ui.fragment.IICFragment;
import at.ac.htlstp.app.iic.ui.fragment.OverviewFragment;
import at.ac.htlstp.app.iic.ui.fragment.QuizListFragment;
import at.ac.htlstp.app.iic.ui.fragment.QuizResultListFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        IICFragment.OnFragmentInteractionListener,
        IICFragment.OnFragmentDisplayedListener,
        AccountFragment.OnActionButtonClickedListener,
        AccountFragment.OnChangePasswordListener {
    public static final String FRAGMENT_KEY_OVERVIEW = "overview";
    public static final String FRAGMENT_KEY_QUIZ_LIST = "quizlist";
    public static final String FRAGMENT_KEY_QUIZ_RESULT_LIST = "quizresultlist";
    public static final String FRAGMENT_KEY_ACCOUNT_SETTINGS = "account_settings";

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private ActionBar mActionBar;
    private ActionBarDrawerToggle mDrawerToggle;

    private User mCurrentUser;
    private Realm mRealm;

    private View mHeaderView;
    private TextView mNameOfUserView;

    private CocoLib mCocoLib;
    private UserController mUserController;

    public static boolean logout(Context context) {
        VolleySingleton.getInstance(context).getCookieStore().removeAll(); //remove all cookies (clear session)

        Realm realm = Realm.getInstance(context);

        try {
            realm.beginTransaction();

            realm.clear(Answer.class);
            realm.clear(FinishedQuizSessionAnswer.class);
            realm.clear(FinishedQuizSessionQuestion.class);
            realm.clear(Language.class);
            realm.clear(Question.class);
            realm.clear(QuestionGroup.class);
            realm.clear(Quiz.class);
            realm.clear(QuizCategory.class);
            realm.clear(QuizResult.class);
            realm.clear(QuizSessionQuiz.class);
            realm.clear(School.class);
            realm.clear(User.class);
            realm.clear(UserClass.class);
            realm.clear(UserType.class);
            realm.clear(Country.class);

            realm.commitTransaction();

            Intent loginIntent = new Intent(context, LoginActivity.class);
            context.startActivity(loginIntent);

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(context, R.string.error_during_logout, Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iic_main);

        mRealm = Realm.getInstance(this);

        initUserInterface();

        initBeforeAuthentication();

        authenticate();
    }

    public void initUserInterface() {
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mActionBar = this.getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); //disables hamburger animation
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        /**
         * Only select the first item if there's no other item selected (no fragment is displayed)
         */
        if (getSupportFragmentManager().getFragments() == null) {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.drawer_overview));
        }
    }

    private void initBeforeAuthentication() {
        mCurrentUser = mRealm.where(User.class).equalTo("isLocalUser", true).findFirst();

        mCurrentUser.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                initNavigationDrawer();
            }
        });

        initNavigationDrawer();

        mCocoLib = CocoLibSingleton.getInstance(this);

        mUserController = mCocoLib.create(UserController.class);
    }

    private void initNavigationDrawer() {
        mHeaderView = mNavigationView.getHeaderView(0);
        mNameOfUserView = (TextView) mHeaderView.findViewById(R.id.name_of_user);

        mNameOfUserView.setText(mCurrentUser.getFirstName() + " " + mCurrentUser.getLastName());
    }

    private void initAfterAuthentication(User param) {

    }

    private void authenticate() {
        mUserController.authenticate(mCurrentUser).setResultHandler(new IICResultHandler<User>(this) {
            @Override
            public void onSuccess(User param) {
                initAfterAuthentication(param);
            }

            @Override
            public boolean onError(IICError error) {
                return false;
            }
        });
    }

    public void openFragment(String key, boolean onlySetMenuItem) {
        IICFragment fragment = null;

        switch (key) {
            case FRAGMENT_KEY_OVERVIEW:
                if (!onlySetMenuItem) fragment = new OverviewFragment();
                setCheckedMenuItem(R.id.drawer_overview);
                break;
            case FRAGMENT_KEY_QUIZ_LIST:
                if (!onlySetMenuItem) fragment = new QuizListFragment();
                setCheckedMenuItem(R.id.drawer_quizzes);
                break;
            case FRAGMENT_KEY_QUIZ_RESULT_LIST:
                if (!onlySetMenuItem) fragment = new QuizResultListFragment();
                setCheckedMenuItem(R.id.drawer_quiz_result);
                break;
            case FRAGMENT_KEY_ACCOUNT_SETTINGS:
                if (!onlySetMenuItem)
                    fragment = AccountFragment.initialize(mCurrentUser.getUser_ID());
                setCheckedMenuItem(R.id.drawer_account_settings);
                break;
        }

        if (!onlySetMenuItem) {
            final FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.content_frame, fragment);

            if (fragmentManager.getFragments() != null) {
                fragmentTransaction.addToBackStack(null);
            }

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            fragmentTransaction.commit();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_debug:
                Intent intentDebug = new Intent(this, DebugActivity.class);
                startActivity(intentDebug);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setCheckedMenuItem(@IdRes int itemId) {
        mNavigationView.setCheckedItem(mNavigationView.getMenu().findItem(itemId).getItemId());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mNavigationView.setCheckedItem(item.getItemId());

        switch (item.getItemId()) {
            case R.id.drawer_overview:
                openFragment(FRAGMENT_KEY_OVERVIEW, false);
                break;
            case R.id.drawer_quizzes:
                openFragment(FRAGMENT_KEY_QUIZ_LIST, false);
                break;
            case R.id.drawer_quiz_result:
                openFragment(FRAGMENT_KEY_QUIZ_RESULT_LIST, false);
                break;
            case R.id.drawer_account_settings:
                openFragment(FRAGMENT_KEY_ACCOUNT_SETTINGS, false);
                break;
            case R.id.drawer_switch_user:
                if (logout(this)) {
                    finish();
                }
                break;
            case R.id.drawer_license:
                Intent licenseIntent = new Intent(this, LicenseActivity.class);
                startActivity(licenseIntent);
                break;
        }

        mDrawerLayout.closeDrawers();

        return true;
    }

    @Override
    public void onFragmentInteraction(String fragment_key) {
        openFragment(fragment_key, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRealm.isClosed()) {
            mRealm = Realm.getInstance(this);
        }

        initBeforeAuthentication();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCocoLib != null)
            mCocoLib.cancelAll();
        mRealm.close();
    }

    @Override
    public void onFragmentDisplayed(String title, String action_key) {
        mToolbar.setSubtitle(title.toUpperCase());
        openFragment(action_key, true);
    }

    @Override
    public void onActionButtonClicked(final User user) {
        initBeforeAuthentication();

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content(R.string.refreshing_user_data)
                .progress(true, 0)
                .cancelable(false)
                .show();

        Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                onActionButtonClicked(user);
            }
        };

        mUserController.updateUser(user).setResultHandler(new IICResultHandler<String>(this, tryAgainAction) {
            @Override
            public void onSuccess(String param) {
                dialog.dismiss();
                openFragment(FRAGMENT_KEY_OVERVIEW, false);
                Toast.makeText(MainActivity.this, param, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onError(IICError error) {
                dialog.dismiss();
                return false;
            }
        });
    }

    @Override
    public void onChangePassword(final LinkedHashMap<String, Object> passwordData, final MaterialDialog passwordDialog) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content(R.string.refreshing_user_data)
                .progress(true, 0)
                .cancelable(false)
                .show();

        Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                onChangePassword(passwordData, passwordDialog);
            }
        };

        mUserController.changePassword(passwordData).setResultHandler(new IICResultHandler<String>(this, tryAgainAction) {
            @Override
            public void onSuccess(String param) {
                Toast.makeText(MainActivity.this, param, Toast.LENGTH_LONG).show();

                /**
                 * UPDATE local stored password to new changed password
                 */
                mRealm.beginTransaction();
                mCurrentUser.setPassword(passwordData.get("NewPassword").toString());
                mRealm.commitTransaction();

                dialog.dismiss();
                passwordDialog.dismiss();
            }

            @Override
            public boolean onError(IICError error) {
                dialog.dismiss();

                if (error.getErrorId().equals(IICError.E_API_ERROR)) {
                    ErrorHandler.showCustomErrorDialog(MainActivity.this, getString(R.string.error), error.getMessage());

                    return true;
                }

                return false;
            }
        });
    }
}
