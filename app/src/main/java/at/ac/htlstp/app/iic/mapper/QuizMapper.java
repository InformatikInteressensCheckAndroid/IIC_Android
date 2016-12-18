package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.Quiz;

/**
 * Created by alexnavratil on 25/12/15.
 */
public class QuizMapper implements ObjectMappingParser<Quiz> {
    @Override
    public Quiz parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ObjectMapper mapper = new ObjectMapper();

        return this.parseToObject(parseMap, mapper);
    }

    public Quiz parseToObject(LinkedHashMap parseMap, ObjectMapper mapper) {
        return mapper.convertValue(parseMap, Quiz.class);
    }
}
