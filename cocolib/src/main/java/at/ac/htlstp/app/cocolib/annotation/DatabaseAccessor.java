package at.ac.htlstp.app.cocolib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by alexnavratil on 02/12/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DatabaseAccessor {
    Class<? extends at.ac.htlstp.app.cocolib.DatabaseAccessor> accessor();
}
