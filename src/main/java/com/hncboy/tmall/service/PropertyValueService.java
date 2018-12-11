package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.PropertyValueDAO;
import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.pojo.Property;
import com.hncboy.tmall.pojo.PropertyValue;
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
 * Date: 2018/11/11
 * Time: 14:06
 */
@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {

    @Autowired
    private PropertyValueDAO propertyValueDAO;

    @Autowired
    private PropertyService propertyService;

    public void init(Product product) {
        PropertyValueService propertyValueService = SpringContextUtil.getBean(PropertyValueService.class);
        List<Property> properties = propertyService.listByCategory(product.getCategory());
        for (Property property : properties) {
            PropertyValue propertyValue = propertyValueService.getByPropertyAndProduct(product, property);
            if (null == propertyValue) {
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    @Cacheable(key = "'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

    @Cacheable(key = "'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    @CacheEvict(allEntries = true)
    public void update(PropertyValue bean) {
        propertyValueDAO.save(bean);
    }
}
