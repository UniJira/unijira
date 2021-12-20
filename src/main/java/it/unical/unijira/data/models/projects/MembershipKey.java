package it.unical.unijira.data.models.projects;

import it.unical.unijira.data.models.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Getter @Setter @ToString
public class MembershipKey implements Serializable {

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Project project;

    public MembershipKey(User user, Project project) {
        this.user = user;
        this.project = project;
    }

    public MembershipKey() {}
}
