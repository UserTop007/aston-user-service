package com.aston.userservice.dao;

import com.aston.userservice.entity.User;
import com.aston.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User save(User user) {
        try (Session session = sessionFactory.openSession()) {
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

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User ORDER BY id", User.class).list();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            User user = session
                    .createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Нельзя обновить пользователя без id");
        }
        try (Session session = sessionFactory.openSession()) {
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

    @Override
    public boolean deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
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