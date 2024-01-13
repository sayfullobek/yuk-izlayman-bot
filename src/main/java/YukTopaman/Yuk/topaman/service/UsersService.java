package YukTopaman.Yuk.topaman.service;

import YukTopaman.Yuk.topaman.entity.Users;
import YukTopaman.Yuk.topaman.entity.enums.RoleName;
import YukTopaman.Yuk.topaman.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;

    public String register(String chatId, String username, String firstName, String lastName, RoleName roleName, String contact) {
        boolean existPhoneNumber = usersRepository.existsUsersByContact(chatId);
        if (!existPhoneNumber) {
            boolean existChatId = usersRepository.existsUsersByChatId(chatId);
            if (!existChatId) {
                Users users = Users.builder()
                        .chatId(chatId)
                        .username(username)
                        .firstName(firstName)
                        .lastName(lastName)
                        .buyurtmas(null)
                        .size(0)
//                        .pay(null)
                        .build();
                if (roleName.equals(RoleName.ZAKAZ_BERUVCHI)) {
                    users.setRoleName(RoleName.ZAKAZ_BERUVCHI);
                } else {
                    users.setRoleName(RoleName.ZAKAZ_OLUVCHI);
                    users.setContact(contact);
                }
                usersRepository.save(users);
                return "Saqlandi";
            }
            Users usersByChatId = usersRepository.findUsersByChatId(chatId);
            if (usersByChatId != null) {
                if (!usersByChatId.getRoleName().equals(roleName)) {
                    usersByChatId.setRoleName(roleName);
                    usersRepository.save(usersByChatId);
                    return "role Almashtirildi";
                }
                return "Almashtirilmadi";
            }
            return "User mavjud emas";
        }
        Users usersByContact = usersRepository.findUsersByContact(contact);
        if (usersByContact != null) {
            if (!usersByContact.getRoleName().equals(roleName)) {
                usersByContact.setRoleName(roleName);
                usersRepository.save(usersByContact);
                return "role Almashtirildi";
            }
            return "Almashtirilmadi";
        }
        return "User mavjud emas";
    }

    public void changeRegion(String chatId, String regionName) {
        Users usersByChatId = usersRepository.findUsersByChatId(chatId);
        if (usersByChatId != null) {
            usersByChatId.setRegion(regionName);
            usersRepository.save(usersByChatId);
        }
    }

    public Users getUserByChatId(String chatId) {
        return usersRepository.findUsersByChatId(chatId);
    }

    public Integer getUserSize() {
        return usersRepository.findAll().size();
    }
}
