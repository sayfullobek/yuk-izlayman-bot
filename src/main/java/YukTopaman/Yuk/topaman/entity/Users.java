package YukTopaman.Yuk.topaman.entity;

import YukTopaman.Yuk.topaman.entity.enums.RoleName;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String chatId;
    private String username;
    private String firstName;
    private String lastName;

    private Integer size;

    private String contact;

    private String region;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Buyurtma> buyurtmas;

//    @OneToMany
//    private List<Pay> pay;

    @Enumerated(value = EnumType.STRING)
    private RoleName roleName;
}
