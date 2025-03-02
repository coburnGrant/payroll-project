package grant.coburn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PasswordUtilTest {

    @Test
    public void testValidPassword() throws PasswordValidationException {
        assertTrue(PasswordUtil.isValidPassword("Password123!"));
        assertTrue(PasswordUtil.isValidPassword("Secure@Pass789"));
        assertTrue(PasswordUtil.isValidPassword("Complex1Password!"));
    }

    @Test
    public void testPasswordTooShort() {
        PasswordValidationException exception = assertThrows(
            PasswordValidationException.class,
            () -> PasswordUtil.isValidPassword("Pass1!")
        );
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    public void testPasswordWithoutUpperCase() {
        PasswordValidationException exception = assertThrows(
            PasswordValidationException.class,
            () -> PasswordUtil.isValidPassword("password123!")
        );
        assertEquals("Password must contain at least one uppercase letter", exception.getMessage());
    }

    @Test
    public void testPasswordWithoutLowerCase() {
        PasswordValidationException exception = assertThrows(
            PasswordValidationException.class,
            () -> PasswordUtil.isValidPassword("PASSWORD123!")
        );
        assertEquals("Password must contain at least one lowercase letter", exception.getMessage());
    }

    @Test
    public void testPasswordWithoutNumber() {
        PasswordValidationException exception = assertThrows(
            PasswordValidationException.class,
            () -> PasswordUtil.isValidPassword("Password!")
        );
        assertEquals("Password must contain at least one number", exception.getMessage());
    }

    @Test
    public void testPasswordWithoutSpecialChar() {
        PasswordValidationException exception = assertThrows(
            PasswordValidationException.class,
            () -> PasswordUtil.isValidPassword("Password123")
        );
        assertEquals("Password must contain at least one special character", exception.getMessage());
    }

    @Test
    public void testNullPassword() {
        PasswordValidationException exception = assertThrows(
            PasswordValidationException.class,
            () -> PasswordUtil.isValidPassword(null)
        );
        assertEquals("Password cannot be null", exception.getMessage());
    }

    @Test
    public void testEmptyPassword() {
        PasswordValidationException exception = assertThrows(
            PasswordValidationException.class,
            () -> PasswordUtil.isValidPassword("")
        );
        assertEquals("Password cannot be empty", exception.getMessage());
    }

    @Test
    public void testPasswordWithSpaces() {
        PasswordValidationException exception = assertThrows(
            PasswordValidationException.class,
            () -> PasswordUtil.isValidPassword("Password 123!")
        );
        assertEquals("Password cannot contain spaces", exception.getMessage());
    }

    @Test
    public void testPasswordHashing() {
        String password = "admin123";
        
        // Test MD5 hashing
        String md5Hash = PasswordUtil.encryptPasswordMD5(password);
        assertNotNull(md5Hash);
        assertFalse(md5Hash.isEmpty());
        assertTrue(PasswordUtil.checkPasswordMD5(password, md5Hash));
        assertFalse(PasswordUtil.checkPasswordMD5("wrongpassword", md5Hash));
        
        // Test BCrypt hashing
        String bcryptHash = PasswordUtil.bcryptPassword(password);
        assertNotNull(bcryptHash);
        assertFalse(bcryptHash.isEmpty());
        assertTrue(PasswordUtil.bcryptCheckPassword(password, bcryptHash));
        assertFalse(PasswordUtil.bcryptCheckPassword("wrongpassword", bcryptHash));
        
        // Verify different hashes for same password
        String anotherBcryptHash = PasswordUtil.bcryptPassword(password);
        assertNotEquals(bcryptHash, anotherBcryptHash, "BCrypt hashes should be different for same password");
        assertTrue(PasswordUtil.bcryptCheckPassword(password, anotherBcryptHash));
    }
} 