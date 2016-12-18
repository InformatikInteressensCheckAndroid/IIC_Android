package at.ac.htlstp.app.cocolib.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import at.ac.htlstp.app.cocolib.ResponseValidator;

/**
 * Created by alexnavratil on 02/12/15.
 */
public class ResponseValidatorHelper {
    private Class validatorClass;

    public ResponseValidatorHelper(Class validatorClass) {
        this.validatorClass = validatorClass;
    }

    public static ResponseValidatorHelper processAnnotation(Annotation annotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Method method = annotationType.getMethod("validatorClass");
        return new ResponseValidatorHelper((Class) method.invoke(annotation));
    }

    public ResponseValidator createValidatorInstance() throws IllegalAccessException, InstantiationException {
        return (ResponseValidator) validatorClass.newInstance();
    }

    public Class getValidatorClass() {
        return validatorClass;
    }

    public void setValidatorClass(Class validatorClass) {
        this.validatorClass = validatorClass;
    }
}
