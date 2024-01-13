package YukTopaman.Yuk.topaman.service;

import YukTopaman.Yuk.topaman.entity.Buyurtma;
import YukTopaman.Yuk.topaman.entity.Users;
import YukTopaman.Yuk.topaman.repository.BuyurtmaRepository;
import YukTopaman.Yuk.topaman.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyurtmaService {
    private final UsersRepository usersRepository;
    private final BuyurtmaRepository buyurtmaRepository;

    public void buyurtma(String chatId, Buyurtma buyurtma) {
        Users usersByChatId = usersRepository.findUsersByChatId(chatId);
        if (usersByChatId != null) {
            Buyurtma save = buyurtmaRepository.save(buyurtma);
            usersByChatId.getBuyurtmas().add(save);
            usersRepository.save(usersByChatId);
        }
    }

    public List<Users> getBuyurtmaByRegion(String regionName) {
        return usersRepository.findAllByRegion(regionName);
    }

    public List<Buyurtma> getBuyurtmaByUserChatId(String chatId) {
        Users usersByChatId = usersRepository.findUsersByChatId(chatId);
        return usersByChatId.getBuyurtmas();
    }

    public Buyurtma getBuyurtmaById(Integer id) {
        return buyurtmaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("getBuyurtma"));
    }
}
