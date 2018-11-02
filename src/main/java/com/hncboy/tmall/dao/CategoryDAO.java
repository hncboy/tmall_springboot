package com.hncboy.tmall.dao;

import com.hncboy.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/1
 * Time: 19:20
 */
public interface CategoryDAO extends JpaRepository<Category, Integer> {
}
