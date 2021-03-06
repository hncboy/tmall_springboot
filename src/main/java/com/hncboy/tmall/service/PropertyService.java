package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.PropertyDAO;
import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.pojo.Property;
import com.hncboy.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "properties")
public class PropertyService {

    @Autowired
    private PropertyDAO propertyDAO;

    @Autowired
    private CategoryService categoryService;

    @CacheEvict(allEntries = true)
    public void add(Property property) {
        propertyDAO.save(property);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        propertyDAO.delete(id);
    }

    @Cacheable(key = "'properties-one-'+ #p0")
    public Property get(int id) {
        return propertyDAO.findOne(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Property property) {
        propertyDAO.save(property);
    }

    @Cacheable(key = "'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
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
