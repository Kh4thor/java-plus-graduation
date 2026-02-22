package malyshev.egor.dto.comment;

import lombok.*;

@Getter
@Setter
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}
