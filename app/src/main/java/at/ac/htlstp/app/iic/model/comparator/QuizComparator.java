package at.ac.htlstp.app.iic.model.comparator;

import java.util.Comparator;

import at.ac.htlstp.app.iic.model.Quiz;

/**
 * Created by alexnavratil on 30/12/15.
 */
public class QuizComparator implements Comparator<Quiz> {
    @Override
    public int compare(Quiz lhs, Quiz rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }
}
