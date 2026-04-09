package Controller;

import Model.price.InvalidPriceException;
import Model.user.InvalidUserException;
import Model.user.User;
import Model.user.UserManager;
import Service.AuthorizationService;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashMap;
import java.util.Map;

import static Service.HTMLRenderer.render;
import static spark.Spark.*;

public class DashBoardController implements Controller {
    private final AuthorizationService authorizationService;

    //Constructor
    public DashBoardController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    //Define User Dashboard routes
    @Override
    public void register() {
        // before will filter any requests to dashboard (session authentication)
        // protected route
        before("/dashboard", AuthorizationService::authorizeDashboardAccess);
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
            session.removeAttribute("username");
            session.invalidate();
        }

        response.redirect("/");
        return null;
    }


    private Object handleDashboard(Request request, Response response) throws InvalidUserException, InvalidPriceException {
        AuthorizationService.authorizeDashboardAccess(request, response);

        String username = request.session().attribute("username");
        User user;
        try {
            user = UserManager.getInstance().getUser(username);
        } catch (InvalidUserException e) {
            UserManager.getInstance().addUser(username);
            user = UserManager.getInstance().getUser(username);
        }

        // Pass data to HTML template engine // Renderer
        Map<String, Object> model = new HashMap<>();
        model.put("username", username);
        model.put("wallet", user.getWalletBalance());  // Assuming Wallet has getBalance()
        model.put("portfolio", user.getUserMap());
        model.put("watchlist", user.getCurrentMarkets()); // Or whatever other info you want

        response.status(200);
        response.type("text/html");
        return render("/private/dashboard.html", model);
    }
}
