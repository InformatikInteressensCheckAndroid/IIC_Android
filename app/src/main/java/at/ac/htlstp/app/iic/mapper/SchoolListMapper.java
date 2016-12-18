package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.School;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class SchoolListMapper implements ObjectMappingParser<List<School>> {
    @Override
    public List<School> parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ArrayList<Object> schoolParseList = (ArrayList<Object>) parseMap.get("schoolList");

        List<School> schoolList = new ArrayList<>();

        SchoolMapper schoolMapper = new SchoolMapper();

        ObjectMapper mapper = new ObjectMapper();

        for (Object schoolParseObject : schoolParseList) {
            LinkedHashMap schoolParseMap = (LinkedHashMap) schoolParseObject;
            schoolList.add(schoolMapper.parseToObject(schoolParseMap, mapper));
        }

        return schoolList;
    }
}
