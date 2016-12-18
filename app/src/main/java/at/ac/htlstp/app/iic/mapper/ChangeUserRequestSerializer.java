package at.ac.htlstp.app.iic.mapper;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.RequestSerializer;

/**
 * Created by alexnavratil on 15/12/15.
 */
public class ChangeUserRequestSerializer implements RequestSerializer {
    @Override
    public LinkedHashMap serialize(LinkedHashMap hashMap) {
        LinkedHashMap<String, Object> userMap = new LinkedHashMap<>();

        LinkedHashMap<String, Object> classMap = (LinkedHashMap<String, Object>) hashMap.get("user_class");
        LinkedHashMap<String, Object> languageMap = (LinkedHashMap<String, Object>) hashMap.get("language");
        LinkedHashMap<String, Object> countryMap = (LinkedHashMap<String, Object>) hashMap.get("country");

        userMap.put("FirstName", hashMap.get("FirstName"));
        userMap.put("LastName", hashMap.get("LastName"));
        userMap.put("EMail", hashMap.get("EMail"));
        if (classMap != null) {
            userMap.put("Class_ID", classMap.get("Class_ID"));
        }
        userMap.put("PLZ", hashMap.get("PLZ"));
        userMap.put("Housenumber", hashMap.get("Housenumber"));
        userMap.put("Village", hashMap.get("Village"));
        userMap.put("Street", hashMap.get("Street"));
        userMap.put("Stair", hashMap.get("Stair"));
        userMap.put("Door", hashMap.get("Door"));
        userMap.put("Language_ID", languageMap.get("Language_ID"));
        userMap.put("YearInSchool", hashMap.get("YearInSchool"));
        userMap.put("Country_ID", countryMap.get("Country_ID"));

        return userMap;
    }
}
