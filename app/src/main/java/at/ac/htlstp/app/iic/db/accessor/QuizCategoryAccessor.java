package at.ac.htlstp.app.iic.db.accessor;


import android.content.Context;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.DatabaseAccessor;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.iic.model.QuizCategory;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by alexnavratil on 21/12/15.
 */
public class QuizCategoryAccessor implements DatabaseAccessor<List<QuizCategory>> {

    @Override
    public APIResult<List<QuizCategory>> getFromDatabase(Context context, List<Variable> variableList) {
        final APIResult<List<QuizCategory>> resultCallback = new APIResult<>();

        final Realm realm = Realm.getInstance(context);

        RealmQuery<QuizCategory> resultQuery = realm.where(QuizCategory.class);

        Integer languageId = getLanguageIdFromVariableList(variableList);
        if (languageId != null) {
            resultQuery = resultQuery.equalTo("language.Language_ID", languageId);
        }

        final RealmResults<QuizCategory> results = resultQuery.findAllAsync();

        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                resultCallback.notifyCacheHandler(realm.copyFromRealm(results)); //there can be only one user which has isLocalUser set to true
                results.removeChangeListeners();
                realm.close();
            }
        });

        return resultCallback;
    }

    @Override
    public void saveToDatabase(Context context, final List<QuizCategory> object) {
        final Realm realm = Realm.getInstance(context);

        Number lastUpdateIdNr = realm.where(QuizCategory.class).findAll().max("updateid");
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

        for (QuizCategory qc : object) {
            qc.setUpdateid(currentUpdateId);
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);

                //remove all non-updated records
                realm.where(QuizCategory.class).notEqualTo("updateid", currentUpdateId).findAll().clear();
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

    private Integer getLanguageIdFromVariableList(List<Variable> variableList) {
        for (Variable v : variableList) {
            if (v.getIdentifier().equals("languageId")) {
                return (Integer) v.getValue();
            }
        }
        return null;
    }
}
