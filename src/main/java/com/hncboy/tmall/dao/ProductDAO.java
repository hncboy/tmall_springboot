package com.hncboy.tmall.dao;

import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/8
 * Time: 12:26
 */
public interface ProductDAO extends JpaRepository<Product, Integer> {

    Page<Product> findByCategory(Category category, Pageable pageable);

    /**
     * 根据分类查询所有产品的方法
     *
     * @param category
     * @return
     */
    List<Product> findByCategoryOrderById(Category category);
}
