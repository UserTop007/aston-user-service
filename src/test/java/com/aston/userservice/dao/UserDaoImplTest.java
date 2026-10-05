package com.aston.userservice.dao;

import com.aston.userservice.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoImplTest {

    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    SessionFactory sessionFactory;
    UserDaoImpl userDao;

    @BeforeAll
    static void startContainer() {
        postgres.start();
    }

    @AfterAll
    static void stopContainer() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        Configuration config = new Configuration();
        config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        config.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        config.setProperty("hibernate.connection.username", postgres.getUsername());
        config.setProperty("hibernate.connection.password", postgres.getPassword());
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        config.setProperty("hibernate.show_sql", "false");
        config.addAnnotatedClass(User.class);

        sessionFactory = config.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);

        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    private void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @DisplayName("save: сохраняет пользователя и присваивает id")
    void save_shouldPersistUser_andAssignId() {
        User user = new User("Ivan", "ivan@mail.ru", 25);

        User saved = userDao.save(user);

        assertNotNull(saved.getId());
        assertEquals("Ivan", saved.getName());
        assertEquals("ivan@mail.ru", saved.getEmail());
        assertEquals(25, saved.getAge());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("findById: возвращает пользователя, если найден")
    void findById_shouldReturnUser_whenExists() {
        User saved = userDao.save(new User("Ivan", "ivan@mail.ru", 25));

        Optional<User> found = userDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Ivan", found.get().getName());
    }

    @Test
    @DisplayName("findById: пустой Optional, если не найден")
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<User> found = userDao.findById(99999L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findAll: возвращает всех пользователей")
    void findAll_shouldReturnAllUsers() {
        userDao.save(new User("Ivan", "ivan@mail.ru", 25));
        userDao.save(new User("Petr", "petr@mail.ru", 30));

        List<User> all = userDao.findAll();

        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("findAll: пустой список, если БД пустая")
    void findAll_shouldReturnEmpty_whenNoUsers() {
        List<User> all = userDao.findAll();

        assertTrue(all.isEmpty());
    }

    @Test
    @DisplayName("findByEmail: возвращает пользователя по email")
    void findByEmail_shouldReturnUser_whenExists() {
        userDao.save(new User("Ivan", "ivan@mail.ru", 25));

        Optional<User> found = userDao.findByEmail("ivan@mail.ru");

        assertTrue(found.isPresent());
        assertEquals("Ivan", found.get().getName());
    }

    @Test
    @DisplayName("findByEmail: пустой Optional, если не найден")
    void findByEmail_shouldReturnEmpty_whenNotExists() {
        Optional<User> found = userDao.findByEmail("nobody@mail.ru");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("update: обновляет данные пользователя")
    void update_shouldModifyUser() {
        User saved = userDao.save(new User("Ivan", "ivan@mail.ru", 25));

        saved.setName("Petr");
        saved.setEmail("petr@mail.ru");
        saved.setAge(30);

        User updated = userDao.update(saved);

        assertEquals("Petr", updated.getName());
        assertEquals("petr@mail.ru", updated.getEmail());
        assertEquals(30, updated.getAge());

        Optional<User> fromDb = userDao.findById(saved.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Petr", fromDb.get().getName());
    }

    @Test
    @DisplayName("deleteById: удаляет пользователя и возвращает true")
    void deleteById_shouldRemoveUser_andReturnTrue() {
        User saved = userDao.save(new User("Ivan", "ivan@mail.ru", 25));

        boolean deleted = userDao.deleteById(saved.getId());

        assertTrue(deleted);
        assertTrue(userDao.findById(saved.getId()).isEmpty());
    }

    @Test
    @DisplayName("deleteById: возвращает false, если пользователя нет")
    void deleteById_shouldReturnFalse_whenNotExists() {
        boolean deleted = userDao.deleteById(99999L);

        assertFalse(deleted);
    }
}