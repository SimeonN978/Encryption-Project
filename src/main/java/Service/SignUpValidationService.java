package Service;

import Model.account.AccountStore;

public class SignUpValidationService {
    private final AccountStore accountStore;

    public SignUpValidationService(AccountStore accountStore){
        this.accountStore = accountStore;
    }

    public String validateSignUp(String username, String password, String email){
        if(username == null || username.isBlank() || password == null || password.isBlank()){
            return "Username and password required";
        }

        if(!isValidUserName(username)){
            return "Username already exists";
        }
        if(!isValidPassword(password)){
            return "Password does not meet requirements";
        }
        if(!isValidEmail(email)){
            return "Not a valid email";
        }

        // Valid Sign up at this point
        // Hash password before making account for security
        // Generate hashed password with users given password + salt that is generated
        String hashedPWD = HashService.hashPassword(password);
        accountStore.add(username, hashedPWD, email);
        return "Success";
    }

    private boolean isValidEmail(String email) {
        //TODO
        //validate that the user entered a legitimate email
        return true;
    }

    //Make Sure users have passwords that fulfill certain strength requirements.
    private static boolean isValidPassword(String password){
        if (password.length() < 8 || password.length() > 35){
            return false;
        }
        return checkChar(password);
    }

    private boolean isValidUserName(String username){
        return (username.length() <= 32 && !accountStore.exists(username)); // Username is small enough and the username does not already exist
    }

    // Password must have unique characters to be stronger
    private static boolean checkChar(String password){
        int upper =0;
        int lower =0;
        int unique =0;
        int nums =0;
        for (char c : password.toCharArray()){
            if(Character.isUpperCase(c) && Character.isLetter(c)){
                upper++;
            }
            else if (Character.isLowerCase(c) && Character.isLetter(c)){
                lower++;
            }
            else if (!Character.isLetter(c) && Character.isDigit(c)){
                nums++;
            }
            else if (!Character.isLetter(c) && !Character.isDigit(c) ){
                unique++;
            }
        }
        if(upper == 0 || lower==0 || unique ==0 || nums ==0){
            return false;
        }
        return true;

    }
}
