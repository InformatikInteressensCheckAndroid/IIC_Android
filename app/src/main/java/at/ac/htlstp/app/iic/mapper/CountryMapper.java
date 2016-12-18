package at.ac.htlstp.app.iic.mapper;

import at.ac.htlstp.app.iic.model.Country;

/**
 * Created by alexnavratil on 09/12/15.
 */
public class CountryMapper extends DefaultIICMapper<Country> {
    {
        super.modelClass = Country.class;
    }
}
