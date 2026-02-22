package ru.kofa.todolist.service.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kofa.todolist.exception.CardNotFoundException;
import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.User;
import ru.kofa.todolist.repository.CardRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_Success() {
        Card card = createNewCardObject();

        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card newCard = cardService.createCard(card);

        assertThat(newCard).isNotNull();
        assertThat(newCard.getId()).isEqualTo(1L);
        assertThat(newCard.getUser().getId()).isEqualTo(1L);

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void initializeNewCart_Success() {
        User user = createUserObject();
        Card card = createNewCardObject();
        card.setUser(user);

        when(cardRepository.findByUserId(user.getId())).thenReturn(null);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card initializeCard = cardService.initializeNewCart(user);

        assertThat(initializeCard.getUser()).isEqualTo(card.getUser());

        verify(cardRepository).findByUserId(user.getId());
        verify(cardRepository).save(any());
    }

    @Test
    void initializeNewCart_UserHasCard_ReturnsExisting() {
        User user = createUserObject();
        Card card = createNewCardObject();
        card.setUser(user);

        when(cardRepository.findByUserId(user.getId())).thenReturn(card);

        Card initializeCard = cardService.initializeNewCart(user);

        assertThat(initializeCard).isEqualTo(card);

        verify(cardRepository).findByUserId(user.getId());
    }

    @Test
    void getCard_Success() {
        Card card = createNewCardObject();

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        Card getCard = cardService.getCard(card.getId());

        assertThat(getCard.getId()).isEqualTo(card.getId());

        verify(cardRepository).findById(card.getId());
    }

    @Test
    void getCard_NotFound_CardNotFoundExc() {
        Card card = createNewCardObject();

        when(cardRepository.findById(card.getId())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> cardService.getCard(card.getId()));
    }

    private User createUserObject() {
        User user = new User();

        user.setId(1L);
        user.setUsername("username");
        user.setPassword("1234");

        return user;
    }

    private Card createNewCardObject() {
        Card card = new Card();
        card.setId(1L);

        User user = createUserObject();
        user.setCard(card);

        card.setUser(user);
        return card;
    }
}
