package ru.kofa.todolist.service.card;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kofa.todolist.dto.CardItemDto;
import ru.kofa.todolist.exception.CardItemNotFoundException;
import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.CardItem;
import ru.kofa.todolist.repository.CardItemRepository;
import ru.kofa.todolist.repository.CardRepository;
import ru.kofa.todolist.request.CardItemRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardItemService implements ICardItemService {
    private final CardItemRepository cardItemRepository;
    private final CardRepository cardRepository;
    private final ICardService cardService;

    @Override
    public CardItem addCardItem(CardItemRequest request, Long cardId) {
        Card card = cardService.getCard(cardId);

        CardItem cardItem = new CardItem();
        cardItem.setCard(card);
        cardItem.setName(request.getName());
        cardItem.setComment(request.getComment());
        cardItem.setPriority(request.getPriority());
        cardItem.setCompleted(false);
        cardItem.setCreatedAt(LocalDateTime.now());

        card.getCardItem().add(cardItem);
        cardRepository.save(card);

        return cardItemRepository.save(cardItem);
    }

    @Override
    public CardItem updateCardItem(CardItemRequest request, Long id) {
        CardItem cardItem = cardItemRepository.findById(id)
                .orElseThrow(() -> new CardItemNotFoundException("Card item not found"));

        cardItem.setName(request.getName());
        cardItem.setComment(request.getComment());
        cardItem.setPriority(request.getPriority());

        return cardItemRepository.save(cardItem);
    }

    @Override
    public void deleteCardItem(Long cardItemId) {
        CardItem cardItem = cardItemRepository.findById(cardItemId)
                .orElseThrow(() -> new CardItemNotFoundException("Card item not found"));

        Card card = cardItem.getCard();
        card.getCardItem().remove(cardItem);
        cardRepository.save(card);

        cardItemRepository.delete(cardItem);
    }

    @Override
    public List<CardItemDto> getCardItemsByCardId(Long cardId) {
        return cardItemRepository.findByCardIdOrderByCreatedAtDesc(cardId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CardItem toggleCardItemStatus(Long id) {
        CardItem cardItem = cardItemRepository.findById(id)
                .orElseThrow(() -> new CardItemNotFoundException("Card item not found"));

        cardItem.setCompleted(!cardItem.isCompleted());
        return cardItemRepository.save(cardItem);
    }

    // ================================
    // Второстепенные методы для работы
    // ================================

    private CardItemDto convertToDto(CardItem cardItem) {
        return CardItemDto.builder()
                .id(cardItem.getId())
                .name(cardItem.getName())
                .comment(cardItem.getComment())
                .priority(cardItem.getPriority())
                .completed(cardItem.isCompleted())
                .createdAt(formatDate(cardItem.getCreatedAt()))
                .build();
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDateTime.now().format(formatter);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTime.format(formatter);
    }
}