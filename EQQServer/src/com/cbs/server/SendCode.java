package com.cbs.server;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * 获取验证码码
 * @author CBS
 *
 */
public class SendCode {
	
	
	//LTAIZY4dOKHwWbka  keyID
	//gbhxwWOfy8orKQc0gQdi7lzq3BQO3e keysecret
	/**
	 * 通过手机号码获取验证码
	 * @param phoneNum
	 * @param code
	 * @return
	 */
	public static boolean sendByPhone(String phoneNum,String code){
		try{
			
			//初始化ascClient需要的几个参数
			final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
			final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
			//替换成你的AK
			final String accessKeyId = "LTAIZY4dOKHwWbka";//你的accessKeyId,参考本文档步骤2
			final String accessKeySecret = "gbhxwWOfy8orKQc0gQdi7lzq3BQO3e";//你的accessKeySecret，参考本文档步骤2
			//初始化ascClient,暂时不支持多region
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
			accessKeySecret);
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
			 //组装请求对象
			 SendSmsRequest request = new SendSmsRequest();
			 //使用post提交
			 request.setMethod(MethodType.POST);
			 //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
			 request.setPhoneNumbers(phoneNum);
			 //必填:短信签名-可在短信控制台中找到
			 request.setSignName("通天聊天");
			 //必填:短信模板-可在短信控制台中找到
			 request.setTemplateCode("SMS_100370037");
			 //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			 //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
			 request.setTemplateParam("{\"code\":\""+code+"\"}");
			//请求失败这里会抛ClientException异常
			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
			if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			//请求成功
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 用邮箱发送验证码
	 * @param email
	 * @param code
	 * @return
	 */
	public static boolean sendByEmail(String email,String code){
		//密码msxcb:cbs314159
		//授权码：shouquan314159
		try {
			HtmlEmail myEmail=new HtmlEmail();
			myEmail.setHostName("smtp.163.com");
			myEmail.setCharset("UTF-8");
			//收件人地址
			myEmail.addTo(email);
			//设置发件人
			myEmail.setFrom("miss_cbs@163.com","不死邮箱");
			//账户，密码
			myEmail.setAuthentication("miss_cbs@163.com", "shouquan314159");
			//标题
			myEmail.setSubject("通天聊天公司");
			//内容
			myEmail.setMsg("验证码是："+code);
			
			myEmail.send();
			return true;
		} catch (EmailException e) {
			e.printStackTrace();
			return false;
		}
		
	}
}
