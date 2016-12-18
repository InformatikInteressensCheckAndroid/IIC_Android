package at.ac.htlstp.app.cocolib.helper;

/**
 * Created by alexnavratil on 02/12/15.
 */
public class Variable {
    private String identifier;
    private Object value;

    public Variable(String identifier, Object value) {
        this.identifier = identifier;
        this.value = value;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
