package at.ac.htlstp.app.iic.parser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import at.ac.htlstp.app.cocolib.parse.MapParser;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class JsonMapParser implements MapParser {

    @Override
    public LinkedHashMap parse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<?, ?> parseMap = mapper.readValue(response, Map.class);

            return (LinkedHashMap) parseMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LinkedHashMap parseFromObject(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String objectJson = mapper.writeValueAsString(object);
            Map<?, ?> parseMap = mapper.readValue(objectJson, Map.class);

            return (LinkedHashMap) parseMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String serializeToString(LinkedHashMap parseMap) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(parseMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

}
