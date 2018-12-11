package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.ProductDAO;
import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.util.Page4Navigator;
import com.hncboy.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/8
 * Time: 12:28
 */
@Service
@CacheConfig(cacheNames = "products")
public class ProductService {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductImageService productImageService;

    @CacheEvict(allEntries = true)
    public void add(Product product) {
        productDAO.save(product);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productDAO.delete(id);
    }

    @Cacheable(key = "'products-one-'+ #p0")
    public Product get(int id) {
        return productDAO.findOne(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Product product) {
        productDAO.save(product);
    }

    @Cacheable(key = "'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> pageFromJPA = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    /**
     * 为多个分类填充产品集合
     *
     * @param categories
     */
    public void fill(List<Category> categories) {
        for (Category category : categories) {
            fill(category);
        }
    }

    /**
     * 为分类填充产品集合
     *
     * @param category
     */
    public void fill(Category category) {
        //间接获取productService，诱发AOP，实现listByCategory获取缓存
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProductImages(products);
        category.setProducts(products);
    }

    /**
     * 为多个分类填充推荐产品集合
     *
     * @param categories
     */
    public void fillByRow(List<Category> categories) {
        int productNumberEachRow = 8;
        for (Category category : categories) {
            List<Product> products = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    /**
     * 查询某个分类下的所有产品
     *
     * @param category
     * @return
     */
    @Cacheable(key = "'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    /**
     * 为产品设置销量和评价
     *
     * @param products
     */
    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products) {
            setSaleAndReviewNumber(product);
        }
    }

    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        int reviewCount = reviewService.getCount(product);
        product.setSaleCount(saleCount);
        product.setReviewCount(reviewCount);
    }

    public List<Product> search(String keyword, int start, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> products = productDAO.findByNameLike("%" + keyword + "%", pageable);
        return products;
    }
}
