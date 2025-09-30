package ru.kofa.todolist.service.card;

import ru.kofa.todolist.dto.CardItemDto;
import ru.kofa.todolist.model.CardItem;
import ru.kofa.todolist.request.CardItemRequest;

import java.util.List;

public interface ICardItemService {
    void addCard(CardItemRequest request, Long cardId);
    void updateCard(CardItemRequest request, Long id);
    void deleteCard(Long cardId);

    List<CardItemDto> getCardItemsByCardId(Long cardId);

    void toggleCardStatus(Long id);
}
