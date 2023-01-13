package com.example.basespring.entities;

import com.example.basespring.dto.AccountDto;
import com.example.basespring.dto.request.RegisterRequest;
import com.example.basespring.entities.basic.BaseEntity;
import com.example.basespring.enums.Enums;
import lombok.*;
import org.springframework.beans.BeanUtils;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "accounts")
public class Accounts extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Lob
    private String avt;
    private String firstName;
    private String lastName;
    @NotNull(message = "Username cannot be left blank")
    @Column(name="username")
    private String username;
    @Email(message = "Incorrect email format!, Please re-enter")
    @Column(name="email")
    private String email;
    private String phoneNumber;
    @Column(name="birthday")
    private Date birthday;
    private String gender;
    private String address;
    private String verifyCode = "";
    private String referralCode = "";
    private boolean verified = false;
    @NotNull(message = "Password cannot be left blank")
    @Size(min = 6, message = "password must be greater than or equal to 6")
    @Column(name="password")
    private String password;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "accounts_roles", joinColumns = @JoinColumn(name = "account_id")
            , inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Roles> roles = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private Enums.AccountStatus status = Enums.AccountStatus.DEACTIVE;

    public Accounts(AccountDto accountDto) {
        BeanUtils.copyProperties(accountDto, this);
    }

    public Accounts(RegisterRequest registerRequest){
        BeanUtils.copyProperties(registerRequest,this);
    }
}
