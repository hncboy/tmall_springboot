package com.hncboy.tmall.web;

import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.pojo.User;
import com.hncboy.tmall.service.CategoryService;
import com.hncboy.tmall.service.ProductService;
import com.hncboy.tmall.service.UserService;
import com.hncboy.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

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
}
