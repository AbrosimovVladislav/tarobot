package io.io.tarobot.integration.telegram;

import io.io.tarobot.service.AIJavaHelperBot;
import io.io.tarobot.service.GeneralGPTService;
import io.io.tarobot.service.TaroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.helperUsername}")
    private String botUsername;

    @Value("${telegram.bot.helperToken}")
    private String botToken;

    @Autowired
    private TaroService taroService;

    @Autowired
    private GeneralGPTService gptService;

    @Bean
    public TelegramLongPollingBot telegramBot() {
//        return new TaroEmotionsBot("TaroEmotionsBot", "7803345293:AAE6u8MKhARoYH3aV4SfNZnzpKLmL3NvxrA", taroService);
//        return new TaroEmotionsBot(botUsername, botToken, taroService);
        return new AIJavaHelperBot(botUsername, botToken, gptService);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramLongPollingBot bot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
        return botsApi;
    }

}
