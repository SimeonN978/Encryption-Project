package Service;

import Model.account.AccountStore;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

public class AuthorizationService {
    private final AccountStore accountStore;

    public AuthorizationService(AccountStore accountStore){
        this.accountStore = accountStore;
    }

    public boolean authenticate(String username, String password){
        if(!this.accountStore.exists(username)){
            return false; // User does not exist
        }

        return HashService.verifyPassword(password, accountStore.getPasswordHash(username));
    }


    // make sure user is logged in before they can access the dashboard
    public static void authorizeDashboardAccess(Request request, Response response) {
        String username = request.session().attribute("username"); // does the client have an active session with a username
        boolean authenticated = username != null && request.session(false) != null;

        // Invalid session --> do not let user access the dashboard
        // redirect them to default page
        if(!authenticated){
            // If not logged in, redirect to login page
            response.redirect("/login");
            // Halt request to prevent access to dashboard
            halt();
        }
    }
}
