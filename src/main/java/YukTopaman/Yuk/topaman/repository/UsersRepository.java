package YukTopaman.Yuk.topaman.repository;

import YukTopaman.Yuk.topaman.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    boolean existsUsersByChatId(String chatId);

    boolean existsUsersByContact(String contact);

    Users findUsersByChatId(String chatId);

    Users findUsersByContact(String contact);

    List<Users> findAllByRegion(String region);
}
