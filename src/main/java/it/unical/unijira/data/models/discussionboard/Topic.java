package it.unical.unijira.data.models.discussionboard;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.Project;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Topic extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;


    @Column
    @Basic(optional = false)
    private String summary;


    @ManyToOne
    @JoinColumn
    private Project project;


    @ManyToOne
    @JoinColumn
    private User user;


    @OneToMany(mappedBy = "topic", fetch = FetchType.EAGER)
    private List<Message> messages;
}
