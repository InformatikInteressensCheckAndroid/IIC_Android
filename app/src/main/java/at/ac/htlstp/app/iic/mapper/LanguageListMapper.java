package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.Language;
import at.ac.htlstp.app.iic.model.comparator.LanguageComparator;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class LanguageListMapper implements ObjectMappingParser<List<Language>> {
    @Override
    public List<Language> parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ArrayList<Object> languageParseList = (ArrayList<Object>) parseMap.get("languageList");

        List<Language> languageList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (Object languageParseObject : languageParseList) {
            LinkedHashMap languageParseMap = (LinkedHashMap) languageParseObject;
            languageList.add(mapper.convertValue(languageParseMap, Language.class));
        }

        Collections.sort(languageList, new LanguageComparator());

        return languageList;
    }
}
