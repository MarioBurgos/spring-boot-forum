package api.forum.controller.dto.userDTO;

import api.forum.model.enums.Status;

public class StatusDTO {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
