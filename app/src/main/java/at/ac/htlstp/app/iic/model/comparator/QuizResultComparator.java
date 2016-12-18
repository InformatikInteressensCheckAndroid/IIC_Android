package at.ac.htlstp.app.iic.model.comparator;

import java.util.Comparator;

import at.ac.htlstp.app.iic.model.QuizResult;

/**
 * Created by alexnavratil on 30/12/15.
 */
public class QuizResultComparator implements Comparator<QuizResult> {
    @Override
    public int compare(QuizResult lhs, QuizResult rhs) {
        return rhs.getStartStamp().compareTo(lhs.getStartStamp());
    }
}
