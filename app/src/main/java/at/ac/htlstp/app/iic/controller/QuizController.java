package at.ac.htlstp.app.iic.controller;

import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.annotation.MappingClass;
import at.ac.htlstp.app.cocolib.annotation.PostBody;
import at.ac.htlstp.app.cocolib.annotation.PostRequestSerializer;
import at.ac.htlstp.app.cocolib.annotation.ResponseValidator;
import at.ac.htlstp.app.cocolib.annotation.RootParseMapping;
import at.ac.htlstp.app.cocolib.annotation.Variable;
import at.ac.htlstp.app.cocolib.annotation.method.GET;
import at.ac.htlstp.app.cocolib.annotation.method.POST;
import at.ac.htlstp.app.iic.mapper.QuizCategoryListMapper;
import at.ac.htlstp.app.iic.mapper.QuizListMapper;
import at.ac.htlstp.app.iic.mapper.QuizResultListMapper;
import at.ac.htlstp.app.iic.mapper.QuizResultMapper;
import at.ac.htlstp.app.iic.mapper.QuizSessionQuizMapper;
import at.ac.htlstp.app.iic.mapper.SubmitQuizRequestSerializer;
import at.ac.htlstp.app.iic.model.Quiz;
import at.ac.htlstp.app.iic.model.QuizCategory;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.model.QuizSessionQuiz;
import at.ac.htlstp.app.iic.validator.IICStandardValidator;

/**
 * Created by alexnavratil on 22/12/15.
 */
public interface QuizController {
    @GET("category/list/all")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = QuizCategoryListMapper.class)
    @MappingClass(accessorClass = QuizCategory.class, wrappedBy = List.class)
    APIResult<List<QuizCategory>> getAllQuizCategories();

    @GET("category/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = QuizCategoryListMapper.class)
    @MappingClass(accessorClass = QuizCategory.class, wrappedBy = List.class)
    APIResult<List<QuizCategory>> getQuizCategories();

    @GET("category/{languageId}/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = QuizCategoryListMapper.class)
    @MappingClass(accessorClass = QuizCategory.class, wrappedBy = List.class)
    APIResult<List<QuizCategory>> getQuizCategories(@Variable(identifier = "languageId") int languageId);

    @GET("quiz/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = QuizListMapper.class)
    @MappingClass(accessorClass = Quiz.class, wrappedBy = List.class)
    APIResult<List<Quiz>> getQuizzes();

    @GET("quiz/category/{categoryId}/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = QuizListMapper.class)
    @MappingClass(accessorClass = Quiz.class, wrappedBy = List.class)
    APIResult<List<Quiz>> getQuizzesOfCategory(@Variable(identifier = "categoryId") int categoryId);

    @GET("quiz/finished/{sessionId}")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = QuizResultMapper.class)
    @MappingClass(accessorClass = QuizResult.class)
    APIResult<QuizResult> getFinishedQuizSession(@Variable(identifier = "sessionId") int sessionId);

    @GET("quiz/finished/list")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = QuizResultListMapper.class)
    @MappingClass(accessorClass = QuizResult.class, wrappedBy = List.class)
    APIResult<List<QuizResult>> getFinishedQuizzes();

    @GET("quiz/start/{quizId}")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @RootParseMapping(parserClass = QuizSessionQuizMapper.class)
    @MappingClass(accessorClass = QuizSessionQuiz.class)
    APIResult<QuizSessionQuiz> startQuiz(@Variable(identifier = "quizId") int quizId);

    @POST("quiz/submit/{sessionId}")
    @ResponseValidator(validatorClass = IICStandardValidator.class)
    @PostRequestSerializer(serializerClass = SubmitQuizRequestSerializer.class)
    @RootParseMapping(parserClass = QuizResultMapper.class)
    @MappingClass(accessorClass = QuizResult.class)
    APIResult<QuizResult> submitQuiz(@Variable(identifier = "sessionId") int sessionId, @PostBody QuizSessionQuiz session);
}
