package Java.Controller;

import Java.Model.user.InvalidUserException;
import Java.Model.user.User;
import Java.Model.user.UserManager;
import Java.Service.AuthenticationService;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.*;
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

        if (session != null) {
            session.removeAttribute("user");
            session.invalidate();
        }

        response.redirect("/");
        return null;
    }


    private Object handleDashboard(Request request, Response response) throws InvalidUserException {


        AuthenticationService.authenticateDashboardAccess(request, response);

        String username = request.session().attribute("username");
        User user = UserManager.getInstance().getUser(username);

        // Pass data to HTML template engine // Renderer
        Map<String, Object> model = new HashMap<>();
        model.put("username", username);
        model.put("wallet", user.getWalletBalance());  // Assuming Wallet has getBalance()
        model.put("portfolio", user.getUserMap());
        model.put("watchlist", user.getCurrentMarkets()); // Or whatever other info you want

        return HTMLRenderer.render("/View/dashboard.html", model);
    }


//        OLD handle Dashboard Code:
//        //Only return if the username and session are valid
//        response.type("text/html");
//        return HTMLRenderer.render("/View/dashboard.html");
//   }
}
