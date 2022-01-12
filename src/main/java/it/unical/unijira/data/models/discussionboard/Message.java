package it.unical.unijira.data.models.discussionboard;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.User;
import lombok.*;

import javax.persistence.*;

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
    private String text;


    @ManyToOne
    @JoinColumn
    private Topic topic;


    @ManyToOne
    @JoinColumn
    private User author;


    @ManyToOne
    @JoinColumn
    private Message repliesTo;

}
