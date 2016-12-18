package at.ac.htlstp.app.cocolib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.ac.htlstp.app.cocolib.RequestSerializer;

/**
 * Created by alexnavratil on 15/12/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostRequestSerializer {
    Class<? extends RequestSerializer> serializerClass();
}
