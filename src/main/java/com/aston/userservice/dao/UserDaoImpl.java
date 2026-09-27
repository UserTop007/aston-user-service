package com.aston.userservice.dao;

import com.aston.userservice.entity.User;
import com.aston.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class UserDaoImpl implements UserDao {

    // Логгер для этого класса 
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    //  CREATE 
        // ===== CREATE =====
    @Override
    public User save(User user) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                session.persist(user);
                tx.commit();
                log.info("Сохранён пользователь: {}", user);
                return user;

            } catch (Exception e) {
                tx.rollback();
                log.error("Ошибка сохранения пользователя", e);
                throw e;
            }
        }
    }

    //  READ 
    @Override
    public Optional<User> findById(Long id) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);            
            return Optional.ofNullable(user);
        }
    }

        @Override
    public List<User> findAll() {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<User> users = session
                    .createQuery("FROM User ORDER BY id", User.class)
                    .list();   // ← .list() выполняет запрос и возвращает List

            // Логируем, сколько нашли.
            log.info("Найдено пользователей: {}", users.size());

            return users;
        }
    }

        @Override
    public Optional<User> findByEmail(String email) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            User user = session
                    .createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();

            return Optional.ofNullable(user);
        }
    }

    //  UPDATE 
        // ===== UPDATE =====
    @Override
    public User update(User user) {

        if (user.getId() == null) {
            throw new IllegalArgumentException("Нельзя обновить пользователя без id");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Транзакция ОБЯЗАТЕЛЬНА — мы меняем данные.
            Transaction tx = session.beginTransaction();

            try {
                User merged = session.merge(user);
                tx.commit();
                log.info("Обновлён пользователь: {}", merged);
                return merged;

            } catch (Exception e) {
                tx.rollback();
                log.error("Ошибка обновления пользователя id={}", user.getId(), e);
                throw e;
            }
        }
    }

    //  DELETE 
    @Override
    public boolean deleteById(Long id) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                User user = session.get(User.class, id);

                if (user == null) {
                    tx.rollback();
                    log.warn("Пользователь id={} не найден, удаление отменено", id);
                    return false;
                }

                session.remove(user);

                tx.commit();

                log.info("Удалён пользователь id={}", id);
                return true;

            } catch (Exception e) {
                tx.rollback();
                log.error("Ошибка удаления пользователя id={}", id, e);
                throw e;
            }
        }
    }
}