package com.hncboy.tmall.service;

import com.hncboy.tmall.dao.PropertyValueDAO;
import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.pojo.Property;
import com.hncboy.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/11
 * Time: 14:06
 */
@Service
public class PropertyValueService {

    @Autowired
    private PropertyValueDAO propertyValueDAO;

    @Autowired
    private PropertyService propertyService;

    public void init(Product product) {
        List<Property> properties = propertyService.listByCategory(product.getCategory());
        for (Property property : properties) {
            PropertyValue propertyValue = getByPropertyAndProduct(product, property);
            if (null == propertyValue) {
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    public void update(PropertyValue bean) {
        propertyValueDAO.save(bean);
    }
}
