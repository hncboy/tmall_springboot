package com.hncboy.tmall.web;

import com.hncboy.tmall.comparator.*;
import com.hncboy.tmall.pojo.*;
import com.hncboy.tmall.service.*;
import com.hncboy.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private OrderService orderService;

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

        //通过随机方式生成盐
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2; //加密2次
        String algorithmName = "md5"; //加密算法
        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        userService.add(user);

        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        //账号密码注入到 userParam 对象上，获取账号
        String name = userParam.getName();
        //把账号通过HtmlUtils.htmlEscape进行转义
        name = HtmlUtils.htmlEscape(name);

        //获取Subject对象
        Subject subject = SecurityUtils.getSubject();
        //封装用户的数据
        UsernamePasswordToken token = new UsernamePasswordToken(name, userParam.getPassword());

        try {
            //将用户的数据token 最终传递到Realm中进行对比
            subject.login(token);
            User user = userService.getByName(name);
            session.setAttribute("user", user);
            return Result.success();
        } catch (AuthenticationException e) {
            String message = "账号密码错误";
            return Result.fail(message);
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

    @GetMapping("forecheckLogin")
    public Object checkLogin(HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return Result.success();
        } else {
            return Result.fail("未登录");
        }
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

    @PostMapping("foresearch")
    public Object search(String keyword) {
        if (null == keyword) {
            keyword = "";
        }
        List<Product> ps = productService.search(keyword, 0, 20);
        productImageService.setFirstProductImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid, num, session);
    }

    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int oiid = 0;

        User user = (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId() == product.getId()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        //订单不存在
        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return oiid;
    }

    @GetMapping("forebuy")
    public Object buy(String[] oiid, HttpSession session) {
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        //通过字符串数组获取参数oiid
        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi = orderItemService.get(id);
            //累计这些ois的价格总数，赋值在total上
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            orderItems.add(oi);
        }

        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        //把订单项集合放在session的属性 "ois" 上
        session.setAttribute("ois", orderItems);

        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total); //把订单集合和total 放在map里
        return Result.success(map);
    }

    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid, num, session);
        return Result.success();
    }

    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        //通过session获取当前用户
        User user = (User) session.getAttribute("user");
        //获取为这个用户关联的订单项集合 ois
        List<OrderItem> ois = orderItemService.listByUser(user);
        //设置图片
        productImageService.setFirstProdutImagesOnOrderItems(ois);
        return ois;
    }

    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(HttpSession session, int pid, int num) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        }

        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId() == pid) {
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }

    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        }
        orderItemService.delete(oiid);
        return Result.success();
    }

    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        //修改订单对象的状态和支付时间
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        //更新这个订单对象到数据库
        orderService.update(order);
        return order;
    }

    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        }

        //根据当前时间加上一个4位随机数生成订单号
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay); //把订单状态设置为等待支付
        //从session中获取订单项集合
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        //把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
        float total = orderService.add(order, ois);

        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }

    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        }
        List<Order> os = orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }

    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.calc(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }

    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed(int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }

    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);

        // 获取第一个订单项对应的产品
        Product p = o.getOrderItems().get(0).getProduct();
        // 获取这个产品的评价集合
        List<Review> reviews = reviewService.list(p);
        //为产品设置评价数量和销量
        productService.setSaleAndReviewNumber(p);

        Map<String, Object> map = new HashMap<>();
        //把产品，订单和评价集合放在map上
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @PostMapping("foredoreview")
    public Object doreview(HttpSession session, int oid, int pid, String content) {
        //根据oid获取订单对象o
        Order o = orderService.get(oid);
        //修改订单对象状态
        o.setStatus(OrderService.finish);
        //更新订单对象到数据库
        orderService.update(o);

        Product p = productService.get(pid);
        //获取参数content (评价信息)进行转义
        content = HtmlUtils.htmlEscape(content);

        User user = (User) session.getAttribute("user");
        Review review = new Review();
        //为评价对象review设置 评价信息，产品，时间，用户
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);

        return Result.success();
    }
}
