package at.ac.htlstp.app.iic.db.accessor;


import android.content.Context;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.DatabaseAccessor;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.iic.model.Language;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by alexnavratil on 21/12/15.
 */
public class LanguageAccessor implements DatabaseAccessor<List<Language>> {

    @Override
    public APIResult<List<Language>> getFromDatabase(Context context, List<Variable> variableList) {
        final APIResult<List<Language>> resultCallback = new APIResult<>();

        final Realm realm = Realm.getInstance(context);

        final RealmResults<Language> results = realm.where(Language.class).findAllSortedAsync("InLanguage", Sort.ASCENDING);

        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                List<Language> languageList = realm.copyFromRealm(results);

                resultCallback.notifyCacheHandler(languageList);
                results.removeChangeListeners();
                realm.close();
            }
        });

        return resultCallback;
    }

    @Override
    public void saveToDatabase(Context context, final List<Language> object) {
        final Realm realm = Realm.getInstance(context);

        Number lastUpdateIdNr = realm.where(Language.class).findAll().max("updateid");
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

        for (Language l : object) {
            l.setUpdateid(currentUpdateId);
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);

                //remove all non-updated records
                realm.where(Language.class).notEqualTo("updateid", currentUpdateId).findAll().clear();
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
