package at.ac.htlstp.app.iic.mapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;

/**
 * Created by alexnavratil on 07/02/16.
 */
public class MessageMapper implements ObjectMappingParser<String> {

    @Override
    public String parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        return (String) parseMap.get("message");
    }
}
