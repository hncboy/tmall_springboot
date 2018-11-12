package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.PropertyDAO;
import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.pojo.Property;
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
 * Date: 2018/11/7
 * Time: 15:34
 */
@Service
public class PropertyService {

    @Autowired
    private PropertyDAO propertyDAO;

    @Autowired
    private CategoryService categoryService;

    public void add(Property property) {
        propertyDAO.save(property);
    }

    public void delete(int id) {
        propertyDAO.delete(id);
    }

    public Property get(int id) {
        return propertyDAO.findOne(id);
    }

    public void update(Property property) {
        propertyDAO.save(property);
    }

    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Property> pageFromJPA = propertyDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    public List<Property> listByCategory(Category category) {
        return propertyDAO.findByCategory(category);
    }
}
