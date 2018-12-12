package com.hncboy.tmall.es;

import com.hncboy.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/12/11
 * Time: 13:22
 */
public interface ProductESDAO extends ElasticsearchRepository<Product, Integer> {
}
