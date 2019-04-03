package com.babymm.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.babymm.domain.Result;
import com.babymm.entity.User;
import com.babymm.jms.SendEmailProducer;
import com.babymm.service.EmailService;
import com.babymm.service.UserService;
import com.babymm.utils.EmailUtils;
import com.babymm.utils.PasswordUtils;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * 用户登陆
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(String username, String password) throws UnsupportedEncodingException {
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		try {
			// 执行认证操作.
			subject.login(token);
		} catch (AuthenticationException e) {
			return "redirect:../login.html?message=" + URLEncoder.encode("用户名或者密码错误");
		} catch (Exception e) {
			return "redirect:../login.html?message=" + URLEncoder.encode(e.getMessage());
		}
		// 将状态修改为1
		User user = ((User) subject.getPrincipal());
		userService.setStatus(1, user.getId());
		return "redirect:../index.html";
	}

	/**
	 * 创建用户，即注册用户
	 * 
	 * @param user
	 *            用户对象
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.PUT)
	@ResponseBody
	public Result registry(@RequestBody User user) {
		System.out.println(user);
		try {
			user.setPassword(PasswordUtils.encode(user.getPassword())); // 将密码加密
			userService.save(user);
			return new Result(true, "注册成功");
		} catch (Exception e) {
			e.printStackTrace();
			if ("用户名已存在".equals(e.getMessage())) {
				return new Result(false, "用户名已存在");
			}
			return new Result(false, "注册失败");
		}
	}

	/**
	 * 查询用户名是否可注册
	 * 
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/find/uname/{username}", method = RequestMethod.GET)
	@ResponseBody
	public Result findByUsername(@PathVariable String username) {
		try {
			boolean flag = userService.findUseridByUsername(username) == null ? false : true;
			if (flag) {
				return new Result(false, "该用户名已存在");
			} else {
				return new Result(true, "该用户名可以注册");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "未知错误");
		}
	}

	/**
	 * 获取登陆用户信息
	 * 
	 * @return 从session中取
	 */
	@RequestMapping("/getLogininfo") // 获取用户信息
	@ResponseBody
	public User getLoginNickname() {
		Subject subject = SecurityUtils.getSubject();
		if (subject == null) {
			return null;
		}
		User user = (User) subject.getPrincipal();
		User user_return = new User();
		user_return.setUsername(user.getUsername());
		user_return.setStatus(user.getStatus());
		user_return.setNickname(user.getNickname());
		user_return.setEmail(user.getEmail());
		user_return.setWechatId(user.getWechatId());
		return user_return;
	}

	/**
	 * 修改昵称
	 * 
	 * @param nickname
	 *            新昵称
	 * @return
	 */
	@RequestMapping(value = "/updateNickname", method = RequestMethod.POST)
	@ResponseBody
	public Result updateNickname(String nickname) {
		try {
			if ("".equals(nickname.trim())) { // 由于前端框架决定了，不存在传递过来null值的情况，只存在空字符串的可能
				return new Result(false, "kidding?修改失败，不提倡做无名侠");
			}
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			// 判断该nickname是否重名，如果重名提示不能修改
			if (nickname.trim().equals(user.getNickname())) {
				return new Result(false, "不改昵称就别来捣乱。。。");
			}
			// 查询数据库，是否存在重昵称的情况，如果存在，也提示修改失败。备注：本项目对此类unique字段，并没有在数据库设计时加以限制，
			// 而是出于逻辑关系来设定；基于如下考虑：1、unique字段，会消耗数据库资源（每次增加时都会进行校验）；2、如果插入同名，会直接抛出异常
			// 这样程序会追踪该异常整个流程，直到最顶层抛出，或者解决掉，这过程也需要占用jvm内存资源；
			// 当然，我这样只一次性单独开资源查询的效果和设计unique的关键字设计，仍需要后续测试才知道。
			Integer user_id = userService.findIdByNickname(nickname.trim());
			if (user_id != null) {
				return new Result(false, "该昵称已被其余用户占用，换个吧！");
			}
			user.setNickname(nickname.trim()); // 这里相当于将session中的也改了
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "未知错误，修改失败");
		}
	}

	@Autowired
	private EmailService emailService;

	/**
	 * 修改密码
	 * 
	 * @param code
	 *            验证码
	 * @param oldPassword
	 *            原密码
	 * @param newPassword
	 *            新密码
	 * @return
	 */
	@RequestMapping(value = "/updatepws", method = RequestMethod.POST)
	@ResponseBody
	public Result updatepws(String code, String oldPassword, String newPassword) {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		String encode_old_password = PasswordUtils.encode(oldPassword); // 加密原密码
		if (user.getPassword().equals(encode_old_password)) {
			try {
				String username = user.getUsername();
				String store_code = emailService.getCode(username + "_forgetCode");
				if (store_code == null) {
					return new Result(false, "验证码已过期，请重新获取");
				} else {
					if (store_code.equals(code)) {
						user.setPassword(PasswordUtils.encode(newPassword)); // 更新密码
						userService.update(user);
						emailService.del(username + "_forgetCode"); // 清除redis对应的缓存code(备注，也可以不清除，等半个小时后会自动失效)
						return new Result(true, "修改成功，下次登录时生效");
					} else {
						return new Result(false, "验证码错误");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "未知错误，修改失败");
			}
		} else {
			return new Result(false, "原密码错误");
		}
	}

	@Autowired
	private SendEmailProducer sendEmailProducer;
	

	/**
	 * 密码找回之一：发送验证码
	 * 
	 * @param username
	 *            用户名
	 * @param email
	 *            电子邮箱
	 * @return
	 */
	@RequestMapping("/findpwd")
	@ResponseBody
	public Result findpwd(String username, String email) {
		if (EmailUtils.isEmail(email)) {
			User user = userService.findUserByUsername(username);
			if (user == null) {
				return new Result(false, "该用户名不存在");
			} else {
				String email_fromDB = user.getEmail();
				if (email_fromDB == null) {
					return new Result(false, "该账号并未绑定邮箱，无法找回");
				} else {
					if (email_fromDB.equals(email)) {
						// 生成6位验证码
						String random_code = RandomStringUtils.randomNumeric(6);
						// 向redis中存入该验证码，以用户名为key，验证码为value，有效期为30分钟
						emailService.storeCode(username + "_findCode", random_code);
						// 调用消息生产者服务，发送该消息至邮件微服务器
						Map<String, String> message = new HashMap<String, String>();
						message.put("email", email);
						message.put("code", random_code);
						sendEmailProducer.sendCode(message);
						return new Result(true, "已向你的邮箱发送验证码");
					} else {
						return new Result(false, "电子邮箱不正确，请输入你绑定的电子邮箱");
					}
				}
			}
		} else {
			return new Result(false, "电子邮箱格式不正确");
		}
	}

	
	/**
	 * 密码找回之二：重置密码
	 * 
	 * @param code  验证码         
	 * @param password  新密码         
	 * @param username   用户名       
	 * @return
	 */
	@RequestMapping("/restpwd")
	@ResponseBody
	public Result restpwd(String code, String password, String username) {
		// 1、获取存储在redis中的验证码
		String code_store = emailService.getCode(username + "_findCode");
		//2、判断redis中是否有该用户名对应的验证码
		if (code_store == null) {		//没有
			return new Result(false, "验证码已失效，请重新验证！");
		} else {		//有
			//3、判断前端传过来的验证码和保存的是否一致
			if (code_store.equals(code)) {		//一致
				if(password.trim().length() >= 6) {		//简单判定密码强度
					User user = userService.findUserByUsername(username);
					if(user == null) {
						return new Result(false, "非法操作，该用户不存在！");
					}else {
						//重置密码
						user.setPassword(PasswordUtils.encode(password));
						userService.update(user);
						//移除redis中的缓存
						emailService.del(username + "_findCode");
						return new Result(true, "密码重置成功！");
					}				
				}else {		//密码简单
					return new Result(false, "密码设置过于简单，请确保密码至少6位");
				}
			} else {		//不一致
				return new Result(false, "验证码不正确");
			}
		}
	}
}
