package com.aston.userservice;

import com.aston.userservice.entity.User;
import com.aston.userservice.service.UserService;
import com.aston.userservice.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final UserService userService = new UserService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        log.info("Приложение user-service запущено");

        try {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> createUser();
                        case "2" -> findUserById();
                        case "3" -> findAllUsers();
                        case "4" -> updateUser();
                        case "5" -> deleteUser();
                        case "6" -> findByEmail();
                        case "0" -> {
                            running = false;
                            log.info("Выход из приложения");
                        }
                        default -> System.out.println("Неизвестная команда: " + choice);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка ввода: " + e.getMessage());
                } catch (Exception e) {
                    log.error("Ошибка выполнения операции", e);
                    System.out.println("Произошла ошибка. См. логи.");
                }
            }
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
            log.info("Приложение остановлено");
        }
    }

    private static void printMenu() {
        System.out.println("""
                
                ===== user-service =====
                1. Создать пользователя
                2. Найти по id
                3. Показать всех
                4. Обновить пользователя
                5. Удалить пользователя
                6. Найти по email
                0. Выход
                Выберите пункт:""");
    }

    private static void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Возраст: ");
        Integer age = readInt();

        User user = userService.createUser(name, email, age);
        System.out.println("Создан: " + user);
    }

    private static void findUserById() {
        System.out.print("id: ");
        Long id = readLong();
        userService.getUserById(id)
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Пользователь не найден")
                );
    }

    private static void findAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список пуст");
        } else {
            System.out.println("Всего: " + users.size());
            users.forEach(System.out::println);
        }
    }

    private static void updateUser() {
        System.out.print("id: ");
        Long id = readLong();
        System.out.print("Новое имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Новый email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Новый возраст: ");
        Integer age = readInt();

        User updated = userService.updateUser(id, name, email, age);
        System.out.println("Обновлён: " + updated);
    }

    private static void deleteUser() {
        System.out.print("id: ");
        Long id = readLong();
        boolean deleted = userService.deleteUser(id);
        System.out.println(deleted ? "Удалён" : "Не найден");
    }

    private static void findByEmail() {
        System.out.print("email: ");
        String email = scanner.nextLine().trim();
        userService.findByEmail(email)
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Пользователь не найден")
                );
    }

    private static Long readLong() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Введите целое число: ");
            }
        }
    }

    private static Integer readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Введите целое число: ");
            }
        }
    }
}