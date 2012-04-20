package com.iitb.MapperBot;

import javax.activation.DataHandler;   
import javax.activation.DataSource;   
import javax.activation.FileDataSource;
import javax.mail.Message;   
import javax.mail.PasswordAuthentication;   
import javax.mail.Session;   
import javax.mail.Transport;   
import javax.mail.internet.InternetAddress;   
import javax.mail.internet.MimeMessage;   
import java.io.ByteArrayInputStream;   
import java.io.IOException;   
import java.io.InputStream;   
import java.io.OutputStream;   
import java.security.Security;   
import java.util.Properties;   

public class GMailSender extends javax.mail.Authenticator {   
    private String mailhost = "auth-iitb.ac.in";   
    private String user = "your-ldap-id";   
    private String password = "your-ldap-passwd";   
    private Session session;   

    static {   
        Security.addProvider(new JSSEProvider());   
    }  

    public GMailSender(String user, String password) {   
        //this.user = user;   
        //this.password = password;   

        Properties props = new Properties();   
        props.setProperty("mail.transport.protocol", "smtp");   
        props.setProperty("mail.host", mailhost);   
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", "25");   
        props.put("mail.smtp.socketFactory.port", "465");   
        props.put("mail.smtp.socketFactory.class",   
                "javax.net.ssl.SSLSocketFactory");   
        props.put("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.quitwait", "false");   

        session = Session.getDefaultInstance(props, this);   
    }   

    protected PasswordAuthentication getPasswordAuthentication() {   
        return new PasswordAuthentication(user, password);   
    }   

    public synchronized void sendMail(String subject, String body, String sender, String recipients, String fileAttachment) throws Exception {   
    	try{
    		MimeMessage message = new MimeMessage(session);   
    		
    		message.setSender(new InternetAddress(sender));   
    		message.setSubject(subject);
    		if (!fileAttachment.isEmpty()){
    			DataSource source = new FileDataSource(fileAttachment);
        		message.setDataHandler(new DataHandler(source));
    		}
    		else {
    			DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
    			message.setDataHandler(handler);
    		}
    		if (recipients.indexOf(',') > 0)   
    			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));   
    		else  
    			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));   
    		Transport.send(message);   
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }   

    public class ByteArrayDataSource implements DataSource {   
        private byte[] data;   
        private String type;   

        public ByteArrayDataSource(byte[] data, String type) {   
            super();   
            this.data = data;   
            this.type = type;   
        }   

        public ByteArrayDataSource(byte[] data) {   
            super();   
            this.data = data;   
        }   

        public void setType(String type) {   
            this.type = type;   
        }   

        public String getContentType() {   
            if (type == null)   
                return "application/octet-stream";   
            else  
                return type;   
        }   

        public InputStream getInputStream() throws IOException {   
            return new ByteArrayInputStream(data);   
        }   

        public String getName() {   
            return "ByteArrayDataSource";   
        }   

        public OutputStream getOutputStream() throws IOException {   
            throw new IOException("Not Supported");   
        }   
    }   
}  
