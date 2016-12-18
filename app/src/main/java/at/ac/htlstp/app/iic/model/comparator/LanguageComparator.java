package at.ac.htlstp.app.iic.model.comparator;

import java.util.Comparator;

import at.ac.htlstp.app.iic.model.Language;

/**
 * Created by alexnavratil on 30/12/15.
 */
public class LanguageComparator implements Comparator<Language> {
    @Override
    public int compare(Language lhs, Language rhs) {
        return lhs.getInLanguage().compareTo(rhs.getInLanguage());
    }
}
