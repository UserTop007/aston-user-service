// Объявление пакета 
package com.aston.userservice.entity;

// Импорты классов 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

// Класс для даты+времени
import java.time.LocalDateTime;
import java.util.Objects;

// @Entity — аннотация для Hibernate: "этот класс соответствует таблице в БД".
@Entity
// @Table(name = "users") — указываем имя таблицы.
@Table(name = "users")
public class User {
    // Поля 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Пустой конструктор 
    public User() {
    }

    // Удобный конструктор для создания нового пользователя.
    // createdAt не передаём — оно проставится автоматически (см. @PrePersist ниже).
    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

        // @PrePersist — Hibernate вызывает этот метод ПЕРЕД сохранением
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // ГЕТТЕРЫ И СЕТТЕРЫ 
    // Hibernate обращается к полям через них (по умолчанию — property access).
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // EQUALS / HASHCODE
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;               // тот же самый объект
        if (!(o instanceof User user)) return false; // не User — не равны
        return id != null && id.equals(user.id);   // сравниваем по id
    }

    // hashCode — для коллекций (HashMap, HashSet).
    // Должен быть согласован с equals: если equals по id, то и hashCode по id.
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString 
    @Override
    public String toString() {
        return "User{id=%d, name='%s', email='%s', age=%s, createdAt=%s}"
                .formatted(id, name, email, age, createdAt);
    }
}