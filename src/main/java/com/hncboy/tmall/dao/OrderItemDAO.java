package com.hncboy.tmall.dao;

import com.hncboy.tmall.pojo.Order;
import com.hncboy.tmall.pojo.OrderItem;
import com.hncboy.tmall.pojo.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/12
 * Time: 16:58
 */
public interface OrderItemDAO extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrderOrderByIdDesc(Order order);

    List<OrderItem> findByProduct(Product product);
}
