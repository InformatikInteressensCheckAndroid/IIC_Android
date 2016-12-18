package at.ac.htlstp.app.iic.controller;

import java.util.LinkedHashMap;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.annotation.MappingClass;
import at.ac.htlstp.app.cocolib.annotation.PostBody;
import at.ac.htlstp.app.cocolib.annotation.PostRequestSerializer;
import at.ac.htlstp.app.cocolib.annotation.ResponseValidator;
import at.ac.htlstp.app.cocolib.annotation.RootParseMapping;
import at.ac.htlstp.app.cocolib.annotation.method.GET;
import at.ac.htlstp.app.cocolib.annotation.method.POST;
import at.ac.htlstp.app.iic.mapper.ChangePasswordRequestSerializer;
import at.ac.htlstp.app.iic.mapper.ChangeUserRequestSerializer;
import at.ac.htlstp.app.iic.mapper.ForgotPasswordRequestSerializer;
import at.ac.htlstp.app.iic.mapper.LoginRequestSerializer;
import at.ac.htlstp.app.iic.mapper.MessageMapper;
import at.ac.htlstp.app.iic.mapper.RegisterUserRequestSerializer;
import at.ac.htlstp.app.iic.mapper.UserAuthenticationMapper;
import at.ac.htlstp.app.iic.mapper.UserMapper;
import at.ac.htlstp.app.iic.model.User;
import at.ac.htlstp.app.iic.validator.IICStandardValidator;

/**
 * Created by alexnavratil on 15/12/15.
 */
public interface UserController {
    @POST("user/login")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @PostRequestSerializer(serializerClass = LoginRequestSerializer.class)
    @RootParseMapping(parserClass = UserAuthenticationMapper.class)
    @MappingClass(accessorClass = User.class)
    APIResult<User> authenticate(@PostBody User user);

    @GET("user/info")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = UserMapper.class)
    @MappingClass(accessorClass = User.class)
    APIResult<User> getCurrentUser();

    @POST("user/edit")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @PostRequestSerializer(serializerClass = ChangeUserRequestSerializer.class)
    @RootParseMapping(parserClass = MessageMapper.class)
    @MappingClass(accessorClass = String.class)
    APIResult<String> updateUser(@PostBody User user);

    @POST("user/password/edit")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @PostRequestSerializer(serializerClass = ChangePasswordRequestSerializer.class)
    @RootParseMapping(parserClass = MessageMapper.class)
    @MappingClass(accessorClass = String.class)
    APIResult<String> changePassword(@PostBody LinkedHashMap<String, Object> passwordData);

    @POST("user/register")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @PostRequestSerializer(serializerClass = RegisterUserRequestSerializer.class)
    @RootParseMapping(parserClass = MessageMapper.class)
    @MappingClass(accessorClass = String.class)
    APIResult<String> register(@PostBody User user);

    @POST("user/password/forgot")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @PostRequestSerializer(serializerClass = ForgotPasswordRequestSerializer.class)
    @RootParseMapping(parserClass = MessageMapper.class)
    @MappingClass(accessorClass = String.class)
    APIResult<String> forgotPassword(@PostBody LinkedHashMap<String, Object> passwordData);

}
