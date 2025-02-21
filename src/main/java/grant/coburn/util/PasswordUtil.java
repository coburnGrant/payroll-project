package grant.coburn.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static void print() {
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Hashed password for '" + password + "':");
        System.out.println(hashedPassword);
    }
} 