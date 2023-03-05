package api.forum.model.users;

import api.forum.model.enums.Shift;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends User{
    @OneToMany(mappedBy = "admin")
    private List<Log> actions;
    @Enumerated(EnumType.STRING)
    private Shift shift;
    private String location;

    public Admin() {
    }

    public Admin(String userName, String email, String password) {
        super(userName, email, password);
        super.setRoles(List.of(new Role("ADMIN")));
        actions = new ArrayList<Log>();
    }

    public List<Log> getActions() {
        return actions;
    }

    public void setActions(List<Log> actions) {
        this.actions = actions;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin admin)) return false;

        if (getActions() != null ? !getActions().equals(admin.getActions()) : admin.getActions() != null) return false;
        if (getShift() != null ? !getShift().equals(admin.getShift()) : admin.getShift() != null) return false;
        return getLocation() != null ? getLocation().equals(admin.getLocation()) : admin.getLocation() == null;
    }

    @Override
    public int hashCode() {
        int result = getActions() != null ? getActions().hashCode() : 0;
        result = 31 * result + (getShift() != null ? getShift().hashCode() : 0);
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        return result;
    }
}
