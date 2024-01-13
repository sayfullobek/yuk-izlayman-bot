package YukTopaman.Yuk.topaman.service;

import YukTopaman.Yuk.topaman.entity.Pay;
import YukTopaman.Yuk.topaman.entity.Users;
import YukTopaman.Yuk.topaman.repository.PayRepository;
import YukTopaman.Yuk.topaman.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PayRepository payRepository;
    private final UsersRepository usersRepository;

    public void pay(String type, String photoId, String chatId) {
        Users usersByChatId = usersRepository.findUsersByChatId(chatId);
        String[] arr = {"10 ta nomer 10 000 so'm", "20 ta nomer 18 000 so'm", "50 ta nomer 40 000 so'm", "100 ta nomer 70 000 so'm", "200 ta nomer 130 000 so'm"};
        Integer[] sizes = {10, 20, 50, 100, 200};
        if (usersByChatId != null) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].equals(type)) {
                    usersByChatId.setSize(usersByChatId.getSize() + sizes[i]);
                    Users save = usersRepository.save(usersByChatId);
                    Pay pay = Pay.builder()
                            .payType(type)
                            .photoId(photoId)
                            .users(save)
                            .build();
                    payRepository.save(pay);
                    break;
                }
            }
        }
    }

    public void sizeKam(String chatId) {
        Users usersByChatId = usersRepository.findUsersByChatId(chatId);
        if (usersByChatId != null) {
            usersByChatId.setSize(usersByChatId.getSize() - 1);
            usersRepository.save(usersByChatId);
        }
    }
}
