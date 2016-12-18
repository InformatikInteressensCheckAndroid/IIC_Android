package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.model.comparator.QuizResultComparator;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class QuizResultListMapper implements ObjectMappingParser<List<QuizResult>> {
    @Override
    public List<QuizResult> parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ArrayList<Object> quizResultParseList = (ArrayList<Object>) parseMap.get("quizResultList");

        List<QuizResult> quizResultList = new ArrayList<>();

        QuizResultMapper quizResultMapper = new QuizResultMapper();

        ObjectMapper mapper = new ObjectMapper();

        for (Object quizResultParseObject : quizResultParseList) {
            LinkedHashMap quizResultParseMap = (LinkedHashMap) quizResultParseObject;
            quizResultList.add(quizResultMapper.parseToObject(quizResultParseMap, mapper));
        }

        Collections.sort(quizResultList, new QuizResultComparator());

        return quizResultList;
    }
}
