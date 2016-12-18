package at.ac.htlstp.app.iic.error;

import android.content.Context;

import com.android.volley.VolleyError;

import at.ac.htlstp.app.cocolib.ResultHandler;

/**
 * Created by alexnavratil on 03/02/16.
 */
public abstract class IICResultHandler<T> extends ResultHandler<T> {
    private Context context;
    private Runnable tryAgainAction;

    public IICResultHandler(Context context) {
        this(context, null);
    }

    public IICResultHandler(Context context, Runnable tryAgainAction) {
        this.context = context;
        this.tryAgainAction = tryAgainAction;
    }

    @Override
    public final void onError(Exception ex) {
        IICError error;
        if (!(ex instanceof IICError)) {
            if (ex instanceof VolleyError) {
                error = new IICError(ex, IICError.E_NETWORK_ERROR);
            } else {
                error = new IICError(ex, "");
            }
        } else {
            error = (IICError) ex;
        }

        if (!onError(error)) {
            ErrorHandler.handleError(context, error, false, tryAgainAction);
        }
    }

    public abstract boolean onError(IICError error);
}
