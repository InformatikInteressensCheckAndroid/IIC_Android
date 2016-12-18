package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.School;

/**
 * Created by alexnavratil on 09/12/15.
 */
public class SchoolMapper implements ObjectMappingParser<School> {

    @Override
    public School parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ObjectMapper mapper = new ObjectMapper();

        return this.parseToObject(parseMap, mapper);
    }

    public School parseToObject(LinkedHashMap parseMap, ObjectMapper mapper) {
        return mapper.convertValue(parseMap, School.class);
    }
}
