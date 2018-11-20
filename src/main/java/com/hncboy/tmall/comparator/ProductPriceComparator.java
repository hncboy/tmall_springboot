package com.hncboy.tmall.comparator;

import com.hncboy.tmall.pojo.Product;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/20
 * Time: 14:40
 */
public class ProductPriceComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return (int) (p1.getPromotePrice()-p2.getPromotePrice());
    }
}