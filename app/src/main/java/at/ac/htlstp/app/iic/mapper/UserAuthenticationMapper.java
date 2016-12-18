package at.ac.htlstp.app.iic.mapper;

import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;

import at.ac.htlstp.app.cocolib.APIResult;
import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.cocolib.ObjectMappingParser;
import at.ac.htlstp.app.cocolib.ResultHandler;
import at.ac.htlstp.app.iic.controller.UserController;
import at.ac.htlstp.app.iic.model.User;

/**
 * Created by alexnavratil on 22/12/15.
 */
public class UserAuthenticationMapper implements ObjectMappingParser<User> {
    private final CountDownLatch userLatch = new CountDownLatch(1);
    private User returnedUser = null;

    @Override
    public User parseToObject(LinkedHashMap parseMap, CocoLibConfiguration configuration) {
        CocoLib cocoLib = new CocoLib(configuration);
        final UserController userController = cocoLib.create(UserController.class);

        APIResult<User> currentUserResult = userController.getCurrentUser();

        currentUserResult.setResultHandler(new ResultHandler<User>() {
            @Override
            public void onSuccess(User param) {
                returnedUser = param;
                returnedUser.setLocalUser(true);
                userLatch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                userLatch.countDown();
            }
        });

        try {
            userLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return returnedUser;
    }
}
