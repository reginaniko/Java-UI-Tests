import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserResponse {
    private User user;
    private String accessToken;
    private String refreshToken;
    private Boolean success;
    private String message;
}
