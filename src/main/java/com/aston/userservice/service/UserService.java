package com.aston.userservice.service;

// Импорты: наш DAO, сущность, логгер и стандартные классы.
import com.aston.userservice.dao.UserDao;
import com.aston.userservice.dao.UserDaoImpl;
import com.aston.userservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class UserService {

    // Логгер для этого класса.
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao;

    // Конструктор: создаём стандартный DAO.
    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    // ===== CREATE =====

    /**
     * @param name  имя (не пустое)
     * @param email email (с @)
     * @param age   возраст (0..150, может быть null)
     * @return сохранённый User с присвоенным id
     */
    public User createUser(String name, String email, Integer age) {
        // Сначала проверяем данные — до того, как лезть в БД.
        validate(name, email, age);

        // Создаём объект. createdAt проставит @PrePersist,
        // id — присвоит БД после INSERT.
        User user = new User(name, email, age);

        // DAO.save сделает всю работу с БД: транзакция, persist, commit.
        User saved = userDao.save(user);

        // Логируем успех. Бизнес-событие "создан пользователь".
        log.info("Создан пользователь: {}", saved);

        return saved;
    }

    // READ 

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    /**
     * Получить всех пользователей.
     * Всегда возвращает список (возможно пустой), но не null.
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        return userDao.findByEmail(email);
    }

    // ===== UPDATE =====

    /**
     * Обновить данные пользователя по id.
     * @param id    id пользователя (обязательно)
     * @param name  новое имя
     * @param email новый email
     * @param age   новый возраст
     * @return обновлённый User
     */
    public User updateUser(Long id, String name, String email, Integer age) {
        if (id == null) {
            throw new IllegalArgumentException("id не может быть null");
        }

        // Валидируем новые данные.
        validate(name, email, age);

        User existing = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с id=" + id + " не найден"));

        existing.setName(name);
        existing.setEmail(email);
        existing.setAge(age);

        // Передаём в DAO. Там merge выполнит UPDATE.
        User updated = userDao.update(existing);

        log.info("Обновлён пользователь: {}", updated);

        return updated;
    }

    //  DELETE 

    /**
     * Удалить пользователя по id.
     *
     * @return true если был удалён, false если не найден.
     */
    public boolean deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id не может быть null");
        }

        boolean deleted = userDao.deleteById(id);

        if (deleted) {
            log.info("Удалён пользователь id={}", id);
        } else {
            log.warn("Пользователь id={} не найден при удалении", id);
        }

        return deleted;
    }

    // ВАЛИДАЦИЯ

    private void validate(String name, String email, Integer age) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }

        // Разумная длина. VARCHAR(100) в БД — значит до 100 символов.
        if (name.length() > 100) {
            throw new IllegalArgumentException("Имя слишком длинное (макс. 100 символов)");
        }

        // email
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Некорректный email: должен содержать @");
        }

        if (email.length() > 150) {
            throw new IllegalArgumentException("Email слишком длинный (макс. 150 символов)");
        }

        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("Возраст должен быть в диапазоне 0..150");
        }
    }
}