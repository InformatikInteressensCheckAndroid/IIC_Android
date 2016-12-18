package at.ac.htlstp.app.cocolib.async;

import android.os.AsyncTask;
import android.os.Looper;

import at.ac.htlstp.app.cocolib.APIResult;

/**
 * Created by alexnavratil on 17/12/15.
 */
public class ParseAsyncTask extends AsyncTask<AsyncTaskCallback, Void, Object> {
    private APIResult<Object> result;

    public ParseAsyncTask(APIResult<Object> result) {
        this.result = result;
    }

    @Override
    protected Object doInBackground(AsyncTaskCallback... params) {
        Looper.prepare();
        Object obj = params[0].doInBackground();
        Looper.loop();

        return obj;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (o instanceof Exception) {
            result.notifyErrorHandler((Exception) o);
        } else {
            result.notifySuccessHandler(o);
        }
    }
}
