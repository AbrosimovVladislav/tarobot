package io.io.tarobot.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AIJavaHelperBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final GeneralGPTService gptService;

    private final Map<Long, String> userLastQuery = new HashMap<>();
    private final Map<Long, Integer> lastMessageId = new HashMap<>();
    private final Map<Long, BotState> userStates = new HashMap<>();

    public AIJavaHelperBot(String botUsername, String botToken, GeneralGPTService gptService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.gptService = gptService;
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleText(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    private void handleText(Message message) {
        long chatId = message.getChatId();
        String userInput = message.getText();
        BotState state = userStates.getOrDefault(chatId, BotState.IDLE);

        if (userInput.equals("/start")) {
            sendWelcome(chatId);
            return;
        }

        if (state == BotState.AWAITING_QUESTION) {
            userLastQuery.put(chatId, userInput);
            userStates.put(chatId, BotState.AWAITING_CONFIRMATION);
            sendMessageWithButtons(chatId, "Отправить сообщение?", List.of(
                    List.of(button("✅ Да", "confirm")),
                    List.of(button("❌ Нет", "cancel"))
            ));
        } else {
            sendMessage(chatId, "Пожалуйста, задай свой вопрос.");
            userStates.put(chatId, BotState.AWAITING_QUESTION);
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        switch (data) {
            case "confirm" -> {
                String question = userLastQuery.get(chatId);
                if (question == null) {
                    sendMessage(chatId, "Сначала задай вопрос.");
                    userStates.put(chatId, BotState.AWAITING_QUESTION);
                    return;
                }

                try {
                    Message loadingMsg = execute(new SendMessage(String.valueOf(chatId), "🔮 Собираю мысли…"));
                    simulateLoading(chatId, loadingMsg.getMessageId(), List.of(
                            "✨ Связываюсь со Вселенной…",
                            "🧘 Получаю мудрость…"
                    ));
                    String response = gptService.ask(chatId, question);
                    execute(EditMessageText.builder()
                            .chatId(chatId)
                            .messageId(loadingMsg.getMessageId())
                            .text(response)
                            .build());
                    sendMessageWithButtons(chatId, "Хочешь задать другой вопрос?", List.of(
                            List.of(button("🔁 Да", "restart")),
                            List.of(button("❌ Нет", "cancel"))
                    ));
                    userStates.put(chatId, BotState.IDLE);
                } catch (TelegramApiException e) {
                    log.error("Ошибка при отправке запроса", e);
                }
            }
            case "cancel", "restart" -> {
                sendMessage(chatId, "Задай свой вопрос.");
                userStates.put(chatId, BotState.AWAITING_QUESTION);
            }
        }
    }

    private void sendWelcome(long chatId) {
        sendMessage(chatId, "Привет! Задай свой вопрос, и я помогу найти ответ ✨");
        userStates.put(chatId, BotState.AWAITING_QUESTION);
    }

    private void simulateLoading(long chatId, int messageId, List<String> messages) {
        for (String msg : messages) {
            try {
                Thread.sleep(1500);
                execute(EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .text(msg)
                        .build());
            } catch (InterruptedException | TelegramApiException e) {
                log.warn("Ошибка при обновлении загрузки", e);
            }
        }
    }

    private InlineKeyboardButton button(String text, String data) {
        return InlineKeyboardButton.builder().text(text).callbackData(data).build();
    }

    private void sendMessage(long chatId, String text) {
        try {
            execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void sendMessageWithButtons(long chatId, String text, List<List<InlineKeyboardButton>> buttons) {
        try {
            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setText(text);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(buttons);
            msg.setReplyMarkup(markup);
            Message sent = execute(msg);
            lastMessageId.put(chatId, sent.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения с кнопками", e);
        }
    }

    public enum BotState {
        IDLE,
        AWAITING_QUESTION,
        AWAITING_CONFIRMATION
    }
}