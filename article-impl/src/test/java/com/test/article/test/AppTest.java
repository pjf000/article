package com.test.article.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.test.article.common.model.TUser;
import com.test.article.service.TUserService;
import org.junit.Test;

import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest  extends BaseJunitTest
{
    @Reference
    private TUserService tUserService;
    @Test
    public void testInsert(){
        TUser user = new TUser();
        user.setRealname("jeffpeng");
        user.setUsername("assss");
        user.setEmail("10254444@ssss.com");
        user.setPassword("111111");
        user.setSex(Byte.valueOf("1"));
        user.setCreateTime(new Date());
        int id = tUserService.insert(user);
        System.out.println("——————————————————————————————————————————");
        System.out.println("user_id="+id);
    }

    @Test
    public void testSelect(){
        int id = 1002;
        TUser user = tUserService.getUserById(id);
        System.out.println(user);
    }
}
