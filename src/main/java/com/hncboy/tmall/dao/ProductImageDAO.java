package com.hncboy.tmall.dao;

import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/10
 * Time: 10:32
 */
public interface ProductImageDAO extends JpaRepository<ProductImage, Integer> {

    public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);
}
