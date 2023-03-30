package com.dku.mentoring.team.entity;

import com.dku.mentoring.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Team {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;


    @OneToMany(mappedBy = "team",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    private String teamName;
}
