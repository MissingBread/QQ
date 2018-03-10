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
 * ��ȡ��֤����
 * @author CBS
 *
 */
public class SendCode {
	
	
	//LTAIZY4dOKHwWbka  keyID
	//gbhxwWOfy8orKQc0gQdi7lzq3BQO3e keysecret
	/**
	 * ͨ���ֻ������ȡ��֤��
	 * @param phoneNum
	 * @param code
	 * @return
	 */
	public static boolean sendByPhone(String phoneNum,String code){
		try{
			
			//��ʼ��ascClient��Ҫ�ļ�������
			final String product = "Dysmsapi";//����API��Ʒ���ƣ����Ų�Ʒ���̶��������޸ģ�
			final String domain = "dysmsapi.aliyuncs.com";//����API��Ʒ�������ӿڵ�ַ�̶��������޸ģ�
			//�滻�����AK
			final String accessKeyId = "LTAIZY4dOKHwWbka";//���accessKeyId,�ο����ĵ�����2
			final String accessKeySecret = "gbhxwWOfy8orKQc0gQdi7lzq3BQO3e";//���accessKeySecret���ο����ĵ�����2
			//��ʼ��ascClient,��ʱ��֧�ֶ�region
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
			accessKeySecret);
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
			 //��װ�������
			 SendSmsRequest request = new SendSmsRequest();
			 //ʹ��post�ύ
			 request.setMethod(MethodType.POST);
			 //����:�������ֻ��š�֧���Զ��ŷָ�����ʽ�����������ã���������Ϊ1000���ֻ�����,������������ڵ������ü�ʱ�������ӳ�,��֤�����͵Ķ����Ƽ�ʹ�õ������õķ�ʽ
			 request.setPhoneNumbers(phoneNum);
			 //����:����ǩ��-���ڶ��ſ���̨���ҵ�
			 request.setSignName("ͨ������");
			 //����:����ģ��-���ڶ��ſ���̨���ҵ�
			 request.setTemplateCode("SMS_100370037");
			 //��ѡ:ģ���еı����滻JSON��,��ģ������Ϊ"�װ���${name},������֤��Ϊ${code}"ʱ,�˴���ֵΪ
			 //������ʾ:���JSON����Ҫ�����з�,����ձ�׼��JSONЭ��Ի��з���Ҫ��,������������а���\r\n�������JSON����Ҫ��ʾ��\\r\\n,����ᵼ��JSON�ڷ���˽���ʧ��
			 request.setTemplateParam("{\"code\":\""+code+"\"}");
			//����ʧ���������ClientException�쳣
			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
			if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			//����ɹ�
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * �����䷢����֤��
	 * @param email
	 * @param code
	 * @return
	 */
	public static boolean sendByEmail(String email,String code){
		//����msxcb:cbs314159
		//��Ȩ�룺shouquan314159
		try {
			HtmlEmail myEmail=new HtmlEmail();
			myEmail.setHostName("smtp.163.com");
			myEmail.setCharset("UTF-8");
			//�ռ��˵�ַ
			myEmail.addTo(email);
			//���÷�����
			myEmail.setFrom("miss_cbs@163.com","��������");
			//�˻�������
			myEmail.setAuthentication("miss_cbs@163.com", "shouquan314159");
			//����
			myEmail.setSubject("ͨ�����칫˾");
			//����
			myEmail.setMsg("��֤���ǣ�"+code);
			
			myEmail.send();
			return true;
		} catch (EmailException e) {
			e.printStackTrace();
			return false;
		}
		
	}
}
