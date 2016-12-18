package at.ac.htlstp.app.iic.util;

/**
 * Created by alexnavratil on 09/02/16.
 */
public class IntegerUtils {
    public static int compare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
}
