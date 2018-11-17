package com.hncboy.tmall.dao;

import com.hncboy.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/12
 * Time: 12:26
 */
public interface UserDAO extends JpaRepository<User, Integer> {

    User findByName(String name);
}
