package Java.Service;

import Java.Model.account.AccountStore;

public class AuthenticationService {
    private static final AccountStore accountStore = AccountStore.getInstance();

    public static boolean authenticateLogin(String username, String password){
        if(!accountStore.exists(username)){
            return false; // User does not exist
        }

        return EncryptionService.verifyPassword(password, accountStore.getPasswordHash(username));
    }

    public static String validateSignUp(String username, String password){
        if(username == null || password == null){
            return "Username and password required";
        }

        if(AuthenticationService.invalidUserName(username)){
            return "Username already exists";
        }

        // Valid Sign up at this point
        // Hash password before making account for security
        // Generate hashed password with users given password + salt that is generated
        String hashedPWD = EncryptionService.hashPassword(password);
        accountStore.add(username, hashedPWD);
        return "Success";
    }

    private static boolean invalidUserName(String username){
        return accountStore.exists(username);
    }
}
