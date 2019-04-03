package com.babymm.shiro;

import java.util.ResourceBundle;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.babymm.entity.User;
import com.babymm.service.UserService;

public class UserRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	/**
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
   
	     String username = (String) principalCollection.getPrimaryPrincipal();//获取登录的用户名   
	     System.out.println("前端传递的用户名是：" + username);
	     SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();     
	     if(username != null) {
	    	 User user = userService.findUserByUsername(username);		//从数据库中查询，如果有，就授权
	    	 if(user == null) {
	    		 return null;
	    	 }else {
	    		 info.addRole("user");
	    		 return info;
	    	 }
	     }
	        return null;      
	}


	/**
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		  //1. token 中获取登录的 username! 注意不需要获取password.
        String username = (String) token.getPrincipal();
                
        //2. 利用 username 查询数据库得到用户的信息.   
        User user=userService.findUserByUsername(username);
        String password = null;
        if(user!=null){
        	//数据库中对应的用户密码
           password=user.getPassword();
        }else {
        	return null;
        }
        String credentials = password;
        //3.设置盐值 ，（加密的调料，让加密出来的东西更具安全性，一般是通过数据库查询出来的。 简单的说，就是把密码根据特定的东西而进行动态加密，如果别人不知道你的盐值，就解不出你的密码）
        //从配置文件中获取盐
        String source = ResourceBundle.getBundle("config/shiro").getString("saltSource");
        ByteSource credentialsSalt = ByteSource.Util.bytes(source);
        
        
        //当前 Realm 的name
        String realmName = getName(); 
        //返回值实例化
        /**
         * 参数解释：
         * 第一个：存放在session中的对象，你想存哪个就放哪个
         * 第二个：从数据库找那个取出的密码
         * 第三个：加入的盐
         * 第四个：自定义的realm的名字，改方法可以重写自己随意更改
         */
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, credentials,credentialsSalt, realmName);
        
        return info;
    }


	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		super.setCredentialsMatcher(credentialsMatcher);
	}
	
	
}
