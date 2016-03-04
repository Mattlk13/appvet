package gov.nist.appvet.servlet.shared;

import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.backend.AppVetProperties;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.Logger;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Emailer {

	private static final Logger log = AppVetProperties.log;
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	static List<UserInfo> adminUsers = null;

	static {
		adminUsers = Database.getUsers(Role.ADMIN);
	}


	public static void sendEmail(String recipient, String subject, 
			String content) {

		if (AppVetProperties.emailEnabled) {
			String smtpHost = AppVetProperties.SMTP_HOST;
			String smtpPort = AppVetProperties.SMTP_PORT;
			boolean smtpAuth = AppVetProperties.SMTP_AUTH;
			boolean enableTls = AppVetProperties.ENABLE_TLS;
			String senderEmail = AppVetProperties.SENDER_EMAIL;
			String senderName = AppVetProperties.SENDER_NAME;
			String senderEmailPassword = AppVetProperties.SENDER_EMAIL_PASSWORD; 
			String connectionTimeout = AppVetProperties.EMAIL_CONNECTION_TIMEOUT;

			try {
				// Step1: Set up mail properties
				mailServerProperties = System.getProperties();
				mailServerProperties.put("mail.smtp.host", smtpHost);  
				mailServerProperties.put("mail.smtp.port", smtpPort);  
				mailServerProperties.put("mail.from", senderEmail);  
				mailServerProperties.put("mail.user", senderEmail);  
				mailServerProperties.put("mail.smtp.auth", smtpAuth);  
				mailServerProperties.put("mail.smtp.starttls.enable", enableTls);  
				mailServerProperties.put("mail.smtp.connectiontimeout", connectionTimeout);

				// Step2: Set up mail message.
				// TODO: Need to add Authenticator for the following
				getMailSession = Session.getDefaultInstance(
						mailServerProperties, null);
				generateMailMessage = new MimeMessage(getMailSession);
				generateMailMessage.setFrom(new InternetAddress(senderEmail, senderName));
				generateMailMessage.addRecipient(Message.RecipientType.TO, 
						new InternetAddress(recipient));

				// Add all admins as BCC'd recipients
				for (int i = 0; i < adminUsers.size(); i++) {
					UserInfo adminUser = adminUsers.get(i);
					String adminUserEmail = adminUser.getEmail();
					if (!adminUserEmail.equals(senderEmail)) {
						// Only add admin if not also the sender
						generateMailMessage.addRecipient(
								Message.RecipientType.BCC, 
								new InternetAddress(adminUserEmail));
					}
				}

				generateMailMessage.setSubject(subject);
				generateMailMessage.setContent(content, "text/html");

				// Step3: Set up transport and send.
				Transport transport = getMailSession.getTransport("smtp");

				if (!smtpAuth) {
					transport.connect();
				} else {
					transport.connect(smtpHost, senderEmail, senderEmailPassword); 
				}

				transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
				transport.close();
				log.debug("\nEmail '" + subject + "' successfully sent to " + recipient);
			} catch (Exception e) {
				log.error("Error sending email.\n"
						+ e.getMessage());
			}
		} else {
			log.warn("Email is disabled. Cannot send message '" + subject + "'.");
		}
	}


	public static boolean testConnection() {

		String smtpHost = AppVetProperties.SMTP_HOST;
		String smtpPort = AppVetProperties.SMTP_PORT;
		boolean smtpAuth = AppVetProperties.SMTP_AUTH;
		boolean enableTls = AppVetProperties.ENABLE_TLS;
		String senderEmail = AppVetProperties.SENDER_EMAIL;
		String senderName = AppVetProperties.SENDER_NAME;
		String senderEmailPassword = AppVetProperties.SENDER_EMAIL_PASSWORD; 
		String connectionTimeout = AppVetProperties.EMAIL_CONNECTION_TIMEOUT;
		long startTime = System.currentTimeMillis();
		
		try {
			// Step1: Set up mail properties
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.host", smtpHost);  
			mailServerProperties.put("mail.smtp.port", smtpPort);  
			mailServerProperties.put("mail.from", senderEmail); 
			mailServerProperties.put("mail.user", senderEmail); 
			mailServerProperties.put("mail.smtp.auth", smtpAuth);  
			mailServerProperties.put("mail.smtp.starttls.enable", enableTls);  
			mailServerProperties.put("mail.smtp.connectiontimeout", connectionTimeout);

			// Step2: Set up mail message.
			// TODO: Need to add Authenticator for the following
			getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			generateMailMessage = new MimeMessage(getMailSession);
			generateMailMessage.setFrom(new InternetAddress(senderEmail, senderName));
			// Add all admins as BCC'd recipients
			for (int i = 0; i < adminUsers.size(); i++) {
				UserInfo adminUser = adminUsers.get(i);
				String adminUserEmail = adminUser.getEmail();
				if (!adminUserEmail.equals(senderEmail)) {
					// Only add admin if not also the sender
					generateMailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(adminUserEmail));
				}
			}

			// Step3: Set up transport and test connection
			Transport transport = getMailSession.getTransport("smtp");

			if (!smtpAuth) {
				transport.connect();
			} else {
				transport.connect(smtpHost, senderEmail, senderEmailPassword); 
			}

			transport.close();
			log.debug("\nEmail connection established - Email enabled.");
			return true;
		} catch (Exception e) {
			log.warn("Email connection could not be established in " + (System.currentTimeMillis() - startTime) + "ms. Disabling email.\n"
					+ e.getMessage());
			return false;
		}
	}
}