package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.QuizResult;

/**
 * Created by alexnavratil on 09/12/15.
 */
public class QuizResultMapper implements ObjectMappingParser<QuizResult> {
    @Override
    public QuizResult parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ObjectMapper mapper = new ObjectMapper();

        return parseToObject(parseMap, mapper);
    }

    public QuizResult parseToObject(LinkedHashMap parseMap, ObjectMapper mapper) {
        return mapper.convertValue(parseMap, QuizResult.class);
    }
}
