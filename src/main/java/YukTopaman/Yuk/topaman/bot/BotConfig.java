package YukTopaman.Yuk.topaman.bot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BotConfig {
    String BOT_USERNAME = "yuk_topaman_bot";
    String BOT_TOKEN = "6776170493:AAF2js77hUjyLP0tgOfSNcpFNCyBedaUifg";
    List<String> START_BTN = Arrays.asList(
            "Buyurtma oluvchi ⛟", "Buyurtma beruvchi \uD83D\uDE9A"
    );

    List<String> BUYURTMA_STAR_BTN = Arrays.asList(
            "Buyurtma berish", "Mening buyurtmalarim", "Foydalanuvchilar soni"
    );

    Map<String, String> IS_REGISTER = new HashMap<>();
    Map<String, String> IS_CONTACT = new HashMap<>();

    Map<String, String> IS_BUYURTMA = new HashMap<>();
    Map<String, String> IS_FIRST_REGION = new HashMap<>();
    Map<String, String> IS_LAST_REGION = new HashMap<>();
    Map<String, String> IS_BUYURTMA_CONTACT = new HashMap<>();
    Map<String, String> IS_DESCRIPTION = new HashMap<>();

    Map<String, String> OUT_REGION = new HashMap<>();
    Map<String, String> IN_REGION = new HashMap<>();
    Map<String, String> IS_DES = new HashMap<>();

    Map<String, String> IS = new HashMap<>();
    Map<String, String> IS_POSITION = new HashMap<>();

    Map<String, String> IS_TOLOV = new HashMap<>();
    Map<String, String> IS_TARIF = new HashMap<>();
    Map<String, String> IS_PHOTO = new HashMap<>();

    Map<String, String> IS_ADMIN_VALIDATE_PAY = new HashMap<>();
    Map<String, String> IS_ADMIN_VALIDATE_ID = new HashMap<>();

    List<String> OLUVCHI_BTN = Arrays.asList(
            "Joylashuv⚕️", "Foydalanuvchilar soni", "To'lov"
    );

    List<String> TOLOV = Arrays.asList(
            "10 ta nomer 10 000 so'm", "20 ta nomer 18 000 so'm", "50 ta nomer 40 000 so'm", "100 ta nomer 70 000 so'm", "200 ta nomer 130 000 so'm", "Asosiy bo'limga qaytish"
    );
    List<String> regions = Arrays.asList(
            "Toshkent", "Andijon", "Farg'ona", "Namangan", "Sirdaryo", "Jizzax", "Samarqand", "Navoiy", "Buxoro", "Qashqadaryo", "Surxondaryo", "Xorazm", "Qoraqalpog'iston", "Orqaga"
    );

    List<String> BUYURTMA_REGIONS = Arrays.asList(
            "Toshkent", "Andijon", "Farg'ona", "Namangan", "Sirdaryo", "Jizzax", "Samarqand", "Navoiy", "Buxoro", "Qashqadaryo", "Surxondaryo", "Xorazm", "Qoraqalpog'iston", "Buyurtmani bekor qilish"
    );

    String ADMIN_CHATID = "689635476";
    String ADMIN_CHATID_MY = "5555360669";
}
