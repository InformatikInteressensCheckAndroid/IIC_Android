package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;

/**
 * Created by alexnavratil on 09/12/15.
 */
public abstract class DefaultIICMapper<T> implements ObjectMappingParser<T> {
    protected Class modelClass;

    @Override
    public T parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ObjectMapper mapper = new ObjectMapper();

        return parseToObject(parseMap, mapper);
    }

    public T parseToObject(LinkedHashMap parseMap, ObjectMapper mapper) {
        return (T) mapper.convertValue(parseMap, modelClass);
    }
}
