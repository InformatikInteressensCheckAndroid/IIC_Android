package at.ac.htlstp.app.cocolib;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.ac.htlstp.app.cocolib.annotation.BatchDatabaseAccessor;
import at.ac.htlstp.app.cocolib.annotation.MappingClass;
import at.ac.htlstp.app.cocolib.annotation.ParseMapping;
import at.ac.htlstp.app.cocolib.annotation.PostRequestSerializer;
import at.ac.htlstp.app.cocolib.annotation.method.GET;
import at.ac.htlstp.app.cocolib.annotation.method.POST;
import at.ac.htlstp.app.cocolib.exception.ValidatorException;
import at.ac.htlstp.app.cocolib.helper.GetHelper;
import at.ac.htlstp.app.cocolib.helper.ParseMappingHelper;
import at.ac.htlstp.app.cocolib.helper.ResponseValidatorHelper;
import at.ac.htlstp.app.cocolib.helper.RootParseMappingHelper;
import at.ac.htlstp.app.cocolib.helper.Variable;
import at.ac.htlstp.app.cocolib.network.VolleySingleton;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author alexnavratil
 */
public class CocoLib {
    private CocoLibConfiguration configuration;
    private boolean detached = false;

    public CocoLib(CocoLibConfiguration configuration) {
        this.configuration = configuration;
    }

    public void cancelAll() {
        detached = true;
    }

