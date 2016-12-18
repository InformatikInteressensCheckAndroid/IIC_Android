package at.ac.htlstp.app.cocolib.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import at.ac.htlstp.app.cocolib.ObjectMappingParser;

/**
 * Created by alexnavratil on 02/12/15.
 */
public class RootParseMappingHelper {
    private Class parserClass;

    public RootParseMappingHelper(Class parserClass) {
        this.parserClass = parserClass;
    }

    public static RootParseMappingHelper processAnnotation(Annotation annotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Method parserClassMethod = annotationType.getMethod("parserClass");
        return new RootParseMappingHelper((Class) parserClassMethod.invoke(annotation));
    }

    public ObjectMappingParser createParserInstance() throws IllegalAccessException, InstantiationException {
        return (ObjectMappingParser) parserClass.newInstance();
    }

    public Class getParserClass() {
        return parserClass;
    }

    public void setParserClass(Class parserClass) {
        this.parserClass = parserClass;
    }
}
