package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.CategoryDAO;
import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/1
 * Time: 19:21
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;

    /**
     * 分页查询分裂
     *
     * @param start         起始页
     * @param size          每页的条数
     * @param navigatePages 总的显示页数的长度
     * @return
     */
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);
        return new Page4Navigator<Category>(pageFromJPA, navigatePages);
    }

    /**
     * 查询所有分类
     *
     * @return
     */
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    /**
     * 添加分类
     *
     * @param category
     */
    public void add(Category category) {
        categoryDAO.save(category);
    }

    /**
     * 删除分类
     *
     * @param id
     */
    public void delete(int id) {
        categoryDAO.delete(id);
    }

    /**
     * 根据id获取分类
     *
     * @param id
     * @return
     */
    public Category get(int id) {
        Category c = categoryDAO.findOne(id);
        return c;
    }

    /**
     * 修改分类
     *
     * @param category
     */
    public void update(Category category) {
        categoryDAO.save(category);
    }

    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null != products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p : ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
