package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;

/**
 * Created by alexnavratil on 03/12/15.
 */
public abstract class DefaultIICListMapper<T> implements ObjectMappingParser<List<T>> {
    protected DefaultIICMapper<T> parser;
    protected String jsonIdentifier;

    @Override
    public List<T> parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ArrayList<Object> objectParseList = (ArrayList<Object>) parseMap.get(jsonIdentifier);

        List<T> objectList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (Object parseObject : objectParseList) {
            LinkedHashMap objectParseMap = (LinkedHashMap) parseObject;
            objectList.add(parser.parseToObject(objectParseMap, mapper));
        }

        return objectList;
    }
}
