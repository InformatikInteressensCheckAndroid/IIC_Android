package at.ac.htlstp.app.iic.mapper;

import at.ac.htlstp.app.iic.model.UserClass;

/**
 * Created by alexnavratil on 09/12/15.
 */
public class ClassMapper extends DefaultIICMapper<UserClass> {
    {
        super.modelClass = UserClass.class;
    }
}
