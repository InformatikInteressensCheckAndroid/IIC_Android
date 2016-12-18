package at.ac.htlstp.app.cocolib;

import java.util.LinkedHashMap;

/**
 * Created by alexnavratil on 03/12/15.
 */
public interface ObjectMappingParser<T> {
    T parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration);
}
