package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.QuizCategory;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class QuizCategoryListMapper implements ObjectMappingParser<List<QuizCategory>> {
    @Override
    public List<QuizCategory> parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ArrayList<Object> quizCategoryParseList = (ArrayList<Object>) parseMap.get("quizCategoryList");

        List<QuizCategory> quizCategoryList = new ArrayList<>();

        QuizCategoryMapper quizCategoryMapper = new QuizCategoryMapper();

        ObjectMapper mapper = new ObjectMapper();

        for (Object quizCategoryParseObject : quizCategoryParseList) {
            LinkedHashMap quizCategoryParseMap = (LinkedHashMap) quizCategoryParseObject;
            quizCategoryList.add(quizCategoryMapper.parseToObject(quizCategoryParseMap, mapper));
        }

        return quizCategoryList;
    }
}
