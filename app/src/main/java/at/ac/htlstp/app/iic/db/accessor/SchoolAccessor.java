package at.ac.htlstp.app.iic.db.accessor;

import android.content.Context;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.DatabaseAccessor;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.iic.model.School;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by alexnavratil on 17/12/15.
 */
public class SchoolAccessor implements DatabaseAccessor<List<School>> {
    @Override
    public APIResult getFromDatabase(Context context, List<Variable> list) {
        final Realm realm = Realm.getInstance(context);
        final RealmResults<School> schoolResults = realm.where(School.class).findAllAsync();

        final APIResult<List<School>> asyncResults = new APIResult<>();

        schoolResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                asyncResults.notifyCacheHandler(realm.copyFromRealm(schoolResults));
                schoolResults.removeChangeListeners();
                realm.close();
            }
        });

        return asyncResults;
    }

    @Override
    public void saveToDatabase(Context context, final List<School> schools) {
        final Realm realm = Realm.getInstance(context);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(schools);
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
