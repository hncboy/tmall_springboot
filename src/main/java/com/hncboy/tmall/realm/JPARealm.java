package com.hncboy.tmall.realm;

import com.hncboy.tmall.pojo.User;
import com.hncboy.tmall.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/12/7
 * Time: 15:25
 */
public class JPARealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        return s;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取账号密码
        String username = token.getPrincipal().toString();
        User user = userService.getByName(username);
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();

        //认证信息里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名 :JPARealm
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(username, passwordInDB,
                ByteSource.Util.bytes(salt), getName());
        return authenticationInfo;
    }
}
