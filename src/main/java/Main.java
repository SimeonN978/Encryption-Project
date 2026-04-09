import Controller.AuthenticationController;
import Controller.Controller;
import Controller.DashBoardController;
import Controller.MFAController;
import Service.MFAService;
import Service.RateLimiter;
import Model.account.AccountStore;
import Service.UserService;
import Service.SignUpValidationService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        // 1. Initialize state (trading engine, account store, etc.)
        //TODO: Project code here
        AccountStore accountStore = new AccountStore();

        // Create Services
        UserService authenticationService = new UserService(accountStore);
        SignUpValidationService signUpValidationService = new SignUpValidationService(accountStore);
        MFAService mfaService = new MFAService();

        RateLimiter rateLimiter = new RateLimiter(5, Duration.ofMinutes(15)); // 5 attempts per minute until hard throttle

        // 2. Configure Spark (port, HTTPS later)\
        port(4567);
        staticFiles.location("/public");

        // 3. Define routes and start server
        AuthenticationController authRoutes = new AuthenticationController(authenticationService, signUpValidationService,rateLimiter);
        authRoutes.register();

        MFAController mfaRoutes = new MFAController(mfaService);
        mfaRoutes.register();

        DashBoardController dashRoutes = new DashBoardController(authenticationService);
        dashRoutes.register();

        //If there is an error, print the stack trace
        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
        });
    }
}
