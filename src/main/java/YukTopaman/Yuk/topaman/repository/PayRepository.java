package YukTopaman.Yuk.topaman.repository;

import YukTopaman.Yuk.topaman.entity.Pay;
import YukTopaman.Yuk.topaman.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayRepository extends JpaRepository<Pay, Integer> {
    List<Pay> findAllByUsers(Users users);
}
