import java.util.*;

import java.util.Properties;
import java.util.Date;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.*;
import java.text.*;
import javax.mail.internet.*;
import java.security.*;
import java.io.*;

public class Main
{
	public static void main(String[] args)
	{
		
		try {
			
			/*
			Properties prop = new Properties();
			         prop.setProperty("mail.host", "smtp.qq.com");
			         prop.setProperty("mail.transport.protocol", "smtp");
			         prop.setProperty("mail.smtp.auth", "true");
			         //使用JavaMail发送邮件的5个步骤
			         //1、创建session
			         Session session = Session.getInstance(prop);
			         //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
			         session.setDebug(true);
			         //2、通过session得到transport对象
			         Transport ts = session.getTransport();
			         //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
			         ts.connect("smtp.qq.com", "1559215761", "bfglxzcmrpkmibdh");
			         //4、创建邮件
			         Message message = createSimpleMail(session);
			         //5、发送邮件
			         ts.sendMessage(message, message.getAllRecipients());
			         ts.close();
			*/
		
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		
		
		
		//设置SSL连接、邮件环境
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", "smtp.qq.com");
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", "995");
        props.setProperty("mail.pop3.socketFactory.port", "995");
        props.setProperty("mail.pop3.auth", "true");
//        props.put("mail.pop3.host", "smtp.qq.com");
//        props.put("mail.pop3.socketFactory.class", SSL_FACTORY);
//        props.put("mail.pop3.socketFactory.fallback", "false");
//        props.put("mail.pop3.port", "995");
//        props.put("mail.pop3.socketFactory.port", "995");
//        props.put("mail.pop3.auth", "true");
        //建立邮件会话
        Session session = Session.getDefaultInstance(props, null);
		//session.setDebug(true);
        //设置连接邮件仓库的环境
        URLName url = new URLName("pop3", "pop.qq.com", 995, null, "1559215761", "bfglxzcmrpkmibdh");
        Store store = null;
        Folder inbox = null;
        try {
            //得到邮件仓库并连接
            store = session.getStore(url);
            store.connect();
            //得到收件箱并抓取邮件
            inbox = store.getFolder("INBOX");  
            inbox.open(Folder.READ_ONLY);
            FetchProfile profile = new FetchProfile();
           // profile.add(FetchProfile.Item.ENVELOPE);
            Message[] messages = inbox.getMessages();
            inbox.fetch(messages, profile);
            //打印收件箱邮件部分信息
            int length = messages.length;
            System.out.println("收件箱的邮件数：" + length);
            System.out.println("-------------------------------------------\n");
            for (int i = 0; i < length; i++) {
                String from = MimeUtility.decodeText(messages[i].getFrom()[0].toString());
                InternetAddress ia = new InternetAddress(from);
                System.out.println("发件人：" + ia.getPersonal() + '(' + ia.getAddress() + ')');
                System.out.println("主题：" + messages[i].getSubject());
				
				//System.out.println("内容:"+messages[i].getContent().toString());
                System.out.println("邮件大小：" + messages[i].getSize());
                System.out.println("邮件发送时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(messages[i].getSentDate()));
                System.out.println("-------------------------------------------\n");
           
				
				
				
				Multipart multipart = (Multipart) messages[i].getContent();
				int count = multipart.getCount();    // 部件个数
				for(int ii=0; ii<count; ii++) {
					// 单个部件     注意：单个部件有可能又为一个Multipart，层层嵌套
					BodyPart part = multipart.getBodyPart(ii);
					// 单个部件类型
					String type = part.getContentType().split(";")[0];
					/**
					 * 类型众多，逐一判断，其中TEXT、HTML类型可以直接用字符串接收，其余接收为内存地址
					 * 可能不全，如有没判断住的，请自己打印查看类型，在新增判断
					 */
					if(type.equals("multipart/alternative")) {        // HTML （文本和超文本组合）
						System.out.println("超文本:" + part.getContent().toString());
					}else if(type.equals("text/plain")) {    // 纯文本
						System.out.println("纯文本:" + part.getContent().toString());
					}else if(type.equals("text/html")){    // HTML标签元素
						System.out.println("HTML元素:" + part.getContent().toString());
					}else if(type.equals("multipart/related")){    // 内嵌资源 (包涵文本和超文本组合)
						System.out.println("内嵌资源:" + part.getContent().toString());
					}else if(type.contains("application/")) {        // 应用附件 （zip、xls、docx等）
						System.out.println("应用文件：" + part.getContent().toString());
					}else if(type.contains("image/")) {            // 图片附件 （jpg、gpeg、gif等）
						System.out.println("图片文件：" + part.getContent().toString());
					}

					/*****************************************获取邮件内容方法***************************************************/
					/**
					 * 附件下载
					 * 这里针对image图片类型附件做下载操作，其他类型附件同理
					 */
					if(type.contains("image/")) {
						// 打开附件的输入流
						DataInputStream in = new DataInputStream(part.getInputStream());
						// 一个文件输出流
						FileOutputStream out = null;
						// 获取附件名
						String fileName = part.getFileName();
						// 文件名解码
						fileName = MimeUtility.decodeText(fileName);
						// 根据附件名创建一个File文件
						File file = new File("/sdcard/0/" + fileName);
						// 查看是否有当前文件
						Boolean b = file.exists();
						if(!b) {
							out = new FileOutputStream(file);
							int data;
							// 循环读写
							while((data=in.read()) != -1) {
								out.write(data);
							}
							System.out.println("附件：【" + fileName + "】下载完毕，保存路径为：" + file.getPath());
						}

						// 关流
						if(in != null) {
							in.close();
						}
						if(out != null) {
							out.close();
						}
					}

					/**
					 * 获取超文本复合内容
					 * 他本是又是一个Multipart容器
					 * 此时邮件会分为TEXT（纯文本）正文和HTML正文（HTML标签元素）
					 */
					if(type.equals("multipart/alternative")) {
						Multipart m = (Multipart) part.getContent();
						for (int k=0; k<m.getCount(); k++) {
							if(m.getBodyPart(k).getContentType().startsWith("text/plain")) {
								// 处理文本正文
								System.out.println("TEXT文本内容："+"\n" + m.getBodyPart(k).getContent().toString().trim()+"\n");
							} else {
								// 处理 HTML 正文
								System.out.println("HTML文本内容："+"\n" + m.getBodyPart(k).getContent()+"\n");
							}
						}
					}

				}

				/**
				 * 最后千万别忘记了关闭
				 */
				
				//store.close();
				
				
				}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inbox.close(false);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            try {
                store.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
		
		
	
	
	
	public static MimeMessage createSimpleMail(Session session)
	throws Exception {
		//创建邮件对象
		MimeMessage message = new MimeMessage(session);
		//指明邮件的发件人
		message.setFrom(new InternetAddress("1559215761@qq.com"));
		//指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
		message.setRecipient(Message.RecipientType.TO, new InternetAddress("1559215761@qq.com"));
		//邮件的标题
		message.setSubject("只包含文本的简单邮件");
		//邮件的文本内容
		message.setContent("你好啊！", "text/html;charset=UTF-8");
		//返回创建好的邮件对象
		return message;
	}
	
	
		
		/**
		 * 解析综合数据
		 * @param part
		 * @throws Exception
		 */
		
		
	
		 
		 
}
