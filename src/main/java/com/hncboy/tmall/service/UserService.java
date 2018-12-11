package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.UserDAO;
import com.hncboy.tmall.pojo.User;
import com.hncboy.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/12
 * Time: 12:27
 */
@Service
@CacheConfig(cacheNames = "users")
public class UserService {

    @Autowired
    private UserDAO userDAO;

    /**
     * 用户名是否重复
     *
     * @param name
     * @return
     */
    public boolean isExist(String name) {
        User user = getByName(name);
        return null != user;
    }

    /**
     * 登录
     *
     * @param name
     * @param password
     * @return
     */
    @Cacheable(key = "'users-one-name-'+ #p0 +'-password-'+ #p1")
    public User get(String name, String password) {
        return userDAO.getByNameAndPassword(name, password);
    }

    /**
     * 注册
     *
     * @param user
     */
    @CacheEvict(allEntries = true)
    public void add(User user) {
        userDAO.save(user);
    }

    @Cacheable(key = "'users-page-'+#p0+ '-' + #p1")
    public Page4Navigator<User> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = userDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    @Cacheable(key = "'users-one-name-'+ #p")
    public User getByName(String name) {
        return userDAO.findByName(name);
    }
}
