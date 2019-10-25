package com.ksm.kakao.repository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@Entity
public class UserInfo {
    @Id
    private String id;

    @Column
    private String password;
}
