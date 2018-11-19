package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.ReviewDAO;
import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/17
 * Time: 13:55
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewDAO reviewDAO;

    public void add(Review review) {
        reviewDAO.save(review);
    }

    public List<Review> list(Product product) {
        List<Review> result = reviewDAO.findByProductOrderByIdDesc(product);
        return result;
    }

    public int getCount(Product product) {
        return reviewDAO.countByProduct(product);
    }
}
