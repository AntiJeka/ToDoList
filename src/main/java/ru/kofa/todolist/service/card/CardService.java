package ru.kofa.todolist.service.card;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kofa.todolist.exception.CardNotFoundException;
import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.User;
import ru.kofa.todolist.repository.CardRepository;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CardService implements ICardService{
    private final CardRepository cardRepository;

    @Override
    public Card createCard(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public Card initializeNewCart(User user) {
        Card existingCard = cardRepository.findByUserId(user.getId());

        if (existingCard != null) {
            return existingCard;
        }

        Card card = new Card();
        card.setUser(user);
        card.setCardItem(new HashSet<>());

        return cardRepository.save(card);
    }

    @Override
    public Card getCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
    }

    @Override
    public Card getCardByUser(User user) {
        return cardRepository.findByUserId(user.getId());
    }
}
