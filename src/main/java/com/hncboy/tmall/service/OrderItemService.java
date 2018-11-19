package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.OrderItemDAO;
import com.hncboy.tmall.pojo.Order;
import com.hncboy.tmall.pojo.OrderItem;
import com.hncboy.tmall.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/12
 * Time: 17:00
 */
@Service
public class OrderItemService {

    @Autowired
    private OrderItemDAO orderItemDAO;

    @Autowired
    private ProductImageService productImageService;

    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }

    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    private void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi : orderItems) {
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);
    }

    public void add(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    public OrderItem get(int id) {
        return orderItemDAO.findOne(id);
    }

    public void delete(int id) {
        orderItemDAO.delete(id);
    }

    public int getSaleCount(Product product) {
        List<OrderItem> ois = listByProduct(product);
        int result = 0;
        for (OrderItem oi : ois) {
            if (null != oi.getOrder()) {
                if (null != oi.getOrder() && null != oi.getOrder().getPayDate()) {
                    result += oi.getNumber();
                }
            }
        }
        return result;
    }

    private List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

    private List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }
}
