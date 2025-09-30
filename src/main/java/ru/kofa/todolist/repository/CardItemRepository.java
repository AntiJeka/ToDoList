package ru.kofa.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kofa.todolist.model.CardItem;

import java.util.List;

@Repository
public interface CardItemRepository extends JpaRepository<CardItem, Long> {
    List<CardItem> findByCardIdOrderByCreatedAtDesc(Long cardId);
    List<CardItem> findByCardIdAndCompletedOrderByCreatedAtDesc(Long cardId, boolean completed);
}
