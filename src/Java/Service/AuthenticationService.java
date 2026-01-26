package Java.Service;

import Java.Model.account.AccountStore;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

public class AuthenticationService {
    private final AccountStore accountStore;

    public AuthenticationService(AccountStore accountStore){
        this.accountStore = accountStore;
    }

    public boolean authenticateLogin(String username, String password){
        if(!this.accountStore.exists(username)){
            return false; // User does not exist
        }

        return EncryptionService.verifyPassword(password, accountStore.getPasswordHash(username));
    }

    public String validateSignUp(String username, String password){
        if(username == null || password == null){
            return "Username and password required";
        }

        if(invalidUserName(username)){
            return "Username already exists";
        }

        // Valid Sign up at this point
        // Hash password before making account for security
        // Generate hashed password with users given password + salt that is generated
        String hashedPWD = EncryptionService.hashPassword(password);
        accountStore.add(username, hashedPWD);
        return "Success";
    }

    private boolean invalidUserName(String username){
        return accountStore.exists(username);
    }

    // make sure user is logged in before they can access the dashboard
    public static void authenticateDashboardAccess(Request request, Response response) {
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
}
