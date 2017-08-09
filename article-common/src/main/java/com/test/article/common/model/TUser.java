package com.test.article.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TUser implements Serializable {
    private Integer id;

    private String username;

    private String password;

    private String realname;

    private Byte sex;

    private String email;

    private Date createTime;
}