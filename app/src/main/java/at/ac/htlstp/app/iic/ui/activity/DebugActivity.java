package at.ac.htlstp.app.iic.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ResultHandler;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.adapter.ClassSpinnerAdapter;
import at.ac.htlstp.app.iic.adapter.SchoolSpinnerAdapter;
import at.ac.htlstp.app.iic.controller.QuizController;
import at.ac.htlstp.app.iic.controller.SchoolClassController;
import at.ac.htlstp.app.iic.controller.UserController;
import at.ac.htlstp.app.iic.model.Quiz;
import at.ac.htlstp.app.iic.model.QuizCategory;
import at.ac.htlstp.app.iic.model.QuizResult;
import at.ac.htlstp.app.iic.model.School;
import at.ac.htlstp.app.iic.model.User;
import at.ac.htlstp.app.iic.model.UserClass;
import at.ac.htlstp.app.iic.parser.JsonMapParser;

public class DebugActivity extends AppCompatActivity {
    private Spinner mSchoolSpinner;
    private Spinner mClassSpinner;
    private List<School> mSchoolList;
    private CocoLib coco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSchoolSpinner = (Spinner) findViewById(R.id.schoolSpinner);
        mClassSpinner = (Spinner) findViewById(R.id.classSpinner);

        try {
            coco = new CocoLib(new CocoLibConfiguration(this, new JsonMapParser(), new URI("https://iic15.alexnavratil.ovh/api/v1/")));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final SchoolClassController schoolController = coco.create(SchoolClassController.class);
        final UserController userController = coco.create(UserController.class);
        final APIResult<List<School>> schoolResult = schoolController.getSchools();

        schoolResult.setResultHandler(new ResultHandler<List<School>>() {
            @Override
            public void onCacheResult(List<School> param) {
                onSuccess(param);
            }

            @Override
            public void onSuccess(List<School> schoolList) {
                mSchoolList = schoolList;
                SchoolSpinnerAdapter adapter = new SchoolSpinnerAdapter(DebugActivity.this, schoolList);
                mSchoolSpinner.setAdapter(adapter);
            }

            @Override
            public void onError(Exception ex) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DebugActivity.this);
                dialogBuilder.setTitle("Fehler");
                dialogBuilder.setMessage("Ein Fehler ist aufgetreten: " + ex.getMessage());
                dialogBuilder.show();
            }
        });

        mSchoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                School selectedSchool = mSchoolList.get(position);
                APIResult<List<UserClass>> classResult = schoolController.getClassesBySchool(selectedSchool.getSchool_ID());

                classResult.setResultHandler(new ResultHandler<List<UserClass>>() {
                    @Override
                    public void onCacheResult(List<UserClass> param) {
                        onSuccess(param);
                    }

                    @Override
                    public void onSuccess(List<UserClass> classList) {
                        ClassSpinnerAdapter adapter = new ClassSpinnerAdapter(DebugActivity.this, classList);
                        mClassSpinner.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Exception ex) {
                        //onSuccess(new ArrayList<UserClass>());

                        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        dialogBuilder.setTitle("Fehler");
                        dialogBuilder.setMessage(ex.getMessage());
                        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialogBuilder.show();*/
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        User authUser = new User();
        authUser.setUserName("alexnavratil");
        authUser.setPassword("asdfgh");

        final APIResult<User> loginResult = userController.authenticate(authUser);
        loginResult.setResultHandler(new ResultHandler<User>() {
            @Override
            public void onSuccess(User currentUser) {
                Toast.makeText(DebugActivity.this, "Login successful", Toast.LENGTH_SHORT).show();


                Toast.makeText(DebugActivity.this, "Hallo " + currentUser.getUserName() + " (" + currentUser.getLanguage().getInLanguage(), Toast.LENGTH_SHORT).show();
                Log.i("IIC", "Type: " + currentUser.getUserType().getDescription() + ", school: " + currentUser.getUserClass().getSchool().getSchoolname());

                QuizController quizController = coco.create(QuizController.class);
                APIResult<List<QuizCategory>> quizCategoryResult = quizController.getQuizCategories();

                quizCategoryResult.setResultHandler(new ResultHandler<List<QuizCategory>>() {
                    @Override
                    public void onCacheResult(List<QuizCategory> param) {
                        Log.i("IIC", "CACHE");
                        onSuccess(param);
                    }

                    @Override
                    public void onSuccess(List<QuizCategory> param) {
                        for (QuizCategory category : param) {
                            Log.i("IIC", "QuizCategory: " + category.getDescription());
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        ex.printStackTrace();
                    }
                });

                quizController.getQuizzesOfCategory(1).setResultHandler(new ResultHandler<List<Quiz>>() {
                    @Override
                    public void onCacheResult(List<Quiz> param) {
                        onSuccess(param);
                    }

                    @Override
                    public void onSuccess(List<Quiz> param) {
                        for (Quiz q : param) {
                            Log.i("IIC", q.getName());
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        ex.printStackTrace();
                    }
                });

                quizController.getFinishedQuizSession(27).setResultHandler(new ResultHandler<QuizResult>() {
                    @Override
                    public void onCacheResult(QuizResult param) {
                        Log.i("IIC",
                                "QuizResult: " + param.getSession_ID()
                                        + ", quiz.Name: " + param.getQuiz().getName()
                                        + ", Questions: " + param.getQuestions().size()
                                        + ", Session_Question: " + param.getQuestions().get(0).getQuestion().getQ_HTML());
                    }

                    @Override
                    public void onSuccess(QuizResult param) {

                    }

                    @Override
                    public void onError(Exception ex) {
                        ex.printStackTrace();
                    }
                });

                quizController.getFinishedQuizzes().setResultHandler(new ResultHandler<List<QuizResult>>() {
                    @Override
                    public void onCacheResult(List<QuizResult> param) {
                        for (QuizResult result : param) {
                            Log.i("IIC", "QuizResult: " + result.getSession_ID());
                        }
                    }

                    @Override
                    public void onSuccess(List<QuizResult> param) {

                    }

                    @Override
                    public void onError(Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                Toast.makeText(DebugActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
