package at.ac.htlstp.app.cocolib.helper;

import java.util.List;

/**
 * Created by alexnavratil on 02/12/15.
 */
public class GetHelper {

    public static String injectVariableList(String url, List<Variable> variableList) {
        for (Variable vh : variableList) {
            url = url.replace("{" + vh.getIdentifier() + "}", vh.getValue().toString());
        }

        return url;
    }
}
