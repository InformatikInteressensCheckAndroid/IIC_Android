package at.ac.htlstp.app.iic.db.accessor;


import android.content.Context;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.DatabaseAccessor;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.iic.model.QuizResult;
import io.realm.Realm;
import io.realm.RealmChangeListener;

/**
 * Created by alexnavratil on 21/12/15.
 */
public class QuizResultAccessor implements DatabaseAccessor<QuizResult> {

    @Override
    public APIResult<QuizResult> getFromDatabase(Context context, List<Variable> variableList) {
        final APIResult<QuizResult> resultCallback = new APIResult<>();

        final Realm realm = Realm.getInstance(context);

        int sessionId = getSessionIDFromVariableList(variableList);

        final QuizResult result = realm.where(QuizResult.class).equalTo("Session_ID", sessionId).findFirstAsync();

        result.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                resultCallback.notifyCacheHandler(realm.copyFromRealm(result));
                result.removeChangeListeners();
                realm.close();
            }
        });

        return resultCallback;
    }

    @Override
    public void saveToDatabase(Context context, final QuizResult object) {
        final Realm realm = Realm.getInstance(context);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                realm.close();
            }

            @Override
            public void onError(Exception e) {
                realm.close();
            }
        });
    }

    public Integer getSessionIDFromVariableList(List<Variable> variableList) {
        for (Variable v : variableList) {
            if (v.getIdentifier().equals("sessionId")) {
                return (Integer) v.getValue();
            }
        }
        return null;
    }
}
