package at.ac.htlstp.app.iic.db;

import android.content.Context;

import at.ac.htlstp.app.iic.model.User;
import io.realm.Realm;

/**
 * Created by alexnavratil on 29/12/15.
 */
public class UserDAO {
    public static User getCurrentUser(Context context) {
        Realm realm = Realm.getInstance(context);

        User currentUser = realm.copyFromRealm(realm.where(User.class).equalTo("isLocalUser", true).findFirst());

        realm.close();

        return currentUser;
    }
}
