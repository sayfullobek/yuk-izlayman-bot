package YukTopaman.Yuk.topaman.bot;

import YukTopaman.Yuk.topaman.entity.Buyurtma;
import YukTopaman.Yuk.topaman.entity.Users;
import YukTopaman.Yuk.topaman.entity.enums.RoleName;
import YukTopaman.Yuk.topaman.service.BuyurtmaService;
import YukTopaman.Yuk.topaman.service.PayService;
import YukTopaman.Yuk.topaman.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final UsersService usersService;
    private final BuyurtmaService buyurtmaService;
    private final PayService payService;
    private static final Logger log = LoggerFactory.getLogger(Bot.class);

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String text = message.getText();
            User from = message.getFrom();
            if (message.hasText()) {
                Integer messageId = message.getMessageId();
                if (text.equals("/start")) {
                    sendMsg(chatId, "Salom hush kelibsiz " + from.getFirstName(), messageId);
                    getBtn(chatId, "Tanlang", BotConfig.START_BTN);
                } else if (text.equals(BotConfig.START_BTN.get(0))) {
                    shareContact(chatId, "Kontaktni ulashing", messageId);
                    BotConfig.IS_REGISTER.put(chatId, "contact");
                } else if (text.equals(BotConfig.OLUVCHI_BTN.get(0))) {
                    getBtn(chatId, "Tanlang", BotConfig.regions);
                    BotConfig.IS.put(chatId, "pos");
                } else if (text.equals(BotConfig.regions.get(13))) {
                    getBtn(chatId, "Tanlang", BotConfig.OLUVCHI_BTN);
                    BotConfig.IS.remove(chatId);
                } else if (text.equals(BotConfig.START_BTN.get(1))) {
                    getBtn(chatId, "Tanlang", BotConfig.BUYURTMA_STAR_BTN);
                    usersService.register(chatId, from.getUserName(), from.getFirstName(), from.getLastName(), RoleName.ZAKAZ_BERUVCHI, "");
                } else if (BotConfig.BUYURTMA_STAR_BTN.get(1).equals(text)) {
                    List<Buyurtma> buyurtmaByUserChatId = buyurtmaService.getBuyurtmaByUserChatId(chatId);
                    for (Buyurtma buyurtma : buyurtmaByUserChatId) {
                        sendMsg(chatId, "Assalomu aleykum\n" + buyurtma.getFirstRegion() + " Viloyatidan " + buyurtma.getLastRegion() + " Viloyatiga yuk bor\nYuk haqida " + buyurtma.getDescription(), messageId);
                    }
                    sendMsg(chatId, "Sizning buyurtmalaringiz...", messageId);
                } else if (BotConfig.BUYURTMA_STAR_BTN.get(2).equals(text)) {
                    sendMsg(chatId, "Bizning botimizda " + usersService.getUserSize() + "ta foydalanuvchi mavjud", messageId);
                } else if (text.equals(BotConfig.BUYURTMA_REGIONS.get(13))) {
                    getBtn(chatId, "Tanlang", BotConfig.BUYURTMA_STAR_BTN);
                } else if (text.equals(BotConfig.OLUVCHI_BTN.get(2))) {
                    getBtn(chatId, "Raqamlarni ko'rish uchun quyidagi trafiklardan birini sotib oling", BotConfig.TOLOV);
                    BotConfig.IS_TOLOV.put(chatId, "tolov");
                } else if (text.equals(BotConfig.BUYURTMA_STAR_BTN.get(0))) {
                    getBtnOut(chatId, "Viloyatingizni tanlang", BotConfig.regions);
                    BotConfig.IS_BUYURTMA.put(chatId, "buyurtma_region");
                } else if (BotConfig.IS_BUYURTMA.get(chatId) != null) {
                    switch (BotConfig.IS_BUYURTMA.get(chatId)) {
                        case "buyurtma_region":
                            getBtnOut(chatId, "Chiquvchi viloyatni tanlang", BotConfig.regions);
                            BotConfig.IS_FIRST_REGION.put(chatId, text);
                            BotConfig.IS_BUYURTMA.replace(chatId, "buyurtma_region_out");
                            BotConfig.OUT_REGION.put(chatId, text);
                            break;
                        case "buyurtma_region_out":
                            shareContact(chatId, "Telefon raqamingizni ulashing", messageId);
                            BotConfig.IS_BUYURTMA.replace(chatId, "phoneNumber");
                            BotConfig.IS_LAST_REGION.put(chatId, text);
                            BotConfig.IN_REGION.put(chatId, text);
                            break;
                        case "description_buyurtma":
                            sendMsg(chatId, "Buyurtmangiz qabul qilindi", messageId);
                            BotConfig.IS_DESCRIPTION.put(chatId, text);
                            getBtn(chatId, "Tanlang", BotConfig.BUYURTMA_STAR_BTN);
                            Buyurtma buyurtma = Buyurtma.builder()
                                    .firstRegion(BotConfig.IS_FIRST_REGION.get(chatId))
                                    .lastRegion(BotConfig.IS_LAST_REGION.get(chatId))
                                    .phoneNumber(BotConfig.IS_BUYURTMA_CONTACT.get(chatId))
                                    .description(BotConfig.IS_DESCRIPTION.get(chatId))
                                    .build();
                            buyurtmaService.buyurtma(chatId, buyurtma);
                            List<Users> buyurtmaByRegion = buyurtmaService.getBuyurtmaByRegion(BotConfig.IS_FIRST_REGION.get(chatId));
                            for (Users users : buyurtmaByRegion) {
                                sendMsgBuyurtma(users.getChatId(), "Assalomu aleykum\n" + buyurtma.getFirstRegion() + " Viloyatidan " + buyurtma.getLastRegion() + " Viloyatiga yuk bor\nYuk haqida " + buyurtma.getDescription(), buyurtma.getId());
                            }
                            BotConfig.IS_BUYURTMA.remove(chatId);
                            break;
                    }
                } else if (BotConfig.IS.get(chatId) != null) {
                    if (BotConfig.IS.get(chatId).equals("pos")) {
                        getBtn(chatId, "Endi sizga " + text + " bo'ylab harakatlanadigan yuklar aylanmasi yuborliadi", BotConfig.OLUVCHI_BTN);
                        usersService.changeRegion(chatId, text);
                        BotConfig.IS_POSITION.put(chatId, text);
                        BotConfig.IS.remove(chatId);
                    }
                } else if (BotConfig.IS_TOLOV.get(chatId) != null) {
                    if (BotConfig.IS_TOLOV.get(chatId).equals("tolov")) {
                        if (text.equals(BotConfig.TOLOV.get(5))) {
                            getBtn(chatId, "To'lov berkor qilindi", BotConfig.OLUVCHI_BTN);
                            BotConfig.IS_TOLOV.remove(chatId);
                        } else {
                            int tr = 0;
                            for (int i = 0; i < BotConfig.TOLOV.size() - 1; i++) {
                                if (BotConfig.TOLOV.get(i).equals(text)) {
                                    tr++;
                                }
                            }
                            if (tr == 1) {
                                sendMsg(chatId, "Ushbu tarifni sotib olish uchun ushbu kartaga kerakli summani kiriting", messageId);
                                BotConfig.IS_TARIF.put(chatId, text);
                                BotConfig.IS_TOLOV.replace(chatId, "rasm");
                            } else {
                                sendMsg(chatId, "Bunday to'lov turi mavjud emas iltimos tekshirib qayta tanlang", messageId);
                            }
                        }
                    } else if (BotConfig.IS_TOLOV.get(chatId).equals("rasm")) {
                        sendMsg(chatId, "Iltimos rasm kiriting.", messageId);
                    }
                } else {
                    sendMsg(chatId, "Bunday bo'lim mavjud emas", messageId);
                }
            } else if (message.hasContact()) {
                Contact contact = message.getContact();
                String id = contact.getUserId().toString();
                String phoneNumber = contact.getPhoneNumber();
                if (BotConfig.IS_BUYURTMA.get(id) != null) {
                    if (BotConfig.IS_BUYURTMA.get(id).equals("phoneNumber")) {
                        sendMsg(chatId, "Ushbu buyurtma haqida ma'lumot yozing", message.getMessageId());
                        BotConfig.IS_BUYURTMA_CONTACT.put(chatId, contact.getPhoneNumber());
                        BotConfig.IS_BUYURTMA.replace(chatId, "description_buyurtma");
                    }
                } else if (BotConfig.IS_REGISTER.get(chatId) != null) {
                    if (BotConfig.IS_REGISTER.get(chatId).equals("contact")) {
                        getBtn(id, "Tanlang", BotConfig.OLUVCHI_BTN);
                        BotConfig.IS_CONTACT.put(chatId, phoneNumber);
                        BotConfig.IS_REGISTER.remove(chatId);
                        usersService.register(chatId, from.getUserName(), contact.getFirstName(), contact.getLastName(), RoleName.ZAKAZ_OLUVCHI, contact.getPhoneNumber());
                    }
                }
            } else if (message.hasPhoto()) {
                if (BotConfig.IS_TOLOV.get(chatId).equals("rasm")) {
                    PhotoSize photo = message.getPhoto().stream()
                            .max((p1, p2) -> Integer.compare(p1.getFileSize(), p2.getFileSize()))
                            .orElse(null);
                    if (photo != null) {
                        String fileId = photo.getFileId();
                        File imageFile = getFile(fileId);
                        getBtn(chatId, "To'lov amalga oshirilganini bilish uchun ozroq kuting...", BotConfig.OLUVCHI_BTN);
                        assert imageFile != null;
                        BotConfig.IS_PHOTO.put(chatId, fileId);
                        BotConfig.IS_TOLOV.remove(chatId);
                        sendTolovAdmin(BotConfig.ADMIN_CHATID_MY, "To'lov bo'ldimi?", imageFile);
                        BotConfig.IS_ADMIN_VALIDATE_PAY.put(BotConfig.ADMIN_CHATID_MY, "is_pay");
                        BotConfig.IS_ADMIN_VALIDATE_ID.put(BotConfig.ADMIN_CHATID_MY, chatId);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            String chatId = callbackQuery.getFrom().getId().toString();
            String[] validate = data.split(" : ");
            if (BotConfig.IS_ADMIN_VALIDATE_PAY.get(BotConfig.ADMIN_CHATID_MY) != null) {
                if (BotConfig.IS_ADMIN_VALIDATE_PAY.get(chatId).equals("is_pay")) {
                    if (data.equals("Xa")) {
                        sendMsgBuyurtma(BotConfig.IS_ADMIN_VALIDATE_ID.get(chatId), "To'lovni muvaffaqiyatli amalga oshirganingiz bilan tabriklayman🫡", 0);
                        payService.pay(BotConfig.IS_TARIF.get(BotConfig.IS_ADMIN_VALIDATE_ID.get(chatId)), BotConfig.IS_PHOTO.get(BotConfig.IS_ADMIN_VALIDATE_ID.get(chatId)), BotConfig.IS_ADMIN_VALIDATE_ID.get(chatId));
                    } else if (data.equals("Yo'q")) {
                        sendMsgBuyurtma(BotConfig.IS_ADMIN_VALIDATE_ID.get(chatId), "To'lov to'g'ri tartibda o'tkazilmagan qayta urinib ko'ring iltimos🤑", 0);
                    }
                    BotConfig.IS_ADMIN_VALIDATE_PAY.remove(chatId);
                    BotConfig.IS_ADMIN_VALIDATE_ID.remove(chatId);
                }
            } else if (validate[0].equals("Tel raqamni ko'rish")) {
                Users userByChatId = usersService.getUserByChatId(chatId);
                if (userByChatId.getSize() == 0) {
                    getBtn(chatId, "Raqamlarni ko'rish uchun quyidagi trafiklardan birini sotib oling", BotConfig.TOLOV);
                    BotConfig.IS_TOLOV.put(chatId, "tolov");
                } else {
                    Buyurtma buyurtmaById = buyurtmaService.getBuyurtmaById(Integer.parseInt(validate[1]));
                    sendMsg(chatId, "Telefon raqam : " + buyurtmaById.getPhoneNumber(), 0);
                    payService.sizeKam(chatId);
                }
            }
        }

    }

    @SneakyThrows
    private File getFile(String fileId) {
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(fileId);
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
            String filePath = file.getFilePath();
            URL fileUrl = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath);

            // Faylni yuklab olish
            try (InputStream in = fileUrl.openStream();
                 FileOutputStream out = new FileOutputStream("downloaded_image.jpg")) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            return new File("downloaded_image.jpg");
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getBtn(String chatId, String text, List<String> btns) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        Users userByChatId = usersService.getUserByChatId(chatId);
        int tr = 0;
        for (int i = 0; i < btns.size() / 2; i++) {
            KeyboardRow row = new KeyboardRow();
            for (int j = 0; j < 2; j++) {
                KeyboardButton build = KeyboardButton.builder()
                        .text(userByChatId != null && userByChatId.getRegion() != null && userByChatId.getRegion().equals(btns.get(tr)) ? btns.get(tr) + " 📌" : btns.get(tr))
                        .build();
                row.add(build);
                tr++;
            }
            rows.add(row);
        }
        if (btns.size() % 2 != 0) {
            KeyboardRow row = new KeyboardRow();
            KeyboardButton build = KeyboardButton.builder()
                    .text(btns.get(btns.size() - 1))
                    .build();
            row.add(build);
            rows.add(row);
        }
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        try {
            execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(text)
                            .replyMarkup(replyKeyboardMarkup)
                            .build()
            );
        } catch (TelegramApiException e) {
            System.err.println("Not Btn");
        }
    }

    public void getBtnOut(String chatId, String text, List<String> btns) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        int tr = 0;
        for (int i = 0; i < btns.size() / 2; i++) {
            KeyboardRow row = new KeyboardRow();
            for (int j = 0; j < 2; j++) {
                KeyboardButton build = KeyboardButton.builder()
                        .text(btns.get(tr))
                        .build();
                row.add(build);
                tr++;
            }
            rows.add(row);
        }
        if (btns.size() % 2 != 0) {
            KeyboardRow row = new KeyboardRow();
            KeyboardButton build = KeyboardButton.builder()
                    .text(btns.get(btns.size() - 1))
                    .build();
            row.add(build);
            rows.add(row);
        }
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        try {
            execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(text)
                            .replyMarkup(replyKeyboardMarkup)
                            .build()
            );
        } catch (TelegramApiException e) {
            System.err.println("Not Btn");
        }
    }

    public void shareContact(String chatId, String text, Integer messageId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Kontaktni ulashing");
        keyboardButton.setRequestContact(true);
        row.add(keyboardButton);
        keyboardRows.add(row);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        try {
            execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(text)
                            .replyToMessageId(messageId)
                            .replyMarkup(replyKeyboardMarkup)
                            .build()
            );
        } catch (TelegramApiException e) {
            System.err.println("Not share btn");
        }
    }

    public void sendMsg(String chatId, String text, Integer messageId) {
        SendMessage sendMessage = new SendMessage();
        if (messageId == 0) {
            sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
        } else {
            sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
        }
        try {
            execute(
                    sendMessage
            );
        } catch (TelegramApiException e) {
            System.err.println("Not bot");
        }
    }

    public void sendMsgBuyurtma(String chatId, String text, Integer id) {
        SendMessage sendMessage = new SendMessage();
        if (!text.equals("To'lovni muvaffaqiyatli amalga oshirganingiz bilan tabriklayman🫡") && !text.equals("To'lov to'g'ri tartibda o'tkazilmagan qayta urinib ko'ring iltimos🤑")) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> btnss = new ArrayList<>();
            List<InlineKeyboardButton> btns = new ArrayList<>();
            InlineKeyboardButton build = InlineKeyboardButton.builder()
                    .text("Tel raqamni ko'rish")
                    .callbackData("Tel raqamni ko'rish : " + id)
                    .build();
            btns.add(build);
            btnss.add(btns);
            inlineKeyboardMarkup.setKeyboard(btnss);
            sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(inlineKeyboardMarkup)
                    .build();
        } else {
            sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
        }
        try {
            execute(
                    sendMessage
            );
        } catch (TelegramApiException e) {
            System.err.println("Not bot");
        }
    }

    public void sendTolovAdmin(String chatId, String text, File file) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> btnss = new ArrayList<>();
        List<InlineKeyboardButton> btns = new ArrayList<>();
        InlineKeyboardButton xa = InlineKeyboardButton.builder()
                .text("Xa")
                .callbackData("Xa")
                .build();
        InlineKeyboardButton yoq = InlineKeyboardButton.builder()
                .text("Yo'q")
                .callbackData("Yo'q")
                .build();
        btns.add(xa);
        btns.add(yoq);
        btnss.add(btns);
        inlineKeyboardMarkup.setKeyboard(btnss);
        try {
            execute(
                    SendPhoto.builder()
                            .chatId(chatId)
                            .caption(text)
                            .photo(new InputFile(file))
                            .replyMarkup(inlineKeyboardMarkup)
                            .build()
            );
        } catch (TelegramApiException e) {
            System.err.println("Not bot");
        }
    }
}
