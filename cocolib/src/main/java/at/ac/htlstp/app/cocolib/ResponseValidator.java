package at.ac.htlstp.app.cocolib;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.exception.ValidatorException;

/**
 * Created by alexnavratil on 02/12/15.
 */
public interface ResponseValidator {
    LinkedHashMap parse(LinkedHashMap responseMap) throws ValidatorException;
}
