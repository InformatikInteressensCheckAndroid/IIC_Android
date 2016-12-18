package at.ac.htlstp.app.iic.controller;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.annotation.MappingClass;
import at.ac.htlstp.app.cocolib.annotation.ResponseValidator;
import at.ac.htlstp.app.cocolib.annotation.RootParseMapping;
import at.ac.htlstp.app.cocolib.annotation.method.GET;
import at.ac.htlstp.app.iic.mapper.CountryListMapper;
import at.ac.htlstp.app.iic.mapper.LanguageListMapper;
import at.ac.htlstp.app.iic.model.Country;
import at.ac.htlstp.app.iic.model.Language;
import at.ac.htlstp.app.iic.validator.IICStandardValidator;

/**
 * Created by alexnavratil on 30/12/15.
 */
public interface LanguageController {
    @GET("language/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = LanguageListMapper.class)
    @MappingClass(accessorClass = Language.class, wrappedBy = List.class)
    APIResult<List<Language>> getLanguages();

    @GET("country/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = CountryListMapper.class)
    @MappingClass(accessorClass = Country.class, wrappedBy = List.class)
    APIResult<List<Country>> getCountries();
}
