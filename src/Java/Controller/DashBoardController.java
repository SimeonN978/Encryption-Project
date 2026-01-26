package Java.Controller;

import Java.Service.AuthenticationService;
import spark.Request;
import spark.Response;
import spark.Session;

import static spark.Spark.*;

public class DashBoardController {
    private final AuthenticationService authenticationService;

    //Constructor
    public DashBoardController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //Define User Dashboard routes
    public void register() {
        // before will filter any requests to dashboard (session authentication)
        // protected route
        before("/dashboard", AuthenticationService::authenticateDashboardAccess);
        get("/dashboard", this::handleDashboard);

        post("/logout", this::handleLogout);
        post("/place-order", this::handleNewOrder);
    }

    private Object handleNewOrder(Request request, Response response) {
        //TODO
        // fetch all of the parameters for an order.
        // check if the value of the order exeeds the value of the User's wallet.
        //if not, then you can place it. Maybe done like this( ProductManager.getInstance.addTradable())

        return null;
    }

    //Checks if the user session is valid
    //  If so, invalidate it
    //Redirect user to the entry point
    private Object handleLogout(Request request, Response response) {
        Session session = request.session(false);

        if(session != null){
            session.removeAttribute("user");
            session.invalidate();
        }

        response.redirect("/");
        return null;
    }



    private Object handleDashboard(Request request, Response response) {
        //Only return if the username and session are valid
        response.type("text/html");
        return HTMLRenderer.render("/View/dashboard.html");
    }
}
