package com.test.article.service;

import com.test.article.common.model.TUser;

public interface TUserService {

    TUser getUserById(int id);

    Integer insert(TUser user);
}
