package at.ac.htlstp.app.iic.validator;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.ResponseValidator;
import at.ac.htlstp.app.cocolib.exception.ValidatorException;
import at.ac.htlstp.app.iic.error.IICError;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class IICStandardValidator implements ResponseValidator {
    @Override
    public LinkedHashMap parse(LinkedHashMap responseMap) throws IICError {
        if (responseMap.get("type").equals("OK")) {
            LinkedHashMap dataMap = (LinkedHashMap) responseMap.get("data");
            String dataType = dataMap.get("data_type").toString();
            Object obj = dataMap.get(dataType);
            if (obj instanceof ArrayList) {
                LinkedHashMap arrayMap = new LinkedHashMap();
                arrayMap.put(dataType, obj);
                return arrayMap;
            } else {
                if (obj instanceof LinkedHashMap) {
                    return (LinkedHashMap) obj;
                } else {
                    return dataMap;
                }
            }
        } else {
            LinkedHashMap dataMap = (LinkedHashMap) responseMap.get("data");
            String dataType = dataMap.get("data_type").toString();
            if (dataType.equals("message")) {
                String message = dataMap.get(dataType).toString();
                if (dataMap.containsKey("error_id")) {
                    throw new IICError(null, message, dataMap.get("error_id").toString());
                } else {
                    throw new IICError(null, message, IICError.E_API_ERROR);
                }
            }
            throw new ValidatorException();
        }
    }
}
