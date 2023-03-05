package api.forum.model.users;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Member extends User {

    private String profilePicture;
    private Date registrationDate;
    private Integer membershipLevel;


    public Member() {
    }

    public Member(String userName, String email, String password, String profilePicture) {
        super(userName, email, password);
        this.profilePicture = profilePicture;
        registrationDate = Date.valueOf(LocalDate.now());
        membershipLevel = 1;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(Integer membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;

        if (getProfilePicture() != null ? !getProfilePicture().equals(member.getProfilePicture()) : member.getProfilePicture() != null)
            return false;
        if (getRegistrationDate() != null ? !getRegistrationDate().equals(member.getRegistrationDate()) : member.getRegistrationDate() != null)
            return false;
        return getMembershipLevel() != null ? getMembershipLevel().equals(member.getMembershipLevel()) : member.getMembershipLevel() == null;
    }

    @Override
    public int hashCode() {
        int result = getProfilePicture() != null ? getProfilePicture().hashCode() : 0;
        result = 31 * result + (getRegistrationDate() != null ? getRegistrationDate().hashCode() : 0);
        result = 31 * result + (getMembershipLevel() != null ? getMembershipLevel().hashCode() : 0);
        return result;
    }
}
