package Java.Service;

import Java.Model.account.AccountStore;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

public class AuthenticationService {
    private static final AccountStore accountStore = AccountStore.getInstance();

    public static boolean authenticateLogin(String username, String password){
        if(!accountStore.exists(username)){
            return false; // User does not exist
        }

        return EncryptionService.verifyPassword(password, accountStore.getPasswordHash(username));
    }


    //Check if a password meets valid requirements and if the user already exists.
    public static String validateSignUp(String username, String password){
        if(username == null || password == null){
            return "Username and password required";
        }

        if(AuthenticationService.invalidUserName(username)){
            return "Username already exists";
        }
        if(!requireValidPassword(password)){
            return "Password Does not meet requirments";
        }

        // Valid Sign up at this point
        // Hash password before making account for security
        // Generate hashed password with users given password + salt that is generated
        String hashedPWD = EncryptionService.hashPassword(password);
        accountStore.add(username, hashedPWD);
        return "Success";
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

    //Make sure UserNames are not too long. Save memory allocation space.
    private static boolean checkValidUser(String name){
        if (name.length() >32 ){
            return false;
        }
        return true;
    }
    //Make Sure users have passwords that fulfill certain strength requirements.
    private static boolean requireValidPassword(String password){
        if (password.length() < 8 || password.length() > 35){
            return false;
        }
        return checkChar(password);
    }
// Password must have unique characters to be stronger
    private static boolean checkChar(String password){
        int upper =0;
        int lower =0;
        int unique =0;
        for (char c : password.toCharArray()){
            if(Character.isUpperCase(c) && Character.isLetter(c)){
                upper++;
            }
            else if (Character.isLowerCase(c) && Character.isLetter(c)){
                lower++;
            }
            else if (!Character.isLetter(c)){
                unique++;
            }
        }
        if(upper == 0 || lower==0 || unique ==0){
            return false;
        }
        return true;

    }


    private static boolean invalidUserName(String username){
        return accountStore.exists(username) && checkValidUser(username);
    }


}
