package com.hncboy.tmall.interceptor;

import com.hncboy.tmall.pojo.Category;
import com.hncboy.tmall.pojo.OrderItem;
import com.hncboy.tmall.pojo.User;
import com.hncboy.tmall.service.CategoryService;
import com.hncboy.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/22
 * Time: 12:31
 */
public class OtherInterceptor implements HandlerInterceptor {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("user");
        int cartTotalItemNumber = 0;

        if (null != user) {
            List<OrderItem> ois = orderItemService.listByUser(user);
            for (OrderItem oi : ois) {
                cartTotalItemNumber += oi.getNumber();
            }
        }

        List<Category> cs = categoryService.list();
        String contextPath = httpServletRequest.getServletContext().getContextPath();
        httpServletRequest.getServletContext().setAttribute("categories_below_search", cs);

        //将cartTotalItemNumber放入session
        session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
        httpServletRequest.getServletContext().setAttribute("contextPath", contextPath);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
