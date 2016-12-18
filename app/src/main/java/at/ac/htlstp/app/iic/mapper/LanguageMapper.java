package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.Language;

/**
 * Created by alexnavratil on 16/12/15.
 */
public class LanguageMapper implements ObjectMappingParser<Language> {
    @Override
    public Language parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        LinkedHashMap languageParseMap = (LinkedHashMap) parseMap.get("language");
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(languageParseMap, Language.class);
    }
}
