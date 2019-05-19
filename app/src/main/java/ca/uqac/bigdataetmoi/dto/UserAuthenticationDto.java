package ca.uqac.bigdataetmoi.dto;

public class UserAuthenticationDto {
    private String email;
    private String password;

    public UserAuthenticationDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
