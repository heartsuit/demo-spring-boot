package com.heartsuit.tenant;

import java.util.List;

import javax.annotation.Resource;

import com.heartsuit.tenant.entity.User;
import com.heartsuit.tenant.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * 多租户 Tenant 演示
 * </p>
 *
 * @author hubin
 * @since 2018-08-11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TenantTest {

    @Resource
    private UserMapper mapper;

    @Test
    public void aInsert() {
        User user = new User();
        user.setName("ok");
        Assert.assertTrue(mapper.insert(user) > 0);
        user = mapper.selectById(user.getId());
        Assert.assertTrue(1 == user.getTenantId());
    }

    @Test
    public void bDelete() {
        Assert.assertTrue(mapper.deleteById(3L) > 0);
    }

    @Test
    public void cUpdate() {
        Assert.assertTrue(mapper.updateById(new User().setId(1L).setName("mp")) > 0);
    }

    @Test
    public void dSelect() {
        List<User> userList = mapper.selectList(null);
        userList.forEach(u -> Assert.assertTrue(1 == u.getTenantId()));
    }

    /**
     * 自定义SQL：默认也会增加多租户条件
     * 参考打印的SQL
     */
    @Test
    public void manualSqlTenantFilterTest() {
        System.out.println(mapper.myCount());
    }

    @Test
    public void testTenantFilter() {
        //    mapper.getUserAndAddr().forEach(System.out::println);
        // mapper.getAddrAndUser(null).forEach(System.out::println);
        mapper.getAddrAndUser("add").forEach(System.out::println);
        // mapper.getUserAndAddr(null).forEach(System.out::println);
        // mapper.getUserAndAddr("J").forEach(System.out::println);
    }
}
