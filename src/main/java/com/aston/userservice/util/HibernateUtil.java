package com.aston.userservice.util;

// Импорты классов
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
// Логгер SLF4J — чтобы писать сообщения о запуске/ошибках.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Утилитный класс для получения SessionFactory.
public final class HibernateUtil {

    // ЛОГГЕР 
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    // SESSION FACTORY 
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    // КОНСТРУКТОР 
    private HibernateUtil() {
    }

    // СОЗДАНИЕ SESSION FACTORY 
    private static SessionFactory buildSessionFactory() {
        try {
            SessionFactory factory = new Configuration()
                    .configure()
                    .buildSessionFactory();

            log.info("SessionFactory успешно создан");
            return factory;

        } catch (Throwable ex) {
            log.error("Ошибка инициализации SessionFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // ПОЛУЧЕНИЕ SESSION FACTORY 
    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    // ЗАКРЫТИЕ 
    public static void shutdown() {
        // Проверяем: не null ли фабрика (вдруг она не создалась),
        // и не закрыта ли уже (двойное закрытие — ошибка).
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
            log.info("SessionFactory закрыт");
        }
    }
}