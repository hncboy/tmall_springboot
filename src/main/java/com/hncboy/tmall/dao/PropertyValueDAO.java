package com.hncboy.tmall.dao;

import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.pojo.Property;
import com.hncboy.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/11
 * Time: 13:58
 */
public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {

    /**
     * 根据产品查询属性值
     *
     * @param product
     * @return
     */
    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    /**
     * 根据产品和属性获取属性值对象
     *
     * @param property
     * @param product
     * @return
     */
    PropertyValue getByPropertyAndProduct(Property property, Product product);
}
