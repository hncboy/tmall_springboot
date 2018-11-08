package com.hncboy.tmall.dao;

import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/7
 * Time: 15:30
 */
public interface PropertyDAO extends JpaRepository<Property, Integer> {

    Page<Property> findByCategory(Category category, Pageable pageable);
}
