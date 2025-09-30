package ru.kofa.todolist.service.card;

import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.User;

public interface ICardService {
    Card createCard(Card card);
    Card initializeNewCart(User user);

    Card getCard(Long cardId);

    Card getCardByUser(User user);
}
