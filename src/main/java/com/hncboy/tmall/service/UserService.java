package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.UserDAO;
import com.hncboy.tmall.pojo.User;
import com.hncboy.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
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
    public User get(String name, String password) {
        return userDAO.getByNameAndPassword(name, password);
    }

    /**
     * 注册
     *
     * @param user
     */
    public void add(User user) {
        userDAO.save(user);
    }

    public Page4Navigator<User> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = userDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    public User getByName(String name) {
        return userDAO.findByName(name);
    }
}
