package com.aston.userservice.service;

import com.aston.userservice.dao.UserDao;
import com.aston.userservice.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userDao);
    }

    @Test
    @DisplayName("createUser: valid data")
    void createUser_shouldSaveUser_whenDataIsValid() {
        String name = "Ivan";
        String email = "ivan@mail.ru";
        Integer age = 25;

        User userToSave = new User(name, email, age);
        userToSave.setId(1L);

        when(userDao.save(any(User.class))).thenReturn(userToSave);

        User result = userService.createUser(name, email, age);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: blank name")
    void createUser_shouldThrowException_whenNameIsBlank() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("", "ivan@mail.ru", 25)
        );
        assertNotNull(ex.getMessage());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: invalid email")
    void createUser_shouldThrowException_whenEmailInvalid() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("Ivan", "invalid-email", 25)
        );
        assertNotNull(ex.getMessage());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: age out of range")
    void createUser_shouldThrowException_whenAgeOutOfRange() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("Ivan", "ivan@mail.ru", -5)
        );
        assertNotNull(ex.getMessage());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getUserById: found")
    void getUserById_shouldReturnUser_whenFound() {
        Long id = 1L;
        User expected = new User("Ivan", "ivan@mail.ru", 25);
        expected.setId(id);

        when(userDao.findById(id)).thenReturn(Optional.of(expected));

        Optional<User> result = userService.getUserById(id);

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verify(userDao, times(1)).findById(id);
    }

    @Test
    @DisplayName("getUserById: not found")
    void getUserById_shouldReturnEmpty_whenNotFound() {
        Long id = 999L;
        when(userDao.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(id);

        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findById(id);
    }

    @Test
    @DisplayName("updateUser: success")
    void updateUser_shouldUpdateUser_whenFound() {
        Long id = 1L;
        User existing = new User("Ivan", "ivan@mail.ru", 25);
        existing.setId(id);

        when(userDao.findById(id)).thenReturn(Optional.of(existing));
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(id, "Petr", "petr@mail.ru", 30);

        assertEquals("Petr", result.getName());
        assertEquals("petr@mail.ru", result.getEmail());
        assertEquals(30, result.getAge());

        verify(userDao, times(1)).findById(id);
        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("updateUser: not found")
    void updateUser_shouldThrowException_whenNotFound() {
        Long id = 999L;
        when(userDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(id, "Petr", "petr@mail.ru", 30)
        );
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    @DisplayName("deleteUser: deleted")
    void deleteUser_shouldReturnTrue_whenDeleted() {
        Long id = 1L;
        when(userDao.deleteById(id)).thenReturn(true);

        boolean result = userService.deleteUser(id);

        assertTrue(result);
        verify(userDao, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deleteUser: not found")
    void deleteUser_shouldReturnFalse_whenNotFound() {
        Long id = 999L;
        when(userDao.deleteById(id)).thenReturn(false);

        boolean result = userService.deleteUser(id);

        assertFalse(result);
        verify(userDao, times(1)).deleteById(id);
    }
}