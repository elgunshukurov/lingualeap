package ai.lingualeap.lingualeap.dao.entity;

import ai.lingualeap.lingualeap.model.enums.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private static final String USER_NAME = "testuser";
    private static final String EMAIL_ADDRESS = "test@example.com";
    private static final String USER_PASSWORD = "password123";
    private static final String USER_FIRST_NAME = "Test";
    private static final String USER_LAST_NAME = "User";

    @Test
    void testUserCreation() {
        User user = new User();
        user.setUsername(USER_NAME);
        user.setEmail(EMAIL_ADDRESS);
        user.setPassword(USER_PASSWORD);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);
        user.setStatus(UserStatus.ACTIVE);

        assertEquals(USER_NAME, user.getUsername());
        assertEquals(EMAIL_ADDRESS, user.getEmail());
        assertEquals(USER_PASSWORD, user.getPassword());
        assertEquals(USER_FIRST_NAME, user.getFirstName());
        assertEquals(USER_LAST_NAME, user.getLastName());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertTrue(user.isActive());
        assertFalse(user.isBanned());
    }

    @Test
    void testUserStatus() {
        User user = new User();

        user.setStatus(UserStatus.ACTIVE);
        assertTrue(user.isActive());
        assertFalse(user.isBanned());

        user.setStatus(UserStatus.INACTIVE);
        assertFalse(user.isActive());
        assertFalse(user.isBanned());

        user.setStatus(UserStatus.BANNED);
        assertFalse(user.isActive());
        assertTrue(user.isBanned());
    }
}
