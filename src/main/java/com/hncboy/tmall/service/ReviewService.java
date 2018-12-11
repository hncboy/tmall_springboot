package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.ReviewDAO;
import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/17
 * Time: 13:55
 */
@Service
@CacheConfig(cacheNames = "reviews")
public class ReviewService {

    @Autowired
    private ReviewDAO reviewDAO;

    @CacheEvict(allEntries = true)
    public void add(Review review) {
        reviewDAO.save(review);
    }

    @Cacheable(key = "'reviews-pid-'+ #p0.id")
    public List<Review> list(Product product) {
        List<Review> result = reviewDAO.findByProductOrderByIdDesc(product);
        return result;
    }

    @Cacheable(key = "'reviews-count-pid-'+ #p0.id")
    public int getCount(Product product) {
        return reviewDAO.countByProduct(product);
    }
}
