package at.ac.htlstp.app.iic.db.accessor;


import android.content.Context;
import android.util.Log;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.DatabaseAccessor;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.iic.model.User;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alexnavratil on 21/12/15.
 */
public class UserAccessor implements DatabaseAccessor<User> {

    @Override
    public APIResult<User> getFromDatabase(Context context, List<Variable> variableList) {
        final APIResult<User> resultCallback = new APIResult<>();

        final Realm realm = Realm.getInstance(context);

        User user = realm.where(User.class).equalTo("isLocalUser", true).findFirst();
        if(user != null) {
            resultCallback.notifyCacheHandler(realm.copyFromRealm(user));
        }
        realm.close();

        return resultCallback;
    }

    @Override
    public void saveToDatabase(Context context, final User object) {
        if(object != null) {
            final Realm realm = Realm.getInstance(context);

            realm.beginTransaction();
            /**
             * copying the password from the current persisted user object to the new user object retrieved from the API.
             */
            RealmResults<User> users = realm.where(User.class).equalTo("isLocalUser", true).findAll();

            if (users.size() > 0) {
                User currentUser = users.get(0);

                if (object != null && currentUser != null) {
                    object.setPassword(currentUser.getPassword());
                    object.setLocalUser(true);
                    realm.copyToRealmOrUpdate(object);
                } else {
                    Log.e("IIC", "cannot update the user because he is null!");
                    realm.cancelTransaction();
                }
            } else {
                object.setLocalUser(true);
                realm.copyToRealmOrUpdate(object);
            }

            realm.commitTransaction();
            realm.close();
        }
    }
}
