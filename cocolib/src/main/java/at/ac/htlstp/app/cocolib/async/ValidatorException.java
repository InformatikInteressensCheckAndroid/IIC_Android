package at.ac.htlstp.app.cocolib.exception;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class ValidatorException extends RuntimeException {
    public ValidatorException() {
    }

    public ValidatorException(String message) {
        super(message);
    }
}
