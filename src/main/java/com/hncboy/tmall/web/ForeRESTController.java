package com.hncboy.tmall.web;

import com.hncboy.tmall.comparator.*;
import com.hncboy.tmall.pojo.*;
import com.hncboy.tmall.service.*;
import com.hncboy.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/13
 * Time: 13:42
 * <p>
 * 前台页面的路径
 */
@RestController
public class ForeRESTController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private PropertyValueService propertyValueService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/forehome")
    public Object home() {
        //查询所有分类
        List<Category> cs = categoryService.list();
        //为这些分类填充产品集合
        productService.fill(cs);
        //为这些分类填充推荐产品集合
        productService.fillByRow(cs);
        //移除产品里的分类信息，以免出现重复递归
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();

        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if (exist) {
            String message = "用户名已经被使用，不能使用";
            return Result.fail(message);
        }

        user.setPassword(password);
        userService.add(user);

        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        //账号密码注入到 userParam 对象上，获取账号
        String name = userParam.getName();
        //把账号通过HtmlUtils.htmlEscape进行转义
        name = HtmlUtils.htmlEscape(name);

        User user = userService.get(name, userParam.getPassword());
        if (null == user) {
            String message = "账号密码错误";
            return Result.fail(message);
        } else {
            session.setAttribute("user", user);
            return Result.success();
        }
    }

    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);

        //根据对象product，获取这个产品对应的单个图片集
        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        //根据对象product，获取这个产品对应的详情图片集合
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);

        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        //获取产品的所有属性值
        List<PropertyValue> pvs = propertyValueService.list(product);
        //获取产品对应的所有的评价
        List<Review> reviews = reviewService.list(product);

        //设置产品的销量和评价数量
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid, String sort) {
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);

        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;
                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;
                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;
                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }
        return c;
    }
}
