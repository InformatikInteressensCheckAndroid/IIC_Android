package at.ac.htlstp.app.iic.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.cocolib.exception.ValidatorException;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.controller.UserController;
import at.ac.htlstp.app.iic.error.ErrorHandler;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {
    public static final int REGISTER_REQUEST_CODE = 1;
    @Bind(R.id.editUsername)
    EditText editUsername;
    @Bind(R.id.editPassword)
    EditText editPassword;
    @Bind(R.id.btn_password_reset)
    Button btnPasswordReset;
    @Bind(R.id.btn_register)
    Button btnRegister;
    @Bind(R.id.btn_login)
    Button btnLogin;
    private boolean loggedIn; //if false, onStop() removes all temp users.
    private CocoLib mCocoLib;
    private UserController mUserController;
    private Realm mRealm;

    private class MyAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            mCocoLib = CocoLibSingleton.getInstance(LoginActivity.this);
            return null;
        }
    }

    private class ResumeTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            mCocoLib = CocoLibSingleton.getInstance(LoginActivity.this);
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyAsyncTask asyncTask = new MyAsyncTask();
        asyncTask.execute();

        //new CocoLibSingleton().execute(LoginActivity.this);

        ButterKnife.bind(this);

        mRealm = Realm.getInstance(this);

        userExists(new Runnable() {
            @Override
            public void run() {
                mUserController = mCocoLib.create(UserController.class);

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final User tmpUser = new User();
                        tmpUser.setUserName(editUsername.getText().toString());
                        tmpUser.setPassword(editPassword.getText().toString());

                        if(tmpUser.getUserName().isEmpty() || tmpUser.getPassword().isEmpty()){
                            Toast.makeText(LoginActivity.this, "Gib bitte gültige Anmeldedaten ein!", Toast.LENGTH_LONG).show();
                        } else {
                            MaterialDialog.Builder progressBuilder = new MaterialDialog.Builder(LoginActivity.this)
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .content(R.string.signing_in);

                            final MaterialDialog progressDialog = progressBuilder.show();

                            APIResult<User> userResult = mUserController.authenticate(tmpUser);

                            userResult.setResultHandler(new IICResultHandler<User>(LoginActivity.this) {
                                @Override
                                public boolean onError(IICError error) {
                                    progressDialog.dismiss();

                                    if (error.getErrorId().equals(IICError.E_AUTH_ERROR)) {
                                        LoginActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new MaterialDialog.Builder(LoginActivity.this)
                                                        .title(R.string.auth_error)
                                                        .content(R.string.username_pwd_wrong)
                                                        .positiveText(R.string.OK)
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                            }
                                                        })
                                                        .show();
                                            }
                                        });
                                    } else if (error.getException() instanceof ValidatorException) {
                                        //do nothing, error gets thrown twice, it's a bug
                                    } else {
                                        error.setErrorId(IICError.E_NETWORK_ERROR);

                                        ErrorHandler.handleError(LoginActivity.this, error, true, new Runnable() {
                                            @Override
                                            public void run() {
                                                onClick(v);
                                            }
                                        });
                                    }

                                    return true;
                                }

                                @Override
                                public void onSuccess(User param) {
                                    Log.i("IIC", param.toString());
                                    progressDialog.dismiss();

                                    mRealm.beginTransaction();

                                    User currentUser = mRealm.where(User.class).equalTo("isLocalUser", true).findFirst();
                                    if (currentUser != null) { //don't know why this should be null, but it seems to sometimes throw a NullPointerException
                                        currentUser.setPassword(tmpUser.getPassword());
                                    }

                                    mRealm.commitTransaction();

                                    Toast.makeText(LoginActivity.this, String.format(getString(R.string.hello_user), param.getFirstName(), param.getLastName()), Toast.LENGTH_SHORT).show();
                                    switchToMainActivity();
                                }
                            });
                        }
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                switchToMainActivity();
            }
        });


    }

    @OnClick(R.id.btn_register)
    public void onRegisterClick() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerIntent, REGISTER_REQUEST_CODE);
    }

    @OnClick(R.id.btn_password_reset)
    public void onPasswordResetClick() {
        new MaterialDialog.Builder(this)
                .title(R.string.forgot_password)
                .customView(R.layout.password_reset_layout, true)
                .positiveText(R.string.reset_password)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog resetDialog, @NonNull DialogAction which) {
                        View resetView = resetDialog.getView();
                        EditText userNameText = (EditText) resetView.findViewById(R.id.editUsername);
                        EditText emailText = (EditText) resetView.findViewById(R.id.editEMail);

                        LinkedHashMap<String, Object> forgotMap = new LinkedHashMap<String, Object>();
                        forgotMap.put("UserName", userNameText.getText().toString());
                        forgotMap.put("EMail", emailText.getText().toString());

                        final MaterialDialog progressDialog = new MaterialDialog.Builder(LoginActivity.this)
                                .content(R.string.password_is_resetting)
                                .progress(true, 0)
                                .cancelable(false)
                                .show();

                        mUserController.forgotPassword(forgotMap).setResultHandler(new IICResultHandler<String>(LoginActivity.this) {
                            @Override
                            public void onSuccess(String param) {
                                progressDialog.dismiss();

                                new MaterialDialog.Builder(LoginActivity.this)
                                        .content(param)
                                        .positiveText(R.string.OK)
                                        .show();

                                resetDialog.dismiss();
                            }

                            @Override
                            public boolean onError(IICError error) {
                                progressDialog.dismiss();

                                if (error.getErrorId().equals(IICError.E_API_ERROR)) {
                                    ErrorHandler.showCustomErrorDialog(LoginActivity.this, getString(R.string.an_error_occurred), error.getMessage());

                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                })
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_REQUEST_CODE && resultCode == RESULT_OK) {
            switchToMainActivity();
        }
    }

    private void userExists(Runnable loginRunnable, Runnable mainRunnable) {
        User currentUser = mRealm.where(User.class).equalTo("isLocalUser", true).findFirst();
        if (currentUser == null) {
            loginRunnable.run();
        } else {
            mainRunnable.run();
        }
    }

    private void switchToMainActivity() {
        loggedIn = true;
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mCocoLib = CocoLibSingleton.getInstance(this);
        ResumeTask asyncTask = new ResumeTask();
        asyncTask.execute();

        if (mRealm.isClosed()) {
            mRealm = Realm.getInstance(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();


        if (!loggedIn) {
            if (mRealm.isClosed()) {
                mRealm = Realm.getInstance(this);
            }

            //remove all users because they aren't fully logged in yet
            mRealm.beginTransaction();
            mRealm.where(User.class).findAll().clear();
            mRealm.commitTransaction();
        }

        mCocoLib.cancelAll();

        if(!mRealm.isClosed()) {
            mRealm.close();
        }
    }
}
