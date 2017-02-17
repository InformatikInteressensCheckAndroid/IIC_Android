package at.ac.htlstp.app.iic.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.iic.CocoLibSingleton;
import at.ac.htlstp.app.iic.R;
import at.ac.htlstp.app.iic.controller.LanguageController;
import at.ac.htlstp.app.iic.controller.SchoolClassController;
import at.ac.htlstp.app.iic.databinding.FragmentAccountBinding;
import at.ac.htlstp.app.iic.error.ErrorHandler;
import at.ac.htlstp.app.iic.error.IICError;
import at.ac.htlstp.app.iic.error.IICResultHandler;
import at.ac.htlstp.app.iic.model.Country;
import at.ac.htlstp.app.iic.model.Language;
import at.ac.htlstp.app.iic.model.School;
import at.ac.htlstp.app.iic.model.User;
import at.ac.htlstp.app.iic.model.UserClass;
import at.ac.htlstp.app.iic.ui.activity.MainActivity;
import at.ac.htlstp.app.iic.util.MultiTextWatcher;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends IICFragment {
    public static final String USER_KEY = "user_key";
    private final List<School> searchResultSchoolList = new ArrayList<>();
    @Bind(R.id.languageSpinner)
    Spinner languageSpinner;
    @Bind(R.id.countrySpinner)
    Spinner countrySpinner;
    @Bind(R.id.classSpinner)
    Spinner classSpinner;

    private Integer user_id = null;
    private User mUser;
    private CocoLib mCocoLib;
    private LanguageController mLanguageController;
    private SchoolClassController mSchoolClassController;
    private String password2 = "";
    private List<Language> mLanguageList = new ArrayList<>();
    private List<Country> mCountryList = new ArrayList<>();
    private OnActionButtonClickedListener mActionButtonListener;
    private OnChangePasswordListener mChangePasswordListener;
    private List<School> mSchoolList = new ArrayList<>();
    private List<String> mSchoolStringList = new ArrayList<>();
    private School selectedSchool;
    private ArrayAdapter<String> schoolAdapter;
    private List<UserClass> mClassList;
    private List<SchoolSearchObject> mSchoolSearchCache = new ArrayList<>();
    private boolean showSchoolSearchResults = false;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment initialize(int user_id) {
        AccountFragment instance = new AccountFragment();

        Bundle b = new Bundle();
        b.putInt(USER_KEY, user_id);

        instance.setArguments(b);
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnActionButtonClickedListener) {
            mActionButtonListener = (OnActionButtonClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnActionButtonClickedListener");
        }

        if (context instanceof OnChangePasswordListener) {
            mChangePasswordListener = (OnChangePasswordListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChangePasswordListener");
        }
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mCocoLib = CocoLibSingleton.getInstance(getContext());
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.title = getString(R.string.account_settings);
        super.action_key = MainActivity.FRAGMENT_KEY_ACCOUNT_SETTINGS;

        if ((user_id = getArguments().getInt(USER_KEY, -1)) != -1) {
            Realm realm = Realm.getInstance(getContext());
            mUser = realm.copyFromRealm(realm.where(User.class).equalTo("User_ID", user_id).findFirst());

            realm.close();
        } else {
            mUser = new User();
        }

        //mCocoLib = CocoLibSingleton.getInstance(getContext());
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute();

        mLanguageController = mCocoLib.create(LanguageController.class);
        mSchoolClassController = mCocoLib.create(SchoolClassController.class);

        //initialize data binding
        FragmentAccountBinding binding = DataBindingUtil.inflate(getLayoutInflater(savedInstanceState), R.layout.fragment_account, container, false);
        binding.setUser(mUser);
        binding.setAccountFragment(this);

        View rootView = binding.getRoot();
        ButterKnife.bind(this, rootView);

        initUI();

        return rootView;
    }

    private void initUI() {
        loadLanguageSpinner();
        loadCountrySpinner();

        if (user_id == -1) {
            loadSchools();
            loadClassSpinner();
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language selectedLanguage = mLanguageList.get(position);
                mUser.setLanguage(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country selectedCountry = mCountryList.get(position);
                mUser.setCountry(selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.addClassButton)
    public void addClassButtonClicked() {
        if (selectedSchool != null) {
            new MaterialDialog.Builder(getContext())
                    .title("neue Klasse hinzufügen")
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                    .input("Klassenname", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                        }
                    })
                    .positiveText("Klasse hinzufügen")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                            String className = dialog.getInputEditText().getText().toString();
                            LinkedHashMap<String, Object> classData = new LinkedHashMap<>();
                            classData.put("School_ID", selectedSchool.getSchool_ID());
                            classData.put("Classname", className);

                            final MaterialDialog progressDialog = new MaterialDialog.Builder(getContext())
                                    .content("Klasse wird hinzugefügt")
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .show();

                            SchoolClassController schoolClassController = mCocoLib.create(SchoolClassController.class);
                            schoolClassController.addClass(classData).setResultHandler(new IICResultHandler<String>(getContext()) {
                                @Override
                                public void onSuccess(String param) {
                                    loadClassSpinner();
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                }

                                @Override
                                public boolean onError(IICError error) {
                                    progressDialog.dismiss();
                                    return false;
                                }
                            });
                        }
                    })
                    .negativeText(R.string.cancel)
                    .show();
        } else {
            Toast.makeText(getContext(), "Bitte wählen Sie zuerst eine Schule aus!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.selectSchoolButton)
    public void selectSchoolButtonClick() {
        showSchoolSelectionDialog();
    }

    private void showSchoolSelectionDialog() {
        showSchoolSearchResults = false;

        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(R.string.choose_school_optional)
                .customView(R.layout.select_school_layout, false)
                .negativeText(R.string.cancel)
                .show();

        View view = dialog.getView();
        EditText searchInput = (EditText) view.findViewById(R.id.school_search);
        ListView searchResultListView = (ListView) view.findViewById(R.id.search_school_list);
        searchResultListView.setAdapter(schoolAdapter);

        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (showSchoolSearchResults) {
                    selectedSchool = searchResultSchoolList.get(position);
                } else {
                    selectedSchool = mSchoolList.get(position);
                }
                dialog.dismiss();
                loadClassSpinner();
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    showSchoolSearchResults = false;
                    updateSchoolAdapter();
                } else {
                    filterSchool(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }
        });
    }

    private void filterSchool(String searchInput) {
        searchInput = searchInput.toLowerCase();

        searchInput = searchInput.replace("st.", "sankt");
        searchInput = searchInput.replace("aegyd", "ägyd");
        searchInput = searchInput.replace("nms", "neue mittelschule");
        searchInput = searchInput.replace("htl", "höhere technische lehranstalt bundeslehranstalt");
        searchInput = searchInput.replace("bg", "bundes gymnasium");
        searchInput = searchInput.replace("brg", "bundes real gymnasium");

        final String search = searchInput;

        synchronized (searchResultSchoolList) {
            searchResultSchoolList.clear();
        }

        final List<SchoolSearchObject> cacheList = new ArrayList<>();

        Observable.from(mSchoolSearchCache)
                .map(new Func1<SchoolSearchObject, SchoolSearchObject>() {
                    @Override
                    public SchoolSearchObject call(SchoolSearchObject schoolSearchObject) {
                        String[] searchSplitted = search.split("[ ,]+");

                        int matchScore = 0;

                        for (String searchPart : searchSplitted) {
                            if (schoolSearchObject.getSearchString() != null) {
                                for (String sourcePart : schoolSearchObject.getSearchString()) {
                                    if (sourcePart.toLowerCase().contains(searchPart.toLowerCase())) {
                                        matchScore++;
                                    }
                                }
                            }
                        }

                        schoolSearchObject.setMatchScore(matchScore);
                        return schoolSearchObject;
                    }
                }).filter(new Func1<SchoolSearchObject, Boolean>() {
            @Override
            public Boolean call(SchoolSearchObject schoolSearchObject) {
                return schoolSearchObject.getMatchScore() > 0;
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SchoolSearchObject>() {
                    @Override
                    public void onCompleted() {
                        synchronized (cacheList) {
                            Collections.sort(cacheList, new Comparator<SchoolSearchObject>() {
                                @Override
                                public int compare(SchoolSearchObject lhs, SchoolSearchObject rhs) {
                                    return rhs.getMatchScore() - lhs.getMatchScore();
                                }
                            });
                        }

                        synchronized (searchResultSchoolList) {
                            for (SchoolSearchObject schoolSearchObject : cacheList) {
                                searchResultSchoolList.add(schoolSearchObject.getSchool());
                            }

                            showSchoolSearchResults = true;
                            updateSchoolAdapter();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SchoolSearchObject schoolSearchObject) {
                        synchronized (cacheList) {
                            cacheList.add(schoolSearchObject);
                        }
                    }
                });


    }

    private void loadClassSpinner() {
        Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                loadClassSpinner();
            }
        };

        if (selectedSchool != null) {
            mSchoolClassController.getClassesBySchool(selectedSchool.getSchool_ID()).setResultHandler(new IICResultHandler<List<UserClass>>(getContext(), tryAgainAction) {
                @Override
                public void onCacheResult(List<UserClass> param) {
                    onSuccess(param);
                }

                @Override
                public void onSuccess(List<UserClass> param) {
                    mClassList = param;
                    renderClassSpinner();
                }

                @Override
                public boolean onError(IICError error) {
                    return false;
                }
            });
        } else {
            renderClassSpinner();
        }
    }

    private void renderClassSpinner() {
        final List<String> classStringList = new ArrayList<>();
        classStringList.add(getString(R.string.select_no_class));

        if (mClassList != null) {
            Observable.from(mClassList)
                    .map(new Func1<UserClass, String>() {
                        @Override
                        public String call(UserClass userClass) {
                            return userClass.getClassname();
                        }
                    })
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            classStringList.add(s);
                        }
                    });
        }

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, classStringList);
        classSpinner.setAdapter(classAdapter);
    }

    private void loadSchools() {
        Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                loadSchools();
            }
        };

        mSchoolClassController.getSchools().setResultHandler(new IICResultHandler<List<School>>(getContext(), tryAgainAction) {
            @Override
            public void onCacheResult(List<School> param) {
                onSuccess(param);
            }

            @Override
            public void onSuccess(List<School> param) {
                mSchoolList = param;
                buildSchoolSearchCache();
                updateSchoolAdapter();
            }

            @Override
            public boolean onError(IICError error) {
                return false;
            }
        });
    }

    private void updateSchoolAdapter() {
        synchronized (mSchoolStringList) {
            if (schoolAdapter == null) {
                schoolAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mSchoolStringList);
            } else {
                mSchoolStringList.clear();
                synchronized (schoolAdapter) {
                    schoolAdapter.notifyDataSetChanged();
                }
            }
            synchronized (schoolAdapter) {

                List<School> sourceList;

                if (showSchoolSearchResults) {
                    sourceList = searchResultSchoolList;
                } else {
                    sourceList = mSchoolList;
                }

                Observable.from(sourceList)
                        .map(new Func1<School, String>() {
                            @Override
                            public String call(School school) {
                                return String.format("%d | %s, %d %s, %s %s", school.getSKZ(), school.getSchoolname(), school.getPLZ(), school.getVillage(), school.getStreet(), school.getHousenumber());
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                mSchoolStringList.add(s);
                            }
                        });

                schoolAdapter.notifyDataSetChanged();
            }
        }
    }

    private void buildSchoolSearchCache() {
        mSchoolSearchCache.clear();

        Observable.from(mSchoolList)
                .map(new Func1<School, SchoolSearchObject>() {

                    @Override
                    public SchoolSearchObject call(School school) {
                        String[] searchString = new String[]{Integer.toString(school.getSKZ()), school.getSchoolname(), Integer.toString(school.getPLZ()), school.getVillage(), school.getStreet(), school.getHousenumber()};
                        return new SchoolSearchObject(searchString, school);
                    }
                })
                .subscribe(new Action1<SchoolSearchObject>() {
                    @Override
                    public void call(SchoolSearchObject s) {
                        mSchoolSearchCache.add(s);
                    }
                });
    }

    @OnClick(R.id.actionButton)
    public void onActionButtonClick() {
        if (mActionButtonListener != null) {
            saveUser();

            if (user_id == -1 && !password2.equals(mUser.getPassword())) {
                ErrorHandler.showCustomErrorDialog(getContext(), getString(R.string.error), getString(R.string.pwd1_not_matching_pwd2));
            } else {
                mActionButtonListener.onActionButtonClicked(mUser);
            }
        }
    }

    @OnClick(R.id.changePassword)
    public void onPasswordChangeClick() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.change_password)
                .customView(R.layout.change_password, true)
                .positiveText(R.string.change_password)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog passwordDialog, @NonNull DialogAction which) {
                        EditText currentPassword = (EditText) passwordDialog.getView().findViewById(R.id.currentPassword);
                        EditText newPassword1 = (EditText) passwordDialog.getView().findViewById(R.id.newPassword1);
                        EditText newPassword2 = (EditText) passwordDialog.getView().findViewById(R.id.newPassword2);

                        if (newPassword1.getText().toString().equals(newPassword2.getText().toString())) {
                            if (currentPassword.getText().toString().equals(mUser.getPassword())) {
                                LinkedHashMap<String, Object> passwordData = new LinkedHashMap<>();

                                passwordData.put("OldPassword", currentPassword.getText().toString());
                                passwordData.put("NewPassword", newPassword1.getText().toString());

                                mChangePasswordListener.onChangePassword(passwordData, passwordDialog);
                            } else {
                                ErrorHandler.showCustomErrorDialog(getContext(), getString(R.string.error), getString(R.string.current_pwd_wrong));
                            }
                        } else {
                            ErrorHandler.showCustomErrorDialog(getContext(), getString(R.string.error), getString(R.string.pwd1_not_matching_pwd2));
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //do nothing
                    }
                })
                .cancelable(false)
                .show();
    }

    public TextWatcher getUsernameWatcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (user_id == -1) {
                    mUser.setUserName(s.toString());
                }
            }
        };
    }

    public TextWatcher getFirstNameWatcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUser.setFirstName(s.toString());
            }
        };
    }

    public TextWatcher getLastNameWatcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUser.setLastName(s.toString());
            }
        };
    }

    public TextWatcher getEmailWatcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUser.setEMail(s.toString());
            }
        };
    }

    public TextWatcher getStreetWatcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUser.setStreet(s.toString());
            }
        };
    }

    public TextWatcher getPLZWatcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        mUser.setPLZ(Integer.parseInt(s.toString()));
                    } catch (NumberFormatException ex) {
                        ErrorHandler.showCustomErrorDialog(getContext(), getString(R.string.error), getString(R.string.plz_must_be_a_number));
                    }
                }
            }
        };
    }

    public TextWatcher getHousenumberWatcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUser.setHousenumber(s.toString());
            }
        };
    }

    public TextWatcher getVillageWatcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUser.setVillage(s.toString());
            }
        };
    }

    public TextWatcher getPassword1Watcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUser.setPassword(s.toString());
            }
        };
    }

    public TextWatcher getPassword2Watcher() {
        return new MultiTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password2 = s.toString();
            }
        };
    }

    private void loadLanguageSpinner() {
        Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                loadLanguageSpinner();
            }
        };

        mLanguageController.getLanguages().setResultHandler(new IICResultHandler<List<Language>>(getContext(), tryAgainAction) {
            @Override
            public void onCacheResult(List<Language> param) {
                mLanguageList.clear();
                mLanguageList.addAll(param);
                renderLanguageSpinner();
            }

            @Override
            public void onSuccess(List<Language> param) {
                mLanguageList.clear();
                mLanguageList.addAll(param);
                renderLanguageSpinner();
            }

            @Override
            public boolean onError(IICError error) {
                return false;
            }
        });
    }

    private void loadCountrySpinner() {
        Runnable tryAgainAction = new Runnable() {
            @Override
            public void run() {
                loadCountrySpinner();
            }
        };

        mLanguageController.getCountries().setResultHandler(new IICResultHandler<List<Country>>(getContext(), tryAgainAction) {
            @Override
            public void onCacheResult(List<Country> param) {
                mCountryList.clear();
                mCountryList.addAll(param);
                renderCountrySpinner();
            }

            @Override
            public void onSuccess(List<Country> param) {
                mCountryList.clear();
                mCountryList.addAll(param);
                renderCountrySpinner();
            }

            @Override
            public boolean onError(IICError error) {
                return false;
            }
        });
    }

    private void renderLanguageSpinner() {
        if (getContext() == null) { //if activity already closed
            return;
        }
        final List<String> languageStringList = new ArrayList<>();

        Observable
                .from(mLanguageList)
                .map(new Func1<Language, String>() {
                    @Override
                    public String call(Language language) {
                        return language.getInLanguage();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        languageStringList.add(s);
                    }
                });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, languageStringList);
        languageSpinner.setAdapter(adapter);

        if (mUser.getLanguage() != null) {
            int userLanguageIndex = getUserLanguageIndex();
            languageSpinner.setSelection(userLanguageIndex);
        }
    }

    private int getUserLanguageIndex() {
        for (int i = 0; i < mLanguageList.size(); i++) {
            Language l = mLanguageList.get(i);
            if (l.getLanguage_ID() == mUser.getLanguage().getLanguage_ID()) {
                return i;
            }
        }
        return -1;
    }

    private int getUserCountryIndex() {
        for (int i = 0; i < mCountryList.size(); i++) {
            Country c = mCountryList.get(i);
            if (c.getCountry_ID() == mUser.getCountry().getCountry_ID()) {
                return i;
            }
        }
        return -1;
    }

    private void renderCountrySpinner() {
        if (getContext() == null) {
            return;
        }

        final List<String> countryStringList = new ArrayList<>();

        Observable
                .from(mCountryList)
                .map(new Func1<Country, String>() {
                    @Override
                    public String call(Country country) {
                        if (mUser.getLanguage() != null) {
                            switch (mUser.getLanguage().getShortcode()) {
                                case "en":
                                    return country.getEn();
                                case "de":
                                    return country.getDe();
                                case "es":
                                    return country.getEs();
                                case "fr":
                                    return country.getFr();
                                case "it":
                                    return country.getIt();
                                case "ru":
                                    return country.getRu();
                                default:
                                    return country.getEn();
                            }
                        }
                        return country.getEn();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        countryStringList.add(s);
                    }
                });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, countryStringList);
        countrySpinner.setAdapter(adapter);

        if (mUser.getCountry() != null) {
            int userCountryIndex = getUserCountryIndex();
            countrySpinner.setSelection(userCountryIndex);
        }
    }

    public Integer getUser_id() {
        return user_id;
    }

    private void saveUser() {
        if (user_id != null && user_id != -1 && mUser != null) {
            Realm realm = Realm.getInstance(getContext());

            User updatedUser = realm.where(User.class).equalTo("User_ID", mUser.getUser_ID()).findFirst();

            //this might be the case when the user clicks switch user when account settings are open
            //all tables get cleared on logout so updatedUser is not found anymore
            if (updatedUser != null) {
                //refresh password as it was changed when changing the password
                //otherwise it would get overridden
                mUser.setPassword(updatedUser.getPassword());

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(mUser);
                realm.commitTransaction();

                realm.close();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveUser();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public interface OnActionButtonClickedListener {
        void onActionButtonClicked(User user);
    }

    public interface OnChangePasswordListener {
        void onChangePassword(LinkedHashMap<String, Object> passwordData, MaterialDialog dialog);
    }

    private class SchoolSearchObject {
        private String[] searchString;
        private int matchScore = 0;
        private School school;

        public SchoolSearchObject(String[] searchString, School school) {
            this.searchString = searchString;
            this.school = school;
        }

        public String[] getSearchString() {
            return searchString;
        }

        public void setSearchString(String[] searchString) {
            this.searchString = searchString;
        }

        public int getMatchScore() {
            return matchScore;
        }

        public void setMatchScore(int matchScore) {
            this.matchScore = matchScore;
        }

        public School getSchool() {
            return school;
        }

        public void setSchool(School school) {
            this.school = school;
        }
    }
}
