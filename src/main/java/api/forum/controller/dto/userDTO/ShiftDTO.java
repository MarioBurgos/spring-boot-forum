package api.forum.controller.dto.userDTO;

import api.forum.model.enums.Shift;

public class ShiftDTO {
    private Shift shift;

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
}
