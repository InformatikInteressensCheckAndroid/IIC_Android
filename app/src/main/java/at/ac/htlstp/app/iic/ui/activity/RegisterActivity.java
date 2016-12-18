package at.ac.htlstp.app.iic.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.controller.UserController;
import at.ac.htlstp.app.iic.error.ErrorHandler;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.User;
import at.ac.htlstp.app.iic.ui.fragment.AccountFragment;
import at.ac.htlstp.app.iic.ui.fragment.IICFragment;
import io.realm.Realm;

public class RegisterActivity extends AppCompatActivity
        implements AccountFragment.OnActionButtonClickedListener,
        AccountFragment.OnChangePasswordListener,
        IICFragment.OnFragmentDisplayedListener,
        IICFragment.OnFragmentInteractionListener {

    private CocoLib mCocoLib;
    private UserController mUserController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCocoLib = CocoLibSingleton.getInstance(this);
        mUserController = mCocoLib.create(UserController.class);

        initUI();
    }

    private void initUI() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AccountFragment fragment = AccountFragment.initialize(-1);
        //fragment.setRetainInstance(true);

        fragmentTransaction.replace(R.id.contentFrameLayout, fragment);

        fragmentTransaction.commit();
    }

    @Override
    public void onActionButtonClicked(final User user) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content(R.string.refreshing_user_data)
                .progress(true, 0)
                .cancelable(false)
                .show();

        //Log.i("IIC", "before password: " + user.getPassword());

        mUserController.register(user).setResultHandler(new IICResultHandler<String>(this) {
            @Override
            public void onSuccess(String param) {
                dialog.dismiss();

                user.setLocalUser(true);
                Realm realm = Realm.getInstance(RegisterActivity.this);
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(user);
                realm.commitTransaction();

                Toast.makeText(RegisterActivity.this, param, Toast.LENGTH_LONG);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public boolean onError(IICError error) {
                dialog.dismiss();

                if (error.getErrorId().equals(IICError.E_API_ERROR)) {
                    ErrorHandler.showCustomErrorDialog(RegisterActivity.this, getString(R.string.error), error.getMessage());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChangePassword(LinkedHashMap<String, Object> passwordData, MaterialDialog dialog) {
        //do nothing
    }

    @Override
    public void onFragmentDisplayed(String title, String action_key) {
        //do nothing
    }

    @Override
    public void onFragmentInteraction(String fragment_key) {
        //do nothing
    }
}
