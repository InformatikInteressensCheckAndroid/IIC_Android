package at.ac.htlstp.app.iic.db.accessor;


import android.content.Context;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.DatabaseAccessor;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.iic.model.UserClass;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by alexnavratil on 21/12/15.
 */
public class ClassAccessor implements DatabaseAccessor<List<UserClass>> {

    @Override
    public APIResult<List<UserClass>> getFromDatabase(Context context, List<Variable> variableList) {
        final APIResult<List<UserClass>> resultCallback = new APIResult<>();

        final Realm realm = Realm.getInstance(context);

        Integer schoolId = getSchoolIDFromVariableList(variableList);
        if (schoolId == null) {
            throw new NullPointerException("Variable schoolId not set in SchoolClassController");
        }

        final RealmResults<UserClass> results = realm.where(UserClass.class).equalTo("School_ID", schoolId).findAllAsync();
        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                resultCallback.notifyCacheHandler(realm.copyFromRealm(results));
                results.removeChangeListeners();
                realm.close();
            }
        });

        return resultCallback;
    }

    @Override
    public void saveToDatabase(Context context, final List<UserClass> object) {
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

    public Integer getSchoolIDFromVariableList(List<Variable> variableList) {
        for (Variable v : variableList) {
            if (v.getIdentifier().equals("schoolId")) {
                return (Integer) v.getValue();
            }
        }
        return null;
    }
}
