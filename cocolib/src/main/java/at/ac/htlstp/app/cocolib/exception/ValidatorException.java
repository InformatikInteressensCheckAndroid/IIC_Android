package at.ac.htlstp.app.cocolib.exception;

/**
 * Created by Matthias on 11.01.17.
 */

public class ValidatorException extends RuntimeException {
    public ValidatorException() {
    }

    public ValidatorException(String message) {
        super(message);
    }
}

