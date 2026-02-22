package ru.kofa.todolist.service.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kofa.todolist.dto.CardItemDto;
import ru.kofa.todolist.exception.CardItemNotFoundException;
import ru.kofa.todolist.exception.CardNotFoundException;
import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.CardItem;
import ru.kofa.todolist.repository.CardItemRepository;
import ru.kofa.todolist.repository.CardRepository;
import ru.kofa.todolist.request.CardItemRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardItemTest {
    @Mock
    private CardItemRepository cardItemRepository;

    @Mock
    private ICardService cardService;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardItemService cardItemService;

    @Test
    void addCardItem_Success() {
        CardItem cardItem = createCardItem();
        Card card = createCard();
        CardItemRequest cardItemRequest = createCardItemRequest();

        when(cardItemRepository.save(any(CardItem.class)))
                .thenReturn(cardItem);
        when(cardService.getCard(card.getId()))
                .thenReturn(card);

        CardItem savedCardItem = cardItemService.addCardItem(cardItemRequest, card.getId());

        assertThat(savedCardItem).isNotNull();
        assertThat(savedCardItem.getId()).isEqualTo(1L);
        assertThat(savedCardItem.getName()).isEqualTo("Test");
        assertThat(savedCardItem.getComment()).isEqualTo("test");
        assertThat(savedCardItem.getPriority()).isEqualTo("Easy");

        verify(cardItemRepository).save(any(CardItem.class));
    }

    @Test
    void addCardItem_NotFoundCard_ThrowCardNotFoundExc() {
        CardItemRequest cardItemRequest = createCardItemRequest();

        when(cardService.getCard(99L)).thenThrow(new CardNotFoundException("Card not found"));

        assertThrows(CardNotFoundException.class,
                () -> cardItemService.addCardItem(cardItemRequest, 99L));

        verify(cardItemRepository, never()).save(any());
    }

    @Test
    void updateCardItem_Success() {
        CardItem cardItem = createCardItem();

        CardItemRequest cardItemRequest = createCardItemRequest();
        cardItemRequest.setName("Test2");
        cardItemRequest.setComment("test2");
        cardItemRequest.setPriority("Hard");

        when(cardItemRepository.findById(cardItem.getId()))
                .thenReturn(Optional.of(cardItem));
        when(cardItemRepository.save(any(CardItem.class)))
                .thenReturn(cardItem);

        CardItem updateCardItem = cardItemService
                .updateCardItem(cardItemRequest, cardItem.getId());

        assertThat(updateCardItem.getId())
                .isEqualTo(cardItem.getId());
        assertThat(updateCardItem.getName())
                .isEqualTo("Test2");
        assertThat(updateCardItem.getComment())
                .isEqualTo("test2");
        assertThat(updateCardItem.getPriority())
                .isEqualTo("Hard");

        verify(cardItemRepository).findById(cardItem.getId());
        verify(cardItemRepository).save(any(CardItem.class));
    }

    @Test
    void updateCardItem_CardNotFound_CardNotFoundExc() {
        CardItemRequest cardItemRequest = createCardItemRequest();

        when(cardItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(CardItemNotFoundException.class,
                () -> cardItemService.updateCardItem(cardItemRequest, 99L));
    }

    @Test
    void deleteCardItem_Success() {
        CardItem cardItem = createCardItem();

        when(cardItemRepository.findById(cardItem.getId()))
                .thenReturn(Optional.of(cardItem));
        doNothing().when(cardItemRepository).delete(cardItem);

        cardItemService.deleteCardItem(cardItem.getId());

        verify(cardItemRepository).findById(cardItem.getId());
        verify(cardItemRepository).delete(cardItem);
    }

    @Test
    void deleteCard_NotFoundCardItem_ThrowsCardNotFoundExc() {
        when(cardItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(CardItemNotFoundException.class,
                () -> cardItemService.deleteCardItem(99L));
        verify(cardItemRepository, never()).delete(any());
    }

    @Test
    void getCardItemByCardId_Success() {
        CardItem cardItem = createCardItem();

        when(cardItemRepository.findByCardIdOrderByCreatedAtDesc(cardItem.getCard().getId()))
                .thenReturn(List.of(cardItem));

        List<CardItemDto> listCardItems = cardItemService.getCardItemsByCardId(cardItem.getCard().getId());
        CardItemDto cardItemDto = listCardItems.getFirst();

        assertThat(cardItemDto.getId())
                .isEqualTo(cardItem.getId());
        assertThat(cardItemDto.getName())
                .isEqualTo(cardItem.getName());
        assertThat(cardItemDto.getComment())
                .isEqualTo(cardItem.getComment());
        assertThat(cardItemDto.getPriority())
                .isEqualTo(cardItem.getPriority());

        verify(cardItemRepository).findByCardIdOrderByCreatedAtDesc(cardItem.getCard().getId());
    }

    @Test
    void toggleCardItemStatus_Success() {
        CardItem cardItem = createCardItem();

        when(cardItemRepository.save(any(CardItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cardItemRepository.findById(cardItem.getId()))
                .thenReturn(Optional.of(cardItem));

        CardItem updateCardItem = cardItemService.toggleCardItemStatus(cardItem.getId());

        assertThat(updateCardItem.getId())
                .isEqualTo(cardItem.getId());
        assertThat(updateCardItem.getName())
                .isEqualTo(cardItem.getName());
        assertThat(updateCardItem.getComment())
                .isEqualTo(cardItem.getComment());
        assertThat(updateCardItem.getPriority())
                .isEqualTo(cardItem.getPriority());
        assertThat(updateCardItem.isCompleted()).isTrue();

        verify(cardItemRepository).findById(1L);
        verify(cardItemRepository).save(any(CardItem.class));
    }

    @Test
    void toggleCardItemStatus_NotFoundCard_ThrowCardItemNotFoundExc() {
        when(cardItemRepository.findById(99L))
                .thenThrow(new CardItemNotFoundException("Card item not found"));

        assertThrows(CardItemNotFoundException.class,
                () -> cardItemService.toggleCardItemStatus(99L));
    }

    private CardItemRequest createCardItemRequest() {
        CardItemRequest cardItemRequest = new CardItemRequest();
        cardItemRequest.setName("Test");
        cardItemRequest.setComment("test");
        cardItemRequest.setPriority("Easy");

        return cardItemRequest;
    }

    private CardItem createCardItem() {
        CardItem cardItem = new CardItem();
        Card card = createCard();

        cardItem.setCard(card);
        cardItem.setId(1L);
        cardItem.setName("Test");
        cardItem.setComment("test");
        cardItem.setPriority("Easy");
        cardItem.setCompleted(false);

        return cardItem;
    }

    private Card createCard() {
        Card card = new Card();
        card.setId(1L);
        card.setCardItem(new HashSet<>());

        return card;
    }
}
