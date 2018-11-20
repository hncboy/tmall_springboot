package com.hncboy.tmall.comparator;

import com.hncboy.tmall.pojo.Product;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/20
 * Time: 14:33
 *
 * 综合比较器
 * 把 销量*评价 高的放前面
 */
public class ProductAllComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p2.getReviewCount() * p2.getSaleCount() - p1.getReviewCount() * p1.getSaleCount();
    }
}
