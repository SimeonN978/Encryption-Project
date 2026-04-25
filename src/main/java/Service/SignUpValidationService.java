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
            return "Username already exists or does not meet requirements (3-32 characters containing letters, numbers, or underscores)";
        }
        if(!isValidPassword(password)){
            return "Password not strong enough";
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
        //validate that the user entered a legitimate email string
        return  email != null &&
                email.length() <= 255 &&
                email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    //Make Sure users have passwords that fulfill certain strength requirements.
    private static boolean isValidPassword(String password){
        if (password.length() < 8 || password.length() > 35){ // BCrypt limit can go up to 72 characters
            return false;
        }

        return checkChar(password);
    }

    private boolean isValidUserName(String username){
        return (username != null && !accountStore.exists(username) && username.matches("^[A-Za-z0-9_]{3,32")); // Username is small enough and the username does not already exist and it matches allow list
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
