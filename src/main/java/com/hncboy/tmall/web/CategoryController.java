package com.hncboy.tmall.web;

import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.service.CategoryService;
import com.hncboy.tmall.util.ImageUtil;
import com.hncboy.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/1
 * Time: 19:25
 */
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public Page4Navigator<Category> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        start = start < 0 ? 0 : start;
        //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        Page4Navigator<Category> page = categoryService.list(start, size, 5);
        return page;
    }

    @PostMapping("/categories")
    public Object add(Category category, MultipartFile image, HttpServletRequest request) throws Exception {
        categoryService.add(category);
        saveOrUpdateImageFile(category, image, request);
        return category;
    }

    private void saveOrUpdateImageFile(Category category, MultipartFile image, HttpServletRequest request) throws Exception {
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, category.getId() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) {
        categoryService.delete(id);
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        file.delete();
        return null;
    }

    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id) {
        Category category = categoryService.get(id);
        return category;
    }

    @PutMapping("/categories/{id}")
    public Object update(Category category, MultipartFile image, HttpServletRequest request) throws Exception {
        String name = request.getParameter("name"); //PUT方式注入不了对象
        category.setName(name);
        categoryService.update(category);

        if (image != null) {
            //更新图片
            saveOrUpdateImageFile(category, image, request);
        }
        return category;
    }
}
