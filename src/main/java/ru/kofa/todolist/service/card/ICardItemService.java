package ru.kofa.todolist.service.card;

import ru.kofa.todolist.dto.CardItemDto;
import ru.kofa.todolist.model.CardItem;
import ru.kofa.todolist.request.CardItemRequest;

import java.util.List;

public interface ICardItemService {
    CardItem addCardItem(CardItemRequest request, Long cardId);
    CardItem updateCardItem(CardItemRequest request, Long id);
    void deleteCardItem(Long cardId);

    List<CardItemDto> getCardItemsByCardId(Long cardId);

    CardItem toggleCardItemStatus(Long id);
}
