import Metrika.Botan;
import Metrika.BotanConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot{

    private static Map<Long, String> users = new HashMap<>();
    private static ReplyKeyboardMarkup keyboard;

    private static CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
    private static Botan botan;

    // Инициализация клавиатуры
    static {
        keyboard = new ReplyKeyboardMarkup().setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Профиль" + EmojiCode.getEmoji("man_in_tuxedo"));
        row1.add("Трофеи" + EmojiCode.getEmoji("trophy"));
        row1.add("Бои" + EmojiCode.getEmoji("dagger"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Сундуки" + EmojiCode.getEmoji("fleur_de_lis"));
        row2.add("Сезон" + EmojiCode.getEmoji("crossed_swords"));
        row2.add("Клан" + EmojiCode.getEmoji("dancers"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Помощь" + EmojiCode.getEmoji("interrobang"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        keyboard.setKeyboard(rows);
    }

    // Запуск метрики
    static {
        client.start();
        botan = new Botan(client, new ObjectMapper());
    }

    /**
     * Инициализация API
     * Регистрация бота
     */
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();

        try {
            api.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            System.err.println("Ошибка регистрации!");
            e.printStackTrace();
        }
    }

    /**
     * Чтение входящего запроса
     * Генерация ответа
     * @param update
     */
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String userTag = users.getOrDefault(message.getChatId(), "");
        String userName = message.getFrom().getUserName();

        if (message.getText().equals("/start")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Start");

            String msg = "Привет! Я - Clash Royale Bot " + EmojiCode.getEmoji("robot_face") + "\n\n"
                    + "Я легко могу показать тебе:\n"
                    + "-Общую статистику профиля " + EmojiCode.getEmoji("scroll") + "\n"
                    + "-Статистику по трофеям " + EmojiCode.getEmoji("sports_medal") + "\n"
                    + "-Статистику по боям " + EmojiCode.getEmoji("hammer_and_pick") + "\n"
                    + "-Информацию по последнему сезону " + EmojiCode.getEmoji("crossed_swords") + "\n"
                    + "-Информацию о сундуках " + EmojiCode.getEmoji("fleur_de_lis") + "\n"
                    + "-Информацию о клане " + EmojiCode.getEmoji("shield") + "\n\n"
                    + "Чтобы всё это посмотреть - просто введи свой TAG в формате #...";
            sendMsg(message,msg);
        }
        else if (message.getText().contains("#")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Edit TAG");

            userTag = message.getText().substring(1);
            try {
                if (ApiClashRoyale.connection(userTag)) {
                    users.put(message.getChatId(), userTag);
                    sendKeyboard(message, "Выбирай!", keyboard);
                }else {
                    sendMsg(message, "Введен некорректный TAG\nВведите TAG в формате #...");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (message.getText().contains("Помощь") || message.getText().contains("/help")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Help");

            String msg = ("Clash Royale Bot\n"
                    + "Версия 1.0\n\n"
                    + "/start - начать работу с ботом\n\n"
                    + "Чтобы изменить TAG - отправьте новый TAG в формате #...");
            sendKeyboard(message, msg, keyboard);
        }
        else if (message.getText().contains("Профиль")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Profile");

            try {
                sendKeyboard(message, ApiClashRoyale.getProfile(userTag), keyboard);
            } catch (IOException e) {
                sendMsg(message, "Введите TAG в формате #...");
            }
        }
        else if (message.getText().contains("Трофеи")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Trophies");

            try {
                sendKeyboard(message, ApiClashRoyale.getTrophies(userTag), keyboard);
            } catch (Exception e) {
                sendMsg(message, "Введите TAG в формате #...");
            }
        }
        else if (message.getText().contains("Бои")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Battles");

            try {
                sendKeyboard(message, ApiClashRoyale.getBattles(userTag), keyboard);
            } catch (Exception e) {
                sendMsg(message, "Введите TAG в формате #...");
            }
        }
        else if (message.getText().contains("Сундуки")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Chests");

            try {
                sendKeyboard(message, ApiClashRoyale.getChests(userTag), keyboard);
            } catch (Exception e) {
                sendMsg(message, "Введите TAG в формате #...");
            }
        }
        else if (message.getText().contains("Сезон")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Season");

            try {
                sendKeyboard(message, ApiClashRoyale.getSeason(userTag), keyboard);
            } catch (Exception e) {
                sendMsg(message, "Введите TAG в формате #...");
            }
        }
        else if (message.getText().contains("Клан")) {
            botan.track(BotanConfig.APIKEY, userName, message, "Clan");

            try {
                sendKeyboard(message, ApiClashRoyale.getClan(userTag), keyboard);
            } catch (Exception e) {
                sendMsg(message, "Введите TAG в формате #...");
            }
        }
        else sendMsg(message, "Я не знаю такой команды " + EmojiCode.getEmoji("disappointed"));
    }

    /**
     * Отправление текстового сообщения
     * @param message
     * @param text
     */
    @SuppressWarnings("deprecation")
    private void sendMsg(Message message, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(message.getChatId());
        s.setText(text);
        try {
            sendMessage(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправление текстового сообщения и клавиатуры
     * @param message
     * @param text
     * @param keyboard
     */
    @SuppressWarnings("deprecation")
    private void sendKeyboard(Message message,String text, ReplyKeyboardMarkup keyboard) {
        SendMessage s = new SendMessage();
        s.setChatId(message.getChatId());
        s.setText(text);
        s.setReplyMarkup(keyboard);
        try {
            sendMessage(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Имя Бота
     */
    public String getBotUsername() {
        return BotConfig.USERNAME;
    }

    /**
     * @return Токен Бота
     */
    public String getBotToken() {
        return BotConfig.TOKEN;
    }
}
