package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.OrderDAO;
import com.hncboy.tmall.pojo.Order;
import com.hncboy.tmall.pojo.OrderItem;
import com.hncboy.tmall.pojo.User;
import com.hncboy.tmall.util.Page4Navigator;
import com.hncboy.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/12
 * Time: 17:02
 */
@Service
@CacheConfig(cacheNames = "orders")
public class OrderService {

    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private OrderItemService orderItemService;

    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }

    /**
     * 将订单里的订单项的订单属性设置为空
     *
     * @param order
     */
    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }

    @Cacheable(key = "'orders-one-'+ #p0")
    public Order get(int oid) {
        return orderDAO.findOne(oid);
    }

    @CacheEvict(allEntries = true)
    public void update(Order bean) {
        orderDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public float add(Order order, List<OrderItem> ois) {
        float total = 0;
        add(order);

        for (OrderItem oi : ois) {
            oi.setOrder(order);
            orderItemService.update(oi);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
        }
        return total;
    }

    @CacheEvict(allEntries = true)
    public void add(Order order) {
        orderDAO.save(order);
    }

    public List<Order> listByUserWithoutDelete(User user) {
        OrderService orderService = SpringContextUtil.getBean(OrderService.class);
        List<Order> orders = orderService.listByUserAndNotDeleted(user);
        orderItemService.fill(orders);
        return orders;
    }

    public List<Order> listByUserAndNotDeleted(User user) {
        return orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
    }

    /**
     * 计算订单价格
     *
     * @param o
     */
    public void calc(Order o) {
        List<OrderItem> orderItems = o.getOrderItems();
        float total = 0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
        }
        o.setTotal(total);
    }
}
