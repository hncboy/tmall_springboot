package com.hncboy.tmall.web;

import com.hncboy.tmall.pojo.Property;
import com.hncboy.tmall.service.PropertyService;
import com.hncboy.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/7
 * Time: 16:13
 */
@RestController
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid,
                                         @RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) {
        start = start < 0 ? 0 : start;
        Page4Navigator<Property> page = propertyService.list(cid, start, size, 5);
        return page;
    }

    @GetMapping("/properties/{id}")
    public Property get(@PathVariable("id") int id) {
        return propertyService.get(id);
    }

    @PostMapping("/properties")
    public Object add(@RequestBody Property property) {
        propertyService.add(property);
        return property;
    }

    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) {
        propertyService.delete(id);
        return null;
    }

    @PutMapping("/properties")
    public Object update(@RequestBody Property property) {
        propertyService.update(property);
        return property;
    }
}
