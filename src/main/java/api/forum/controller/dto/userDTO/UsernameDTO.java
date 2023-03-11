package api.forum.controller.dto.userDTO;

public class UsernameDTO {
    private String username;

    public UsernameDTO() {
    }

    public UsernameDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
