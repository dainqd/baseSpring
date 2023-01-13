package com.example.basespring.entities;

import com.example.basespring.entities.basic.BaseEntity;
import com.example.basespring.enums.Enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Roles extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Enums.Roles name = Enums.Roles.USER;
}