    public <T> T create(Class<T> apiClass) {
        Class proxyClass = Proxy.getProxyClass(apiClass.getClassLoader(), apiClass);

        InvocationHandler invocationHandler = new CocoLibInvocationHandler(apiClass);

        T controllerObj = null;
        try {
            controllerObj = (T) proxyClass.getConstructor(new Class[]{InvocationHandler.class}).newInstance(new Object[]{invocationHandler});
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return controllerObj;
    }

    public class CocoLibInvocationHandler implements InvocationHandler {
        private Class realControllerClass;

        public CocoLibInvocationHandler(Class realControllerClass) {
            this.realControllerClass = realControllerClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            final List<Variable> variableList = new ArrayList<>();
            Object postBodyObject = null;

            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (i < parameterAnnotations.length) {
                        Annotation[] paramAnnotation = parameterAnnotations[i];
                        for (int j = 0; j < paramAnnotation.length; j++) {
                            Class<? extends Annotation> annotationType = paramAnnotation[j].annotationType();
                            switch (annotationType.getSimpleName()) {
                                case "Variable":
                                    Method variableMethod = annotationType.getDeclaredMethod("identifier");
                                    String identifierName = variableMethod.invoke(paramAnnotation[j]).toString();
                                    variableList.add(new Variable(identifierName, args[i]));
                                    break;
                                case "PostBody":
                                    if (postBodyObject != null) {
                                        throw new IllegalStateException("@PostBody annotation can only be defined once");
                                    }

                                    postBodyObject = args[i];
                                    break;
                            }
                            if (annotationType.getSimpleName().equals("Variable")) {
                                Method variableMethod = annotationType.getDeclaredMethod("identifier");
                                String identifierName = variableMethod.invoke(paramAnnotation[j]).toString();
                                variableList.add(new Variable(identifierName, args[i].toString()));
                            }
                        }
                    } else {
                        break;
                    }
                }
            }

            String methodUrl = null;
            final List<ResponseValidatorHelper> responseValidatorList = new ArrayList<>();
            final List<ParseMappingHelper> parseMappingList = new ArrayList<>();
            final LinkedHashMap requestParseMap = new LinkedHashMap();
            GET getAnnotation = null;
            POST postAnnotation = null;
            MappingClass mappingClass = null;
            RootParseMappingHelper rootParseMapping = null;
            PostRequestSerializer postRequestSerializer = null;

            if (postBodyObject != null) {
                requestParseMap.putAll(configuration.getMapParser().parseFromObject(postBodyObject));
            }

            for (Annotation annotation : method.getAnnotations()) {
                Class<? extends Annotation> annotationType = annotation.annotationType();

                switch (annotationType.getSimpleName()) {
                    case "GET":
                        getAnnotation = (GET) annotation;

                        if (methodUrl != null) {
                            throw new IllegalStateException("You can only define @GET once");
                        }

                        methodUrl = GetHelper.injectVariableList(getAnnotation.value(), variableList);
                        break;
                    case "POST":
                        postAnnotation = (POST) annotation;

                        if (methodUrl != null) {
                            throw new IllegalStateException("You can only define @POST once");
                        }

                        methodUrl = GetHelper.injectVariableList(postAnnotation.value(), variableList);
                        break;
                    case "ResponseValidator":
                        responseValidatorList.add(ResponseValidatorHelper.processAnnotation(annotation));
                        break;
                    case "ParseMapping":
                        parseMappingList.add(new ParseMappingHelper((ParseMapping) annotation));
                        break;
                    case "RootParseMapping":
                        if (rootParseMapping == null) {
                            rootParseMapping = RootParseMappingHelper.processAnnotation(annotation);
                        } else {
                            throw new IllegalStateException("You can only define @RootParseMapping once");
                        }
                        break;
                    case "PostRequestSerializer":
                        if (postRequestSerializer != null) {
                            throw new IllegalStateException("You can only define @PostRequestSerializer once");
                        }
                        postRequestSerializer = (PostRequestSerializer) annotation;
                        RequestSerializer postRequestSerializerInstance = postRequestSerializer.serializerClass().newInstance();
                        LinkedHashMap tmpRequestParseMap = postRequestSerializerInstance.serialize(requestParseMap);

                        requestParseMap.clear();
                        requestParseMap.putAll(tmpRequestParseMap);

                        break;
                    case "MappingClass":
                        mappingClass = (MappingClass) annotation;
                        break;
                }
            }

            if (mappingClass == null) {
                throw new IllegalStateException("The annotation @MappingClass must be defined. accessorClass must be the class where the DatabaseAccessor-Annotation is set. This is normally the model-class.");
            } else if (rootParseMapping == null) {
                throw new IllegalStateException("There's no RootParseMapping defined");
            }

            final APIResult result = new APIResult();

            /**
             * GET CACHED RESULTS FROM DATABASE AND CALL CACHED LISTENER
             */
            boolean batch = false;
            if (mappingClass.wrappedBy().equals(List.class) || mappingClass.wrappedBy().equals(ArrayList.class)) {
                batch = true;
            }

            final boolean finalBatch = batch;
            final MappingClass finalMappingClass = mappingClass;
            final RootParseMappingHelper finalRootParseMapping = rootParseMapping;

            Observable.just(null).observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            try {
                                processCachedListener(finalMappingClass.accessorClass(), result, variableList, finalBatch);
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (!detached)
                                result.notifyErrorHandler((Exception) throwable);
                        }
                    });

            int requestMethod = Request.Method.GET;
            if (postAnnotation != null) {
                requestMethod = Request.Method.POST;
            }

            final StringRequest request = new StringRequest(requestMethod, configuration.getAPIUrl().toString() + methodUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {

                    final Observable<Object> myObservable = Observable.just(response)
                            .observeOn(Schedulers.newThread())
                            .map(new Func1<String, LinkedHashMap>() {
                                @Override
                                public LinkedHashMap call(String s) {
                                    return configuration.getMapParser().parse(response);
                                }
                            })
                            .map(new Func1<LinkedHashMap, LinkedHashMap>() {
                                @Override
                                public LinkedHashMap call(LinkedHashMap parseMap) {
                                    try {
                                        return validateResponse(responseValidatorList, parseMap);
                                    } catch (InstantiationException e) {
                                        result.notifyErrorHandler(e);
                                    } catch (IllegalAccessException e) {
                                        result.notifyErrorHandler(e);
                                    }
                                    return null;
                                }
                            })
                            .doOnNext(new Action1<LinkedHashMap>() {
                                @Override
                                public void call(LinkedHashMap hashMap) {
                                    Collections.sort(parseMappingList);
                                }
                            })
                            .observeOn(Schedulers.newThread())
                            .map(new Func1<LinkedHashMap, Object>() {
                                @Override
                                public Object call(LinkedHashMap parseMap) {
                                    ObjectMappingParser parser = null;
                                    try {
                                        parser = finalRootParseMapping.createParserInstance();

                                        Object resultObject = parser.parseToObject(parseMap, configuration);

                                        return resultObject;
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InstantiationException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(new Func1<Object, Object>() {
                                @Override
                                public Object call(Object resultObject) {
                                    if (resultObject != null) {

                                        DatabaseAccessor<Object> dbAccessor = null;
                                        try {
                                            dbAccessor = getDatabaseAccessor(finalMappingClass.accessorClass(), finalBatch);
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (InstantiationException e) {
                                            e.printStackTrace();
                                        }

                                        if (dbAccessor != null) {
                                            dbAccessor.saveToDatabase(configuration.getContext(), resultObject);
                                        }
                                    }

                                    return resultObject;
                                }
                            });

                    myObservable
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Object>() {
                                @Override
                                public void call(Object resultObject) {
                                    if (!detached)
                                        result.notifySuccessHandler(resultObject);
                                    else {
                                        Log.i("CocoLib", "Not calling callback because CocoLib-Request was canceled.");
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (!detached)
                                        result.notifyErrorHandler((Exception) throwable);
                                }
                            });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (!detached)
                        result.notifyErrorHandler(error);
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    if (requestParseMap.size() != 0) {
                        return configuration.getMapParser().serializeToString(requestParseMap).getBytes();
                    }
                    return null;
                }

                @Override
                public String getBodyContentType() {
                    return configuration.getMapParser().getContentType();
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", configuration.getMapParser().getContentType());
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    //Log.i("CocoLib", new String(response.data)); //Response Logging
                    return super.parseNetworkResponse(response);
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(configuration.getContext()).addToRequestQueue(request);

            return result;
        }

        private LinkedHashMap validateResponse(List<ResponseValidatorHelper> responseValidatorList, LinkedHashMap parseMap) throws ValidatorException, InstantiationException, IllegalAccessException {
            for (ResponseValidatorHelper validator : responseValidatorList) {
                parseMap = validator.createValidatorInstance().parse(parseMap);
            }

            return parseMap;
        }

        /**
         * Returns the DatabaseAccessor implementation object of the given object
         *
         * @param objectClass class object where the DatabaseAccessor is defined
         * @return DatabaseAccessor implementation (not annotation) of class of given object
         */
        private DatabaseAccessor<Object> getDatabaseAccessor(Class objectClass, boolean batch) throws IllegalAccessException, InstantiationException {
            Annotation[] annotations = objectClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (batch && annotation.annotationType().equals(BatchDatabaseAccessor.class)) {
                    BatchDatabaseAccessor batchDatabaseAccessor = (BatchDatabaseAccessor) annotation;
                    return batchDatabaseAccessor.accessor().newInstance();
                } else if (!batch && annotation.annotationType().equals(at.ac.htlstp.app.cocolib.annotation.DatabaseAccessor.class)) {
                    at.ac.htlstp.app.cocolib.annotation.DatabaseAccessor dbAccessor = (at.ac.htlstp.app.cocolib.annotation.DatabaseAccessor) annotation;
                    return dbAccessor.accessor().newInstance();
                }
            }

            return null;
        }

        private void processCachedListener(Class objClass, final APIResult<Object> apiResult, List<Variable> variableList, boolean batch) throws InstantiationException, IllegalAccessException {
            DatabaseAccessor dbAccessor = getDatabaseAccessor(objClass, batch);
            if (dbAccessor != null) {
                APIResult<Object> dbResult = dbAccessor.getFromDatabase(configuration.getContext(), variableList);

                dbResult.setResultHandler(new ResultHandler<Object>() {
                    @Override
                    public void onCacheResult(Object param) {
                        if (!detached)
                            apiResult.notifyCacheHandler(param);
                    }

                    @Override
                    public void onSuccess(Object param) {
                        if (!detached)
                            apiResult.notifyCacheHandler(param);
                    }

                    @Override
                    public void onError(Exception ex) {
                        if (!detached)
                            apiResult.notifyErrorHandler(ex);
                    }
                });
            }
        }

    }

    public boolean isDetached() {
        return detached;
    }
}
