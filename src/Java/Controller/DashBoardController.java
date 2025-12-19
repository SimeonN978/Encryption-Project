package Java.Controller;

import spark.Request;
import spark.Response;
import spark.Session;

import static spark.Spark.*;

public class DashBoardController {

    //Constructor
    public DashBoardController() {}

    //Define User Dashboard routes
    public void register() {
        // before will filter any requests to dashboard (session authentication)
        // protected route
        before("/dashboard", this::authenticateDashboardAccess);
        get("/dashboard", this::handleDashboard);

        post("/logout", this::handleLogout);
        post("/place-order", this::handleNewOrder);
    }

    private Object handleNewOrder(Request request, Response response) {
        //TODO
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

    // make sure user is logged in before they can access the dashboard
    private void authenticateDashboardAccess(Request request, Response response) {
        boolean authenticated = false;
        String username = request.session().attribute("username"); // does the client have an active session with a username

        // Check if the session has a valid username associated with it
        if(username != null || request.session(false) == null){
            authenticated = true;
        }

        // Invalid session --> do not let user access the dashboard
        // redirect them to default page
        if(!authenticated){
            // If not logged in, redirect to login page
            response.redirect("/login");
            // Halt request to prevent access to dashboard
            halt();
        }
    }

    private Object handleDashboard(Request request, Response response) {
        //Only return if the username and session are valid
        response.type("text/html");
        return HTMLRenderer.render("/View/dashboard.html");
    }
}
