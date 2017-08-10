package com.test.article.impl.impl;

import com.test.article.common.model.TUser;
import com.test.article.dao.dao.TUserMapper;
import com.test.article.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TUserServiceImpl implements TUserService {

    @Autowired
    private TUserMapper tUserMapper;

    public TUser getUserById(int id) {
        return tUserMapper.selectByPrimaryKey(id);
    }

    public Integer insert(TUser user) {
        tUserMapper.insertSelective(user);
        if(user.getId()!=null){
            return user.getId();
        }else{
            return 0;
        }
    }
}
