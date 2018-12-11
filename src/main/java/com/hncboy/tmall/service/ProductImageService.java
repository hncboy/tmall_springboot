package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.ProductImageDAO;
import com.hncboy.tmall.pojo.OrderItem;
import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.pojo.ProductImage;
import com.hncboy.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/10
 * Time: 16:02
 */
@Service
@CacheConfig(cacheNames = "productImages")
public class ProductImageService {

    public static final String type_single = "single";
    public static final String type_detail = "detail";

    @Autowired
    private ProductImageDAO productImageDAO;

    @CacheEvict(allEntries = true)
    public void add(ProductImage bean) {
        productImageDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productImageDAO.delete(id);
    }

    @Cacheable(key = "'productImages-one-'+ #p0")
    public ProductImage get(int id) {
        return productImageDAO.findOne(id);
    }

    @Cacheable(key = "'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_single);
    }

    @Cacheable(key = "'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_detail);
    }

    public void setFirstProductImage(Product product) {
        ProductImageService productImageService = SpringContextUtil.getBean(ProductImageService.class);
        List<ProductImage> singleImages = productImageService.listSingleProductImages(product);
        if (!singleImages.isEmpty()) {
            product.setFirstProductImage(singleImages.get(0));
        } else {
            //这样做是考虑到产品还没有来得及设置图片，但是在订单后台管理里查看订单项的对应产品图片
            product.setFirstProductImage(new ProductImage());
        }
    }

    public void setFirstProductImages(List<Product> products) {
        for (Product product : products) {
            setFirstProductImage(product);
        }
    }

    public void setFirstProductImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProductImage(orderItem.getProduct());
        }
    }
}
