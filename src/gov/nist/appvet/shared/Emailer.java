package gov.nist.appvet.shared;

import gov.nist.appvet.properties.AppVetProperties;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailer {

	private static final Logger log = AppVetProperties.log;
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;

/*	public static void main(String args[]) throws AddressException, MessagingException {

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

//	public static void generateAndSendEmail(String host, String port, 
//			String recipient, String sender, String senderName, String subject,
//			String content) {
		
	public static void generateAndSendEmail( 
			String recipient, String subject,String content) {
		
		String smtpHost = AppVetProperties.SMTP_HOST;
		String smtpPort = AppVetProperties.SMTP_PORT;
		boolean smtpAuth = AppVetProperties.SMTP_AUTH;
		boolean enableTls = AppVetProperties.ENABLE_TLS;
		String senderEmail = AppVetProperties.SENDER_EMAIL;
		String senderName = AppVetProperties.SENDER_NAME;
		String senderEmailPassword = AppVetProperties.SENDER_EMAIL_PASSWORD; 
		
		
		
		if (smtpHost == null) {
			log.error("SMTP host is null. Cannot send email notification");
			return;
		} else if (smtpPort == null) {
			log.error("SMTP port is null. Cannot send email notification");
			return;
		} else if (senderEmail == null) {
			log.error("Sender email address is null. Cannot send email notification");
			return;
		} else if (senderName == null) {
			log.error("Sender name is null. Using sender email address.");
			senderName = senderEmail;
		}
		
		if (smtpAuth) {
			// SMTP requires authentication password
			if (senderEmailPassword == null) {
				log.error("SMTP requires authentication but password is null");
				return;
			}
		}

		try {
			// Step1
			System.out.println("Setting Mail Server Properties...");
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.host", smtpHost); // e.g., smtp.gmail.com
			mailServerProperties.put("mail.smtp.port", smtpPort); // e.g., 587
			mailServerProperties.put("mail.from", senderEmail); // e.g., joe@test.com
			mailServerProperties.put("mail.user", senderEmail); // e.g., joe@test.com
			mailServerProperties.put("mail.smtp.auth", smtpAuth); // e.g., false
			mailServerProperties.put("mail.smtp.starttls.enable", enableTls); // e.g., true

			// Step2
			// TODO: Need to add Authenticator for the following
			getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			generateMailMessage = new MimeMessage(getMailSession);
			generateMailMessage.setFrom(new InternetAddress(senderEmail, senderName));
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			// generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@test.com"));
			generateMailMessage.setSubject(subject);
			generateMailMessage.setContent(content, "text/html");

			// Step3
			Transport transport = getMailSession.getTransport("smtp");
			// Use the following if no authentication is required
			transport.connect();
			// Use the following if SMTP authentication is required
			//transport.connect(host, senderEmail, "MyPassword1234"); 
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
			log.debug("\nEmail '" + subject + "' successfully sent to " + recipient);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showUsage() {
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
}