package at.ac.htlstp.app.iic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.iic.model.User;

/**
 * Created by alexnavratil on 15/12/15.
 */
public class UserMapper implements ObjectMappingParser {
    @Override
    public Object parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(parseMap, User.class);
    }
}
