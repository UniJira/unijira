package it.unical.unijira.data.models.discussions;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.User;
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
public class Message extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;


    @Column
    @Basic(optional = false)
    private String content;


    @ManyToOne
    @JoinColumn
    private Topic topic;


    @ManyToOne
    @JoinColumn
    private User author;


    @ManyToOne
    @JoinColumn
    private Message repliesTo;

    @OneToMany(mappedBy = "repliesTo", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Message> myReplies;
}
