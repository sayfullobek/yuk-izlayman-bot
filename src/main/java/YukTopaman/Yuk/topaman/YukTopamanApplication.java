package YukTopaman.Yuk.topaman;

import YukTopaman.Yuk.topaman.bot.Bot;
import YukTopaman.Yuk.topaman.service.BuyurtmaService;
import YukTopaman.Yuk.topaman.service.PayService;
import YukTopaman.Yuk.topaman.service.UsersService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class YukTopamanApplication {
    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext run = SpringApplication.run(YukTopamanApplication.class, args);
            UsersService usersService = run.getBean(UsersService.class);
            BuyurtmaService buyurtmaService = run.getBean(BuyurtmaService.class);
            PayService payService = run.getBean(PayService.class);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot(usersService, buyurtmaService, payService));
        } catch (TelegramApiException e) {
            System.err.println("Not bot");
        }
    }
}
