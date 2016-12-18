package at.ac.htlstp.app.iic.mapper;

import at.ac.htlstp.app.iic.model.UserClass;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class ClassListMapper extends DefaultIICListMapper<UserClass> {
    {
        super.jsonIdentifier = "classList";
        super.parser = new ClassMapper();
    }
}
