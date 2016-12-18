package at.ac.htlstp.app.iic.mapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.RequestSerializer;

/**
 * Created by alexnavratil on 15/12/15.
 */
public class ForgotPasswordRequestSerializer implements RequestSerializer {
    @Override
    public LinkedHashMap serialize(LinkedHashMap hashMap) {
        //Log.i("IIC", hashMap.get("OldPassword").toString());
        //Log.i("IIC", hashMap.get("NewPassword").toString());
        LinkedHashMap<String, Object> forgotMap = new LinkedHashMap<>();
        forgotMap.put("UserName", hashMap.get("UserName"));
        forgotMap.put("EMail", hashMap.get("EMail"));
        return forgotMap;
    }
}
