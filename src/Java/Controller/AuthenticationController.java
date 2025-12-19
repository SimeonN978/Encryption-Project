package Java.Controller;

import Java.Service.AuthenticationService;
import Java.Service.EncryptionService.*;

import spark.Request;
import spark.Response;

import spark.Session;

import static spark.Spark.*;

public class AuthenticationController {

    //Constructor
    public AuthenticationController() {}

    // Define User Authentication routes
    public void register() {
        //Show pages to user
        get("/", this::showIndexPage);
        get("/signup", this::showSignUpPage);
        get("/login", this::showLoginPage);

        //Handle Authentication
        post("/signup", this::handleSignUp);
        post("/login", this::handleLogin);
    }

    private Object handleLogin(Request request, Response response) {
        String username = request.queryParams("username");
        String password = request.queryParams("password");

        boolean valid = AuthenticationService.authenticateLogin(username, password);

        //Is username and password valid
        if(!valid){
            response.status(401);
            return "Invalid username or password";
        }

        // Valid login at this point
        Session session = request.session(true); // Make a new session for the user
        session.attribute("username", username);  // set session attribute

        response.redirect("/dashboard");
        return null;
    }

    private Object handleSignUp(Request request, Response response) {
        String username = request.queryParams("username");
        String password = request.queryParams("password");

        String signUpResult = AuthenticationService.validateSignUp(username, password);
        if(!signUpResult.equals("Success")){
            response.status(400);
            return signUpResult;
        }

        // Valid sign up at this point
        Session session = request.session(true); //Create new session for user
        session.attribute("username", username);  //Set session username

        response.redirect("/dashboard");
        return null;
    }

    //Show page helpers for lambda funcs
    private Object showLoginPage(Request request, Response response) {
        response.type("text/html");
        return HTMLRenderer.render("/public/login.html");
    }

    private Object showSignUpPage(Request request, Response response) {
        response.type("text/html");
        return HTMLRenderer.render("/public/sign-up.html");
    }

    private Object showIndexPage(Request request, Response response) {
        response.type("text/html");
        return HTMLRenderer.render("/public/index.html");
    }
}
