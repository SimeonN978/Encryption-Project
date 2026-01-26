package Java.Controller;

import Java.Service.AuthenticationService;

import spark.Request;
import spark.Response;

import spark.Session;

import static spark.Spark.*;

public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RateLimiter rateLimiter;

    //Constructor
    public AuthenticationController(AuthenticationService authenticationService, RateLimiter rateLimiter) {
        this.authenticationService = authenticationService;
        this.rateLimiter = rateLimiter;
    }

    // Define User Authentication routes
    public void register() {
        //Show pages to user
        get("/", this::showIndexPage);
        get("/signup", this::showSignUpPage);
        get("/login", this::showLoginPage);

        //Handle Authentication
        before("/login", this::beforeLogin);
        post("/signup", this::handleSignUp);
        post("/login", this::handleLogin);
    }

    private void beforeLogin(Request request, Response response) {
        if(!"POST".equalsIgnoreCase(request.requestMethod())){
            return; // before() is a post AND get
        }

        String ip = request.ip();
        String username = request.queryParams("username");
        String key = (username == null) ? ip : ip + ":" + username;

        //Is the request not allowed via rateLimiter
        if(!rateLimiter.allowRequest(key)){
            response.redirect("/login?error=throttled");
            halt(429, "");
        }
    }

    private Object handleLogin(Request request, Response response) {
        String username = request.queryParams("username");
        String password = request.queryParams("password");

        boolean valid = authenticationService.authenticateLogin(username, password);

        //Is username and password valid
        if(!valid){
            response.redirect("/login?error=invalid");
            halt(401, "");
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

        String signUpResult = authenticationService.validateSignUp(username, password);
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
