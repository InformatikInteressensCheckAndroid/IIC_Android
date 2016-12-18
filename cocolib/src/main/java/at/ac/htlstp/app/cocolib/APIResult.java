/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.app.cocolib;

/**
 * @author alexnavratil
 */
public class APIResult<T> {
    private ResultHandler<T> resultHandler;
    private boolean cacheSet;
    private boolean successSet;
    private boolean errorSet;

    private T cacheObject;
    private T successObject;
    private Exception exceptionObject;

    public APIResult() {
    }

    public void setResultHandler(ResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;

        notifyResultCacheHandler();
        notifyResultSuccessHandler();
        notifyResultErrorHandler();
    }

    public void notifyCacheHandler(T param) {
        cacheSet = true;
        this.cacheObject = param;

        notifyResultCacheHandler();
    }

    public void notifySuccessHandler(T param) {
        successSet = true;
        this.successObject = param;

        notifyResultSuccessHandler();
    }

    public void notifyErrorHandler(Exception param) {
        errorSet = true;
        this.exceptionObject = param;

        notifyResultErrorHandler();
    }

    private void notifyResultCacheHandler() {
        if (resultHandler != null && cacheSet) {
            resultHandler.onCacheResult(cacheObject);
        }
    }

    private void notifyResultSuccessHandler() {
        if (resultHandler != null && successSet) {
            resultHandler.onSuccess(successObject);
        }
    }

    private void notifyResultErrorHandler() {
        if (resultHandler != null && errorSet) {
            resultHandler.onError(exceptionObject);
        }
    }
}
