import Java.Controller.UserSignUp;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        // 1. Initialize state (trading engine, account store, etc.)

        // 2. Configure Spark (port, HTTPS later)\
        port();
        // 3. Define routes
        get("/signup", UserSignUp::createUser);



    }
}
