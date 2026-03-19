package Service;

import Model.account.AccountStore;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

public class UserService {
    private final AccountStore accountStore;

    public UserService(AccountStore accountStore){
        this.accountStore = accountStore;
    }

    public boolean authenticate(String username, String password){
        if(!this.accountStore.exists(username)){
            return false; // User does not exist
        }

        return HashService.verifyPassword(password, accountStore.getPasswordHash(username));
    }


    // make sure user is logged in before they can access the dashboard
    public static void authenticateDashboardAccess(Request request, Response response) {
        boolean authenticated = false;
        String username = request.session().attribute("username"); // does the client have an active session with a username

        // Check if the session has a valid username associated with it
        if(username != null && request.session(false) != null){
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
}
