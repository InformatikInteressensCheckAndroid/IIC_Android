package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.QuizCategory;

/**
 * Created by alexnavratil on 22/12/15.
 */
public class QuizCategoryMapper implements ObjectMappingParser<QuizCategory> {
    @Override
    public QuizCategory parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ObjectMapper mapper = new ObjectMapper();

        return this.parseToObject(parseMap, mapper);
    }

    public QuizCategory parseToObject(LinkedHashMap parseMap, ObjectMapper mapper) {
        return mapper.convertValue(parseMap, QuizCategory.class);
    }
}
