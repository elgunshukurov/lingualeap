package ai.lingualeap.lingualeap.dao.entity;

import ai.lingualeap.lingualeap.model.enums.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(UserStatus.ACTIVE);

        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
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
