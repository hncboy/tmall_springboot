package com.hncboy.tmall.comparator;

import com.hncboy.tmall.pojo.Product;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/20
 * Time: 14:39
 *
 * 新品比较器
 * 把 创建日期晚的放前面
 */
public class ProductDateComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p1.getCreateDate().compareTo(p2.getCreateDate());
    }
}
