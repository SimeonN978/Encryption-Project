import Java.Controller.AuthenticationController;
import Java.Controller.DashBoardController;
import Java.Controller.RateLimiter;
import Java.Model.account.AccountStore;
import Java.Service.AuthenticationService;

import java.time.Duration;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        // 1. Initialize state (trading engine, account store, etc.)
        //TODO: Project code here
        AccountStore accountStore = new AccountStore();

        AuthenticationService authenticationService = new AuthenticationService(accountStore);

        RateLimiter rateLimiter = new RateLimiter(5, Duration.ofMinutes(15)); // 5 attempts per minute until hard throttle

        // 2. Configure Spark (port, HTTPS later)\
        port(4567);
        staticFiles.location("/public");

        // 3. Define routes and start server
        AuthenticationController authRoutes = new AuthenticationController(authenticationService, rateLimiter);
        authRoutes.register();

        DashBoardController dashRoutes = new DashBoardController(authenticationService);
        dashRoutes.register();
    }
}
