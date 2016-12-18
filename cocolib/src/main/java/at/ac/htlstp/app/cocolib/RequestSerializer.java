package at.ac.htlstp.app.cocolib;

import java.util.LinkedHashMap;

/**
 * Created by alexnavratil on 15/12/15.
 */
public interface RequestSerializer {
    LinkedHashMap serialize(LinkedHashMap hashMap);
}
