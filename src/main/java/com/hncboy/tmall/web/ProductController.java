package com.hncboy.tmall.web;

import com.hncboy.tmall.pojo.Product;
import com.hncboy.tmall.service.CategoryService;
import com.hncboy.tmall.service.ProductImageService;
import com.hncboy.tmall.service.ProductService;
import com.hncboy.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/8
 * Time: 12:35
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @GetMapping("/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid,
                                        @RequestParam(value = "start", defaultValue = "0") int start,
                                        @RequestParam(value = "size", defaultValue = "5") int size) {
        start = start < 0 ? 0 : start;
        Page4Navigator<Product> page = productService.list(cid, start, size, 5);
        productImageService.setFirstProductImages(page.getContent());
        return page;
    }

    @GetMapping("/products/{id}")
    public Product get(@PathVariable("id") int id) {
        return productService.get(id);
    }

    @PostMapping("/products")
    public Object add(@RequestBody Product product) {
        product.setCreateDate(new Date());
        productService.add(product);
        return product;
    }

    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) {
        productService.delete(id);
        return null;
    }

    @PutMapping("/products")
    public Object update(@RequestBody Product product) {
        productService.update(product);
        return product;
    }
}
