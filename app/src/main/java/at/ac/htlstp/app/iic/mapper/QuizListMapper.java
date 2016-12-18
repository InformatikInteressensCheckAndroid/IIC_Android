package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.Quiz;
import at.ac.htlstp.app.iic.model.comparator.QuizComparator;

/**
 * Created by alexnavratil on 25/12/15.
 */
public class QuizListMapper implements ObjectMappingParser<List<Quiz>> {
    @Override
    public List<Quiz> parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ArrayList<Object> quizParseList = (ArrayList<Object>) parseMap.get("quizList");

        List<Quiz> quizList = new ArrayList<>();

        QuizMapper quizMapper = new QuizMapper();

        ObjectMapper mapper = new ObjectMapper();

        for (Object quizParseObject : quizParseList) {
            LinkedHashMap quizParseMap = (LinkedHashMap) quizParseObject;
            quizList.add(quizMapper.parseToObject(quizParseMap, mapper));
        }

        Collections.sort(quizList, new QuizComparator());

        return quizList;
    }
}
