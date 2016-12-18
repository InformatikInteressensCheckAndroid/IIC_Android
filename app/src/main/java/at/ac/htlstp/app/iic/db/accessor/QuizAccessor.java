package at.ac.htlstp.app.iic.db.accessor;


import android.content.Context;
import android.util.Log;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.DatabaseAccessor;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.iic.model.Quiz;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by alexnavratil on 21/12/15.
 */
public class QuizAccessor implements DatabaseAccessor<List<Quiz>> {

    @Override
    public APIResult<List<Quiz>> getFromDatabase(Context context, List<Variable> variableList) {
        final APIResult<List<Quiz>> resultCallback = new APIResult<>();

        final Realm realm = Realm.getInstance(context);

        RealmQuery<Quiz> quizQuery = realm.where(Quiz.class);

        Integer categoryId = getCategoryIdFromVariableList(variableList);

        if (categoryId != null) {
            quizQuery.equalTo("QuizCategory_Language_ID", categoryId);
        }

        final RealmResults<Quiz> results = quizQuery.findAllSortedAsync("Name", Sort.ASCENDING);

        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                List<Quiz> quizList = realm.copyFromRealm(results);

                resultCallback.notifyCacheHandler(quizList); //there can be only one user which has isLocalUser set to true
                results.removeChangeListeners();
                realm.close();
            }
        });

        return resultCallback;
    }

    @Override
    public void saveToDatabase(Context context, final List<Quiz> object) {
        final Realm realm = Realm.getInstance(context);

        Number lastUpdateIdNr = realm.where(Quiz.class).findAll().max("updateid");
        Log.i("IIC", "lastUpdateId: " + lastUpdateIdNr);
        int lastUpdateId = 0;
        if (lastUpdateIdNr != null) {
            lastUpdateId = lastUpdateIdNr.intValue();
        }

        /**
         * don't let the updateid get up too much
         * we don't want max_integer overflows :)
         */
        if (lastUpdateId >= 100) {
            lastUpdateId = 0;
        }

        final int currentUpdateId = lastUpdateId + 1;

        for (Quiz q : object) {
            q.setUpdateid(currentUpdateId);
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);

                //remove all non-updated records
                realm.where(Quiz.class).notEqualTo("updateid", currentUpdateId).findAll().clear();
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

    private Integer getCategoryIdFromVariableList(List<Variable> variableList) {
        for (Variable v : variableList) {
            if (v.getIdentifier().equals("categoryId")) {
                return (Integer) v.getValue();
            }
        }
        return null;
    }
}
