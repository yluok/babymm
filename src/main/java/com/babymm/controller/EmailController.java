package com.babymm.controller;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.babymm.domain.Result;
import com.babymm.entity.User;
import com.babymm.jms.SendEmailProducer;
import com.babymm.service.EmailService;
import com.babymm.utils.EmailUtils;

@Controller
@RequestMapping("/email")
public class EmailController {
	
	@Autowired
	SendEmailProducer sendEmailProducer;
	
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	private EmailService emailService;
	
	@RequestMapping(value="/{email:.+}",method=RequestMethod.GET)
	@ResponseBody
	public Result sendEmail(@PathVariable("email") String email) throws MalformedURLException {

		if(EmailUtils.isEmail(email)) {		//判断邮箱格式是否正确
			System.out.println("邮箱格式正确");
			//2、构建激活链接信息
			//获取当前日期，用于生成激活链接使用
			long date = new Date().getTime();
			//生成随机20位激活码
			String code = RandomStringUtils.randomAlphabetic(20);
			
			Map<String, String> map = new HashMap<String, String>();
			
		//	String localaddr = request.getLocalAddr();			//这个方法获取的只能是本机内网ip
			String ip = ResourceBundle.getBundle("config/ip").getString("ip");		//本机ip
			
			int port = request.getLocalPort();		//获取本项目端口
			String contextPath = request.getContextPath();		//获取项目contextPath
			
			String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
			//构建激活链接
			String active_url = createActiveUrl(ip,port,contextPath,username,date,code,email);
			
			map.put("email", email);
			
			map.put("active_url", active_url.toString());
			System.out.println("激活路径：" + active_url.toString());
			
			//3、向redis中存入激活信息
			try {
				emailService.storeEmail(username + "_active", active_url.toString());
				
				//4、发送激活链接邮件
				sendEmailProducer.sendActiveEmailUrl(map);
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "邮件服务暂时失效，请稍后再试");
			}
			
			return new Result(true, "邮件发送成功，请在一天之内完成激活！");
			
		}else {
			return new Result(false, "请检查邮箱格式是否正确");
		}
	}
	
	
	@RequestMapping(value="/active/{username}/{date}/{code}/{email:.+}",method=RequestMethod.GET)
	public String acitve(@PathVariable String username,@PathVariable long date,@PathVariable String code,@PathVariable String email) {
		String map = emailService.getEmail(username + "_active");			//从redis中取出的激活链接
		System.out.println("从redis中获取的url为" + map);	
		String ip = ResourceBundle.getBundle("config/ip").getString("ip");		//本机ip
		int port = request.getLocalPort();
		String contextPath = request.getContextPath();
		String url = "http://" + ip + ":" + port + contextPath;
		if(map == null) {				//redis存储的激活url过期
			return "redirect:" + url + "/invalidate.html";
		}else {
			
			String active_url = createActiveUrl(ip,port,contextPath,username,date,code,email);
			System.out.println("前端传过来的参数构建的url为：" + active_url);
			if(active_url.equals(map)) {		//前端构建的和redis存储的一致的话，就开始存
				//用户信息插入数据库中
				User user = new User();
				user.setEmail(email);
				user.setUsername(username);
				emailService.update(user);		//更新用户信息
				//删除对应的缓存内容
				emailService.del(username + "_active");
				return "redirect:"+ url + "/validate.html";		//激活成功
			}else {
				return "redirect:"+ url + "/validateError.html";		//激活链接有问题，失败
			}
			
		}
	}
	
	/**
	 * 构建激活链接
	 * @param localaddr  IP地址
	 * @param port  端口号
	 * @param contextPath   项目虚拟路径
	 * @param username   用户名
	 * @param date  当前时间
	 * @param code  随机激活码
	 * @param email  邮箱地址
	 * @return
	 */
	private String createActiveUrl(String localaddr,int port,String contextPath,String username,long date,String code,String email) {
		StringBuffer active_url = new StringBuffer("http://");
		active_url.append(localaddr)
				  .append(":")
				  .append(port)
				  .append(contextPath)
				  .append("/email/active/")
				  .append(username)
				  .append("/")
				  .append(date)
				  .append("/")
				  .append(code)
				  .append("/")
				  .append(email);
		return active_url.toString();
		
	}
	
	/**
	 * 发送验证码（用于密码修改的）
	 * @param email
	 * @return
	 */
	@RequestMapping("/sendCode")
	@ResponseBody
	public Result sendCode(String email) {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		if(EmailUtils.isEmail(email)) {
			if(!user.getEmail().equals(email)) {
				return new Result(false, "非法操作，请确保发送验证码的电子邮箱和绑定的一致");
			}
			try {
				String random_num = RandomStringUtils.randomNumeric(6);		//随机生成6位数字
				emailService.storeCode(user.getUsername()+"_forgetCode", random_num);		//向redis中存储验证码
				Map<String,String> map = new HashMap<String, String>();		//创建一个map封装
				map.put("email", email);		//需要发送的邮箱地址
				map.put("code", random_num);			//验证码
				sendEmailProducer.sendCode(map);		//发送给MQ的生存者
				return new Result(true, "验证码已发送至你的邮箱中！");
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "未知错误，请联系客服人员！");
				
			}
		}else {
			return new Result(false, "请检查电子邮箱格式是否正确！");
		}
	}
	
}
