package at.ac.htlstp.app.iic.db.accessor;


import android.content.Context;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.DatabaseAccessor;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.iic.model.QuizResult;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by alexnavratil on 21/12/15.
 */
public class QuizResultListAccessor implements DatabaseAccessor<List<QuizResult>> {

    @Override
    public APIResult<List<QuizResult>> getFromDatabase(Context context, List<Variable> variableList) {
        final APIResult<List<QuizResult>> resultCallback = new APIResult<>();

        final Realm realm = Realm.getInstance(context);

        final RealmResults<QuizResult> results = realm.where(QuizResult.class).findAllSortedAsync("StartStamp", Sort.DESCENDING);

        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                List<QuizResult> quizResultList = realm.copyFromRealm(results);

                resultCallback.notifyCacheHandler(quizResultList);
                results.removeChangeListeners();
                realm.close();
            }
        });

        return resultCallback;
    }

    @Override
    public void saveToDatabase(Context context, final List<QuizResult> object) {
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
}
