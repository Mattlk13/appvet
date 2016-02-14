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

			try {
				// Step1: Set up mail properties
				mailServerProperties = System.getProperties();
				mailServerProperties.put("mail.smtp.host", smtpHost); // e.g., smtp.gmail.com
				mailServerProperties.put("mail.smtp.port", smtpPort); // e.g., 587
				mailServerProperties.put("mail.from", senderEmail); // e.g., joe@test.com
				mailServerProperties.put("mail.user", senderEmail); // e.g., joe@test.com
				mailServerProperties.put("mail.smtp.auth", smtpAuth); // e.g., false
				mailServerProperties.put("mail.smtp.starttls.enable", enableTls); // e.g., true

				// Step2: Set up mail message.
				// TODO: Need to add Authenticator for the following
				getMailSession = Session.getDefaultInstance(mailServerProperties, null);
				generateMailMessage = new MimeMessage(getMailSession);
				generateMailMessage.setFrom(new InternetAddress(senderEmail, senderName));
				generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
				
				// Add all admins as BCC'd recipients
				for (int i = 0; i < adminUsers.size(); i++) {
					UserInfo adminUser = adminUsers.get(i);
					String adminUserEmail = adminUser.getEmail();
					if (!adminUserEmail.equals(senderEmail)) {
						// Only add admin if not also the sender
						generateMailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(adminUserEmail));
					}
				}

				generateMailMessage.setSubject("[AppVet] " + subject);
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
				e.printStackTrace();
			}

		}

	}


/*	public static void showUsage() {
		System.out.println("Usage: java -jar emailer.jar [-h] host port recipient sender subject content\n");
		System.out.println("Where\n");
		System.out.println("host:\t The hostname or IP of the SMTP server (e.g., smtp.dhs.gov)\n");
		System.out.println("port:\t The port number of the SMTP server (e.g., 25)\n");
		System.out.println("to:\t The recipient's email address (e.g., recipient@test.com)\n");
		System.out.println("to:\t The sender's email address (e.g., sender@test.com)\n");
		System.out.println("to:\t The subject of the email (e.g., \"My subject\")\n");
		System.out.println("to:\t The content of the email (e.g., \"This is a message.\")\n");
		System.out.println("-h:\t This help message");
	}


	public static void main(String args[]) throws AddressException, MessagingException {

		if (args == null || args.length == 0 || args[0].equals("-h")) {
			showUsage();
			return;
		}

		if (args.length == 7) {
			log.debug("SMTP Host: " + args[0]);
			log.debug("SMTP Port: " + args[1]);
			log.debug("Recipient: " + args[2]);
			log.debug("Sender: " + args[3]);
			log.debug("Sender Name: " + args[4]);
			log.debug("Subject: " + args[5]);
			log.debug("Content: " + args[6]);

			//generateAndSendEmail(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);

		} else {
			showUsage();
			return;
		}
	}*/
}