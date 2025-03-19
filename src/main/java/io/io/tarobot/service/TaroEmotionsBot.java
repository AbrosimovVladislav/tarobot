package io.io.tarobot.service;

import io.io.tarobot.domain.UserState;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Slf4j
public class TaroEmotionsBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final TaroService taroService;

    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, String> userSelectedCategory = new HashMap<>();
    private final Map<Long, String> userLastQuery = new HashMap<>();
    private final Map<Long, Integer> lastMessageId = new HashMap<>();

    public TaroEmotionsBot(String botUsername, String botToken, TaroService taroService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.taroService = taroService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived[TaroEmotionsBot] initiated");
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleText(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
        log.info("onUpdateReceived[TaroEmotionsBot] finished");
    }

    private void handleText(Message message) {
        long chatId = message.getChatId();
        String userInput = message.getText();
        log.info("handleText[TaroEmotionsBot] initiated with text: " + userInput);
        UserState state = userStates.getOrDefault(chatId, UserState.IDLE);

        if (userInput.equals("/start")) {
            sendWelcome(chatId);
            return;
        }

        switch (state) {
            case ENTERING_QUESTION -> {
                userLastQuery.put(chatId, userInput);
                userStates.put(chatId, UserState.WAITING_FOR_CONFIRMATION);
                sendMessageWithButtons(chatId, "🔮 Всё готово! Нажми «Сделать расклад», когда будешь готов.",
                        List.of(
                                List.of(button("🃏 Сделать расклад", "confirm_tarot")),
                                List.of(button("❌ Отмена", "cancel_tarot"))
                        ));
            }
            case AWAITING_CLARIFICATION -> {
                userLastQuery.put(chatId, userInput);
                userStates.put(chatId, UserState.WAITING_FOR_CLARIFICATION_CONFIRMATION);
                sendMessageWithButtons(chatId, "📝 Всё готово! Нажми «Сделать уточнение», когда будешь готов.",
                        List.of(
                                List.of(button("🔍 Сделать уточнение", "confirm_clarifying")),
                                List.of(button("❌ Отмена", "cancel_clarifying"))
                        ));
            }
            default -> sendMessage(chatId, "😕 Я пока не понимаю твой запрос. Используй кнопки.");
        }
        log.info("handleText[TaroEmotionsBot] finished");
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        log.info("handleCallback[TaroEmotionsBot] initiated with data: " + data);

        switch (data) {
            case "start_tarot", "again" -> {
                userStates.put(chatId, UserState.CHOOSING_CATEGORY);
                sendCategories(chatId);
            }
            case "how_it_works" -> sendHowItWorks(chatId);
            case "category_love", "category_work", "category_energy",
                    "category_growth", "category_life" -> {
                userSelectedCategory.put(chatId, data);
                userStates.put(chatId, UserState.ENTERING_QUESTION);
                sendMessage(chatId, "📝 Опиши свой вопрос для расклада.");
            }
            case "confirm_tarot" -> {
                if (userLastQuery.containsKey(chatId)) {
                    userStates.put(chatId, UserState.IDLE);

                    String category = switch (userSelectedCategory.getOrDefault(chatId, "category_general")) {
                        case "category_love" -> "Любовь и отношения";
                        case "category_work" -> "Работа и деньги";
                        case "category_energy" -> "Энергии дня";
                        case "category_growth" -> "Личностный рост";
                        case "category_life" -> "Жизненный путь";
                        default -> "Общий расклад";
                    };

                    String userQuery = category + ": " + userLastQuery.get(chatId);
                    String tarotResult = taroService.makeTarotPrediction(userQuery);
                    sendTarotResult(chatId, tarotResult);
                } else {
                    sendMessage(chatId, "❗ Пожалуйста, сначала введите свой вопрос.");
                }
            }
            case "clarifying_question" -> {
                if (userLastQuery.containsKey(chatId)) {
                    userStates.put(chatId, UserState.AWAITING_CLARIFICATION);
                    sendMessage(chatId, "📝 Какой момент в раскладе тебе хочется уточнить?");
                } else {
                    sendMessage(chatId, "❗ Сначала сделай расклад.");
                }
            }
            case "confirm_clarifying" -> {
                if (userLastQuery.containsKey(chatId)) {
                    userStates.put(chatId, UserState.IDLE);
                    String clarifyingQuery = userLastQuery.get(chatId);
                    String tarotResult = taroService.makeClarifyingQuestion(clarifyingQuery);
                    sendTarotResult(chatId, tarotResult);
                } else {
                    sendMessage(chatId, "❗ Пожалуйста, сначала введите уточняющий вопрос.");
                }
            }
            case "cancel_clarifying" -> {
                userStates.put(chatId, UserState.IDLE);
                sendTarotOptions(chatId);
            }
            case "cancel_tarot" -> {
                userStates.put(chatId, UserState.IDLE);
                sendWelcome(chatId);
            }
            case "end" -> sendWelcome(chatId);
        }
        log.info("handleCallback[TaroEmotionsBot] finished");
    }


    private void sendCategories(long chatId) {
        sendMessageWithButtons(chatId, "Какой вопрос тебя волнует?",
                List.of(
                        List.of(button("💖 Любовь и отношения", "category_love")),
                        List.of(button("💼 Работа и деньги", "category_work")),
                        List.of(button("🌀 Энергии дня", "category_energy")),
                        List.of(button("🌟 Личностный рост", "category_growth")),
                        List.of(button("🔮 Жизненный путь", "category_life"))
                ));
    }

    private void sendWelcome(long chatId) {
        sendMessageWithButtons(chatId, "✨ Привет! Я твой личный таро-проводник.\nДавай посмотрим, что говорят карты?",
                List.of(
                        List.of(button("🃏 Сделать расклад", "start_tarot")),
                        List.of(button("❓ Как это работает?", "how_it_works"))
                ));
    }

    private void sendHowItWorks(long chatId) {
        sendMessageWithButtons(chatId, "Ты выбираешь тему гадания, я вытягиваю карты и даю тебе их интерпретацию.\nГотов попробовать?",
                List.of(List.of(button("🃏 Сделать расклад", "start_tarot"))));
    }

    private void sendTarotResult(long chatId, String result) {
        sendMessageWithButtons(chatId, result,
                List.of(
                        List.of(button("🔄 Сделать ещё один расклад", "again")),
                        List.of(button("🧘 Задать уточняющий вопрос", "clarifying_question")),
                        List.of(button("❌ Завершить", "end"))
                ));
    }

    private void sendMessageWithButtons(long chatId, String text, List<List<InlineKeyboardButton>> buttons) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(buttons);
        message.setReplyMarkup(markup);

        try {
            Message sentMessage = execute(message);

            // Убираем старые кнопки, если было предыдущее сообщение с кнопками
            if (lastMessageId.containsKey(chatId)) {
                removeOldButtons(chatId, lastMessageId.get(chatId));
            }

            // Сохраняем ID последнего отправленного сообщения
            lastMessageId.put(chatId, sentMessage.getMessageId());

        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void removeOldButtons(long chatId, int messageId) {
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(chatId);
        editMarkup.setMessageId(messageId);
        editMarkup.setReplyMarkup(null); // Убираем кнопки

        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            log.warn("Не удалось убрать старые кнопки: " + e.getMessage());
        }
    }

    private void sendTarotOptions(long chatId) {
        sendMessageWithButtons(chatId, "Что ты хочешь сделать дальше?",
                List.of(
                        List.of(button("🔄 Сделать ещё один расклад", "again")),
                        List.of(button("🧘 Задать уточняющий вопрос", "clarifying_question")),
                        List.of(button("❌ Завершить", "end"))
                ));
    }

    private InlineKeyboardButton button(String text, String data) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(data);
        return btn;
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }
}
