package ru.kofa.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.User;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Card findByUserId(Long userId);
    Card findByUser_Id(Long userId); // Обращение к id через свойство user
    Card findByUser(User user);
}
