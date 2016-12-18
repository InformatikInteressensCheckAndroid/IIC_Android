package at.ac.htlstp.app.iic.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.RequestSerializer;

/**
 * Created by alexnavratil on 15/12/15.
 */
public class SubmitQuizRequestSerializer implements RequestSerializer {
    @Override
    public LinkedHashMap serialize(LinkedHashMap hashMap) {
        LinkedHashMap<String, Object> quizSubmitMap = new LinkedHashMap<>();

        List<LinkedHashMap<String, Object>> questionMapList = new ArrayList<>();

        for (LinkedHashMap<String, Object> questionGroupMap : ((ArrayList<LinkedHashMap<String, Object>>) hashMap.get("question_groups"))) {
            for (LinkedHashMap<String, Object> questionMap : ((ArrayList<LinkedHashMap<String, Object>>) questionGroupMap.get("questions"))) {
                LinkedHashMap<String, Object> tmpQuestionMap = new LinkedHashMap<>();
                List<LinkedHashMap<String, Object>> tmpAnswerList = new ArrayList<>();

                tmpQuestionMap.put("SessionQuestion_ID", questionMap.get("SessionQuestion_ID"));

                for (LinkedHashMap<String, Object> answerMap : ((ArrayList<LinkedHashMap<String, Object>>) questionMap.get("answers"))) {
                    LinkedHashMap<String, Object> tmpAnswerMap = new LinkedHashMap<>();
                    tmpAnswerMap.put("Answer_ID", answerMap.get("answer_ID"));
                    tmpAnswerMap.put("AnswerOfUser", answerMap.get("AnswerOfUser"));

                    tmpAnswerList.add(tmpAnswerMap);
                }

                tmpQuestionMap.put("answers", tmpAnswerList);
                questionMapList.add(tmpQuestionMap);
            }
        }

        quizSubmitMap.put("AppStartStamp", hashMap.get("appStartStamp"));
        quizSubmitMap.put("questions", questionMapList.toArray());

        return quizSubmitMap;
    }
}
