package com.aston.userservice.dao;

// Импорт 
import com.aston.userservice.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    // CREATE 

    /**
     * Сохранить нового пользователя в БД.
     *
     * @param user — объект для сохранения (id будет null, его присвоит БД)
     * @return тот же объект, но уже с проставленным id и createdAt
     */
    User save(User user);

    // READ 

    /**
     * Найти пользователя по id.
     * @param id — идентификатор
     * @return Optional с пользователем или пустой Optional
     */
    Optional<User> findById(Long id);

    /**
     * Получить всех пользователей из БД.
     *
     * @return список (может быть пустым, но не null)
     */
    List<User> findAll();

    /**
     * Найти пользователя по email.
     * Email уникален, поэтому Optional.
     *
     * @param email — email для поиска
     * @return Optional с пользователем или пустой Optional
     */
    Optional<User> findByEmail(String email);

    //  UPDATE

    /**
     * Обновить существующего пользователя.
     *
     * @param user — объект с заполненным id и новыми значениями
     * @return обновлённый объект
     */
    User update(User user);

    // DELETE 

    /**
     * Удалить пользователя по id.
     *
     * @param id — идентификатор
     * @return true, если удалён; false, если не найден
     */
    boolean deleteById(Long id);
}
