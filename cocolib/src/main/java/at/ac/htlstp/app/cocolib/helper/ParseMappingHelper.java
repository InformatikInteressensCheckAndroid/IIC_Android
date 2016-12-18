package at.ac.htlstp.app.cocolib.helper;

import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.cocolib.annotation.ParseMapping;

/**
 * Created by alexnavratil on 02/12/15.
 */
public class ParseMappingHelper implements Comparable<ParseMappingHelper> {
    private ParseMapping parseMapping;

    public ParseMappingHelper(ParseMapping parseMapping) {
        this.parseMapping = parseMapping;
    }

    public ParseMapping getParseMapping() {
        return parseMapping;
    }

    public void setParseMapping(ParseMapping parseMapping) {
        this.parseMapping = parseMapping;
    }

    public ObjectMappingParser createParserInstance() throws IllegalAccessException, InstantiationException {
        return parseMapping.parser().newInstance();
    }

    @Override
    public int compareTo(ParseMappingHelper another) {
        return another.parseMapping.responseNode().split(".").length - this.parseMapping.responseNode().split(".").length;
    }
}
