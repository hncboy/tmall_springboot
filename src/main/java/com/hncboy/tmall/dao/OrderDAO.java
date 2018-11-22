package com.hncboy.tmall.dao;

import com.hncboy.tmall.pojo.Order;
import com.hncboy.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/12
 * Time: 16:59
 */
public interface OrderDAO extends JpaRepository<Order, Integer> {

    List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
