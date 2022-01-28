package it.unical.unijira.data.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserScoreboard extends AbstractBaseEntity {


    @Id
    @GeneratedValue
    private Long id;


    @Column
    private int score;


    @ManyToOne
    @JoinColumn
    private User user;


    @ManyToOne
    @JoinColumn
    private Sprint sprint;

}
