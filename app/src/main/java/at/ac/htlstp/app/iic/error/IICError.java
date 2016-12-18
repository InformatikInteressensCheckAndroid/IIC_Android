package at.ac.htlstp.app.iic.error;

import at.ac.htlstp.app.cocolib.exception.ValidatorException;

/**
 * Created by alexnavratil on 31/01/16.
 */
public class IICError extends ValidatorException {
    public static final String E_API_ERROR = "E_API_ERROR";
    public static final String E_DB_ERROR = "E_DB_ERROR";
    public static final String E_PARAMETERS_MISSING = "E_PARAMETERS_MISSING";
    public static final String E_AUTH_ERROR = "E_AUTH_ERROR";
    public static final String E_NO_CATEGORY_FOUND = "E_NO_CATEGORY_FOUND";
    public static final String E_NO_QUIZ_FOUND = "E_NO_QUIZ_FOUND";
    public static final String E_NO_CLASS_FOUND = "E_NO_CLASS_FOUND";
    public static final String E_NO_SCHOOL_FOUND = "E_NO_SCHOOL_FOUND";

    public static final String E_NETWORK_ERROR = "E_NETWORK_ERROR";

    public static final String E_SERVER_DOWN = "E_SERVER_DOWN";

    private String errorId = E_API_ERROR;

    private Exception exception;

    private String message;

    public IICError(Exception ex, String errorId) {
        this(ex, "", errorId);
    }

    public IICError(Exception ex, String message, String errorId) {
        this.exception = ex;
        this.message = message;
        this.errorId = errorId;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "IICError{" +
                "errorId='" + errorId + '\'' +
                ", exception=" + exception +
                ", message='" + message + '\'' +
                '}';
    }
}
