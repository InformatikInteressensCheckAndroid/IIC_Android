package at.ac.htlstp.app.iic.controller;

import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.annotation.MappingClass;
import at.ac.htlstp.app.cocolib.annotation.PostBody;
import at.ac.htlstp.app.cocolib.annotation.ResponseValidator;
import at.ac.htlstp.app.cocolib.annotation.RootParseMapping;
import at.ac.htlstp.app.cocolib.annotation.Variable;
import at.ac.htlstp.app.cocolib.annotation.method.GET;
import at.ac.htlstp.app.cocolib.annotation.method.POST;
import at.ac.htlstp.app.iic.mapper.ClassListMapper;
import at.ac.htlstp.app.iic.mapper.MessageMapper;
import at.ac.htlstp.app.iic.mapper.SchoolListMapper;
import at.ac.htlstp.app.iic.model.School;
import at.ac.htlstp.app.iic.model.UserClass;
import at.ac.htlstp.app.iic.validator.IICStandardValidator;

/**
 * Created by alexnavratil on 03/12/15.
 */
public interface SchoolClassController {
    @GET("school/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = SchoolListMapper.class)
    @MappingClass(accessorClass = School.class, wrappedBy = List.class)
    APIResult<List<School>> getSchools();

    @GET("class/{schoolId}/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = ClassListMapper.class)
    @MappingClass(accessorClass = UserClass.class, wrappedBy = List.class)
    APIResult<List<UserClass>> getClassesBySchool(@Variable(identifier = "schoolId") int schoolId);

    @POST("class/add")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = MessageMapper.class)
    @MappingClass(accessorClass = String.class)
    APIResult<String> addClass(@PostBody LinkedHashMap<String, Object> classData);
}
