package grant.coburn.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to manage hashing and encrypting of passwords.
 * Can encrypt and check passwords using MD5 and BCrypt.
 */
public class PasswordUtil {
    /**
     * Encrypts a password using MD5 hash algorithm.
     * @param password The password to encrypt.
     * @return The hashed password using MD5.
     */
    public static String encryptPasswordMD5(String password) {
        String hashAlgorithm = "MD5";

        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);

            byte[] messageDigest = md.digest(password.getBytes());

            BigInteger number = new BigInteger(1, messageDigest);

            String hashText = number.toString(16);
            
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return "";
        }
    }

    /**
     * Encrypts a password using BCrypt hash algorithm.
     * @param password The password to encrypt.
     * @return The hashed password using BCrypt.
     */
    public static String bcryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Checks if a password matches a hashed password using BCrypt.
     * @param password The password to check.
     * @param hashedPassword The hashed password to compare against.
     * @return True if the password matches the hashed password, false otherwise.
     */
    public static boolean bcryptCheckPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * Checks if a password matches a hashed password using MD5.
     * @param password The password to check.
     * @param hashedPassword The hashed password to compare against.
     * @return True if the password matches the hashed password, false otherwise.
     */
    public static boolean checkPasswordMD5(String password, String hashedPassword) {
        String encryptedPassword = encryptPasswordMD5(password);
        return encryptedPassword.equals(hashedPassword);
    }

    /**
     * Validates if a password meets the required criteria:
     * - At least 8 characters long
     * - Contains at least one uppercase letter
     * - Contains at least one lowercase letter
     * - Contains at least one number
     * - Contains at least one special character
     * - No spaces allowed
     * @param password The password to validate
     * @return True if the password meets all criteria
     * @throws PasswordValidationException if the password doesn't meet the criteria
     */
    public static boolean isValidPassword(String password) throws PasswordValidationException {
        if (password == null) {
            throw new PasswordValidationException("Password cannot be null");
        }
        
        if (password.isEmpty()) {
            throw new PasswordValidationException("Password cannot be empty");
        }

        if (password.contains(" ")) {
            throw new PasswordValidationException("Password cannot contain spaces");
        }

        if (password.length() < 8) {
            throw new PasswordValidationException("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new PasswordValidationException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new PasswordValidationException("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            throw new PasswordValidationException("Password must contain at least one number");
        }

        if (!password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>/?].*")) {
            throw new PasswordValidationException("Password must contain at least one special character");
        }
        
        return true;
    }

    public static void main(String[] args) {
        String invalidPassword = "admin123";

        System.out.println("Invalid Password: " + invalidPassword + "\n");

        try {
            isValidPassword(invalidPassword);
        } catch (PasswordValidationException e) {
            System.err.println(e.getMessage());
        }

        String password = "Admin123!";

        System.out.println("\nValid Password: " + password + "\n");

        try {
            isValidPassword(password);
        } catch (PasswordValidationException e) {
            System.err.println(e.getMessage());
        }

        System.err.println("Password is valid");

        String md5Hash = encryptPasswordMD5(password);
        String bcryptHash = bcryptPassword(password);

        System.out.println("MD5 Hash: " + md5Hash);
        System.out.println("BCrypt Hash: " + bcryptHash);
        
        System.out.println("\nChecking password match...'" + password + "'");
        boolean isMatch = checkPasswordMD5(password, md5Hash);
        System.out.println("MD5 Password match: " + isMatch);

        boolean isMatch2 = bcryptCheckPassword(password, bcryptHash);
        System.out.println("BCrypt Password match: " + isMatch2);

        String wrongPassword = "admin1234";

        System.out.println("\nChecking wrong password...'" + wrongPassword + "'");
        boolean isMatch3 = checkPasswordMD5(wrongPassword, md5Hash);
        System.out.println("MD5 Incorrect password is match: " + isMatch3);

        boolean isMatch4 = bcryptCheckPassword(wrongPassword, bcryptHash);
        System.out.println("BCrypt Incorrect password is match: " + isMatch4);
    }
} 