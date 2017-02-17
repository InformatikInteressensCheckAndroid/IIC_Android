package at.ac.htlstp.app.iic.error;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Arrays;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.cocolib.ResultHandler;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.controller.UserController;
import at.ac.htlstp.app.iic.model.User;
import at.ac.htlstp.app.iic.ui.activity.MainActivity;
import io.realm.Realm;

/**
 * Created by alexnavratil on 03/02/16.
 */
public class ErrorHandler {
    private static boolean offlineMode = false;

    public static void handleError(Context context, IICError error, boolean forceOnline) {
        handleError(context, error, forceOnline, null, null);
    }

    public static void handleError(Context context, IICError error, boolean forceOnline, Runnable tryAgainAction) {
        handleError(context, error, forceOnline, tryAgainAction, null);
    }

    public static void handleError(Context context, IICError error, boolean forceOnline, Runnable tryAgainAction, Runnable okAction) {
        Log.e("IIC", error.getErrorId());

        switch (error.getErrorId()) {
            case IICError.E_NETWORK_ERROR:
                handleNetworkError(context, error, forceOnline, tryAgainAction, okAction);
                break;
            case IICError.E_AUTH_ERROR:
                handleAuthenticationError(context, error, tryAgainAction);
                break;
            case IICError.E_API_ERROR:
            case IICError.E_DB_ERROR:
            case IICError.E_PARAMETERS_MISSING:
            default:
                showErrorDialog(context, error, forceOnline, tryAgainAction, okAction);
                break;
        }
    }

    public static void showCustomErrorDialog(Context context, String title, String content) {
        new MaterialDialog.Builder(context)
                .title(title)
                .cancelable(false)
                .iconRes(R.mipmap.ic_launcher)
                .maxIconSize(80)
                .content(content)
                .positiveText(R.string.OK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private static void showErrorDialog(Context context, IICError error, boolean forceOnline, final Runnable tryAgainAction, final Runnable okAction) {
        int contentResId = R.layout.iic_error_layout;

        if (error.getException() != null) {
            contentResId = R.layout.iic_error_layout_detail;
        }

        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(context)
                .title(R.string.an_error_occurred)
                .cancelable(false)
                .iconRes(R.mipmap.ic_launcher)
                .maxIconSize(80)
                .customView(contentResId, true)
                .positiveText(R.string.OK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (okAction != null) {
                            okAction.run();
                        }
                    }
                });

        if (tryAgainAction != null) {
            dialogBuilder
                    .negativeText("Erneut versuchen")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            tryAgainAction.run();
                        }
                    });
        }

        MaterialDialog dialog = dialogBuilder.show();
        View contentView = dialog.getCustomView();
        TextView errorText = (TextView) contentView.findViewById(R.id.errorText);

        errorText.setText(Html.fromHtml(getErrorMessage(context, error, forceOnline)));

        if (error.getException() != null) {
            final TextView errorDetail = (TextView) contentView.findViewById(R.id.errorDetail);

            errorDetail.setText(Arrays.toString(error.getException().getStackTrace()));

            final Button showErrorDetailButton = (Button) contentView.findViewById(R.id.btnShowDetailInformation);
            showErrorDetailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (errorDetail.getVisibility() == View.GONE) {
                        errorDetail.setVisibility(View.VISIBLE);
                        showErrorDetailButton.setText(R.string.hide_information);
                        showErrorDetailButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_up_grey_700_24dp, 0, 0, 0);
                    } else {
                        errorDetail.setVisibility(View.GONE);
                        showErrorDetailButton.setText(R.string.show_more_information);
                        showErrorDetailButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_down_grey_700_24dp, 0, 0, 0);
                    }
                }
            });

            showErrorDetailButton.setVisibility(View.VISIBLE);
        }

    }

    private static class BackgroundTask extends AsyncTask<Context, Void, CocoLib> {

        @Override
        protected CocoLib doInBackground(Context... params) {
            return CocoLibSingleton.getInstance(params[0]);

        }
    }

    private static void handleAuthenticationError(final Context context, IICError error, final Runnable tryAgainRunnable) {
        if (isInternetAvailable(context)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .content(R.string.signing_in)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            Realm realm = Realm.getInstance(context);
            User currentUser = realm.where(User.class).equalTo("isLocalUser", true).findFirst();

            //CocoLib cocoLib = CocoLibSingleton.getInstance(context);
            BackgroundTask backgroundTask = new BackgroundTask();
            CocoLib cocoLib = backgroundTask.doInBackground(context);

            UserController userController = cocoLib.create(UserController.class);

            userController.authenticate(currentUser).setResultHandler(new ResultHandler<User>() {
                @Override
                public void onSuccess(User param) {
                    super.onSuccess(param);
                    offlineMode = false;
                    dialog.dismiss();

                    if (tryAgainRunnable != null) {
                        tryAgainRunnable.run();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    super.onError(ex);
                    dialog.dismiss();

                    offlineMode = false;

                    //logout user
                    Toast.makeText(context, R.string.auth_error_relogin, Toast.LENGTH_LONG).show();

                    MainActivity.logout(context);
                }
            });
        }
    }

    private static String getErrorMessage(Context context, IICError error, boolean forceOnline) {
        if (forceOnline) {
            return String.format(context.getString(R.string.function_currently_not_available), getErrorMessage(context, error, false));
        }

        switch (error.getErrorId()) {
            case IICError.E_API_ERROR:
            case IICError.E_DB_ERROR:
            case IICError.E_SERVER_DOWN:
            case IICError.E_PARAMETERS_MISSING:
                return String.format("<b>%s</b> %s", (error.getMessage() != null ? error.getMessage() : ""), context.getString(R.string.oops_server_error));
            case IICError.E_NETWORK_ERROR:
                return context.getString(R.string.network_error);
            default:
                return context.getString(R.string.unknown_error);
        }
    }

    private static void handleNetworkError(Context context, IICError error, boolean forceOnline, Runnable tryAgainAction, Runnable okAction) {
        if (!offlineMode) {
            if (isInternetAvailable(context)) {
                //maybe the server is down
                offlineMode = true;
                showErrorDialog(context, new IICError(null, IICError.E_SERVER_DOWN), forceOnline, tryAgainAction, okAction);
            } else {
                //There's no internet connection
                offlineMode = true;
                showErrorDialog(context, new IICError(null, IICError.E_NETWORK_ERROR), forceOnline, tryAgainAction, okAction);
            }
        } else if (forceOnline) {
            if (isInternetAvailable(context)) {
                //maybe the server is down
                offlineMode = true;
                showErrorDialog(context, new IICError(null, IICError.E_SERVER_DOWN), true, tryAgainAction, okAction);
            } else {
                //There's no internet connection
                offlineMode = true;
                showErrorDialog(context, new IICError(null, IICError.E_NETWORK_ERROR), true, tryAgainAction, okAction);
            }
        }
    }

    private static boolean isInternetAvailable(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conManager.getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            //There's no internet connection
            return false;
        } else {
            //maybe the server is down
            return true;
        }
    }

    public static boolean isOfflineMode() {
        return offlineMode;
    }
}
