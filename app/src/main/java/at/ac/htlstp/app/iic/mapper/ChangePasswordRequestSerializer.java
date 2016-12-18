package at.ac.htlstp.app.iic.mapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.RequestSerializer;

/**
 * Created by alexnavratil on 15/12/15.
 */
public class ChangePasswordRequestSerializer implements RequestSerializer {
    @Override
    public LinkedHashMap serialize(LinkedHashMap hashMap) {
        //Log.i("IIC", hashMap.get("OldPassword").toString());
        //Log.i("IIC", hashMap.get("NewPassword").toString());
        LinkedHashMap<String, Object> passwordMap = new LinkedHashMap<>();
        passwordMap.put("OldPassword", hashMap.get("OldPassword"));
        passwordMap.put("NewPassword", hashMap.get("NewPassword"));
        return passwordMap;
    }
}
