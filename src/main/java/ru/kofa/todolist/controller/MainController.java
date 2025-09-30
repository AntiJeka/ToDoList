package ru.kofa.todolist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kofa.todolist.dto.CardItemDto;
import ru.kofa.todolist.model.Card;
import ru.kofa.todolist.model.User;
import ru.kofa.todolist.request.CardItemRequest;
import ru.kofa.todolist.service.card.ICardItemService;
import ru.kofa.todolist.service.card.ICardService;
import ru.kofa.todolist.service.user.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    private final ICardItemService cardItemService;
    private final ICardService cardService;
    private final UserService userService;

    @GetMapping
    public String userAccess(Model model, Principal principal) {
        try {
            User user = userService.getAuthenticatedUser();
            Card card = cardService.getCardByUser(user);

            if (card == null) {
                card = cardService.initializeNewCart(user);
            }

            List<CardItemDto> cardItems = cardItemService.getCardItemsByCardId(card.getId());
            model.addAttribute("tasks", cardItems);
            model.addAttribute("username", user.getUsername());

        } catch (Exception e) {
            model.addAttribute("tasks", new ArrayList<CardItemDto>());
        }
        return "main";
    }

    @PostMapping("/add")
    public String addCardItem(@ModelAttribute CardItemRequest request, Principal principal) {
        try {
            User user = userService.getAuthenticatedUser();
            Card card = cardService.getCardByUser(user);
            cardItemService.addCard(request, card.getId());
            return "redirect:/main?success=added";
        } catch (Exception e) {
            return "redirect:/main?error=add_failed";
        }
    }

    @PostMapping("/{id}/update")
    public String updateCard(@ModelAttribute CardItemRequest request, @PathVariable Long id) {
        try {
            cardItemService.updateCard(request, id);
            return "redirect:/main?success=updated";
        } catch (Exception e) {
            return "redirect:/main?error=update_failed";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCard(@PathVariable Long id) {
        try {
            cardItemService.deleteCard(id);
            return "redirect:/main?success=deleted";
        } catch (Exception e) {
            return "redirect:/main?error=delete_failed";
        }
    }

    @PostMapping("/{id}/toggle")
    @ResponseBody
    public ResponseEntity<String> toggleTaskStatus(@PathVariable Long id) {
        try {
            cardItemService.toggleCardStatus(id);
            return ResponseEntity.ok("Status updated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating status");
        }
    }
}