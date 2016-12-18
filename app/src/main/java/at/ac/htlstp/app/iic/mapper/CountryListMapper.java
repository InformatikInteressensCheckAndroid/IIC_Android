package at.ac.htlstp.app.iic.mapper;

import at.ac.htlstp.app.iic.model.Country;

/**
 * Created by alexnavratil on 03/12/15.
 */
public class CountryListMapper extends DefaultIICListMapper<Country> {
    {
        super.jsonIdentifier = "countryList";
        super.parser = new CountryMapper();
    }
}
