package Java.Service;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptionService {
    public static String hashPassword(String plainTextPWD){
        return BCrypt.hashpw(plainTextPWD, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String plainTextPWD, String hashedPWD){
        return BCrypt.checkpw(plainTextPWD, hashedPWD);
    }
}
