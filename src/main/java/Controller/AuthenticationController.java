package Controller;

import Model.price.InvalidPriceException;
import Model.user.InvalidUserException;
import Model.user.UserManager;
import Service.UserService;

import Service.SignUpValidationService;
import spark.Request;
import spark.Response;

import spark.Session;

import static Controller.HTMLRenderer.render;
import static spark.Spark.*;

public class AuthenticationController {
    private final UserService authenticationService;
    private final SignUpValidationService signUpService;
    private final RateLimiter rateLimiter;

    //Constructor
    public AuthenticationController(UserService authenticationService, SignUpValidationService signUpService, RateLimiter rateLimiter) {
        this.authenticationService = authenticationService;
        this.signUpService = signUpService;
        this.rateLimiter = rateLimiter;
    }

    // Define User Authentication routes
    public void register() {
        //Show pages to user
        get("/", this::showIndexPage);
        get("/index", this::showIndexPage);
        get("/signup", this::showSignUpPage);
        get("/login", this::showLoginPage);

        //Handle Authentication
        before("/login", this::beforeLogin);
        post("/signup", this::handleSignUp);
        post("/login", this::handleLogin);
    }

    private String buildLoginKey(Request request){
        String ip = request.ip();
        String username = request.queryParams("username");

        if(username == null || username.isBlank()){
            return ip;
        }

        return ip + ":" + username.trim().toLowerCase();
    }

    private void beforeLogin(Request request, Response response) {
        if(!"POST".equalsIgnoreCase(request.requestMethod())){
            return; // before() is a post AND get
        }

        String key = buildLoginKey(request);

        //Is the request not allowed via rateLimiter
        if(!rateLimiter.allowRequest(key)){
            halt(429, "Slow down! Too many failed logins. Account has been restricted for a set amount of time.");
        }
    }

    private Object handleLogin(Request request, Response response) throws InvalidPriceException, InvalidUserException {
        String username = request.queryParams("username");
        String password = request.queryParams("password");

        boolean valid = authenticationService.authenticate(username, password);

        //Is username and password valid
        if(!valid){
            response.status(400);
            return "Invalid username or password";
        }

        // Valid login at this point
        String key = buildLoginKey(request);
        rateLimiter.onSuccessfulLogin(key);

        // Ensure the logged-in user exists in UserManager
        try {
            UserManager.getInstance().getUser(username);
        } catch (InvalidUserException e) {
            UserManager.getInstance().addUser(username);
        }

        Session session = request.session(true); // Make a new session for the user
        session.attribute("username", username);  // set session attribute

        response.redirect("/dashboard");
        return null;
    }

    private Object handleSignUp(Request request, Response response) throws InvalidPriceException, InvalidUserException {
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        String email = request.queryParams("email");

        String signUpResult = signUpService.validateSignUp(username, password, email); // Calls the password checkers.
        //If signup requirements are not valid, send sign up error to client
        if(!signUpResult.equals("Success")){
            response.status(400);
            return signUpResult;
        }

        // Valid sign up at this point
        //Add User to the UserManager when there is a successful signup
        UserManager.getInstance().addUser(username);

        response.status(200);
        return "Success";
    }

    //Show page helpers for lambda funcs
    private Object showLoginPage(Request request, Response response) {
        response.status(200);
        response.type("text/html");
        return render("/views/login.html");
    }

    private Object showSignUpPage(Request request, Response response) {
        response.status(200);
        response.type("text/html");
        return render("/views/signup.html");
    }

    private Object showIndexPage(Request request, Response response) {
        response.status(200);
        response.type("text/html");
        return render("/views/index.html");
    }
}
