package Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

public class MFAService {
    private final GoogleAuthenticator googleAuthenticator;

    public MFAService(){
        this.googleAuthenticator = new GoogleAuthenticator();
    }

    // Create a new MFA Secret
    // To be used every time a new account signs up
    public String getMFASecret(String username){
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(username);

        return key.getKey(); // store this in Account store TODO
    }

    // Create a QR code and return it
    // To be used during login
    public Object getQRCode(String username, GoogleAuthenticatorKey key){
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
               "CrypTrader", username, key
        );

        return otpAuthURL; //TODO
    }

    // Check if OTP user entered is valid
    // To be used during login
    public boolean isValid(){
        //TODO
        return true;
    }
}
