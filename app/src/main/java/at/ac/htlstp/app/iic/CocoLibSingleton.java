package at.ac.htlstp.app.iic;

import android.content.Context;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.iic.parser.JsonMapParser;

/**
 * Created by alexnavratil on 29/12/15.
 */
public class CocoLibSingleton {
    public static final String URL = "https://iic-test.htlstp.ac.at/api/v1/";
    private static Map<Context, CocoLib> cocoLibMap = new HashMap<>();

    public static synchronized CocoLib getInstance(Context context) {
        //wenn noch keine Instanz gespeichert ist, oder die Instanz gecancelt wurde
        if (!cocoLibMap.containsValue(context) || cocoLibMap.get(context).isDetached()) {
            try {
                cocoLibMap.put(context, new CocoLib(new CocoLibConfiguration(context, new JsonMapParser(), new URI(URL))));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return cocoLibMap.get(context);
    }
}
