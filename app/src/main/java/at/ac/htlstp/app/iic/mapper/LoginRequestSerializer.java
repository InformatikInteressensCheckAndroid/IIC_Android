package at.ac.htlstp.app.iic.mapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.RequestSerializer;

/**
 * Created by alexnavratil on 15/12/15.
 */
public class LoginRequestSerializer implements RequestSerializer {
    @Override
    public LinkedHashMap serialize(LinkedHashMap hashMap) {
        LinkedHashMap<String, Object> authenticationMap = new LinkedHashMap<>();
        authenticationMap.put("username", hashMap.get("userName"));
        authenticationMap.put("password", hashMap.get("password"));

        return authenticationMap;
    }
}
