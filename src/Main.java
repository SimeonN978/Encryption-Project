import Java.Controller.AuthenticationController;
import Java.Controller.DashBoardController;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        // 1. Initialize state (trading engine, account store, etc.)
        //TODO: Project code here

        // 2. Configure Spark (port, HTTPS later)\
        port(4567);
        staticFiles.location("/public");

        // 3. Define routes and start server
        AuthenticationController authRoutes = new AuthenticationController();
        authRoutes.register();

        DashBoardController dashRoutes = new DashBoardController();
        dashRoutes.register();
    }
}
