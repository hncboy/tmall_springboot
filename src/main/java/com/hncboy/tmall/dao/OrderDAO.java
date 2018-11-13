package com.hncboy.tmall.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hncboy.tmall.pojo.Order;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/12
 * Time: 16:59
 */
public interface OrderDAO extends JpaRepository<Order, Integer> {
}
