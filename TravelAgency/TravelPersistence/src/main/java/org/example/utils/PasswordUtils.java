package org.example.utils;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    public static boolean checkPassword(String enteredPassword, String storedHash) {
        return BCrypt.checkpw(enteredPassword, storedHash);
    }
}
