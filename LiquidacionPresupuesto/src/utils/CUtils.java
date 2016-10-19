package utils;

import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class CUtils {

	public static boolean isEmpty(String cadena) {

		if (cadena == null)
			return true;

		if (cadena.trim() == "")
			return true;

		return false;
	}

	public static boolean emailValido(String correo) {
		Pattern ptr = Pattern.compile(
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
				Pattern.CASE_INSENSITIVE);

		if (isEmpty(correo.trim())) {
			return false;
		}

		if (!ptr.matcher(correo).matches()) {
			return false;
		}

		return true;
	}

	public static boolean send(String fromAddress, String toAddress, String ccAddress, String bccAddress,
			String subject, boolean isHTMLFormat, StringBuffer body) {

		if (emailValido(fromAddress) && emailValido(toAddress)) {

			boolean authLogin = Boolean.valueOf(CProperties.getProperty("auth_login"));
			String user = CProperties.getProperty("mail_smtp_user");
			String password = CProperties.getProperty("mail_smtp_password");

			String hostSmtp = CProperties.getProperty("mail_smtp_host");
			String portSmtp = CProperties.getProperty("mail_smtp_port");

			Properties properties = new Properties();
			properties.setProperty("mail.smtp.host", hostSmtp);
			properties.setProperty("mail.smtp.port", portSmtp);

			if (hostSmtp.equalsIgnoreCase("smtp.gmail.com")) {
				properties.setProperty("mail.smtp.starttls.enable",
						CProperties.getProperty("mail_smtp_starttls_enable"));
			}

			Session session = Session.getDefaultInstance(properties);

			session.setDebug(true);

			MimeMultipart multipart = new MimeMultipart();

			try {
				MimeMessage msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(fromAddress));
				msg.setRecipients(Message.RecipientType.TO, toAddress);

				if (emailValido(ccAddress))
					msg.setRecipients(Message.RecipientType.CC, ccAddress);

				if (emailValido(bccAddress))
					msg.setRecipients(Message.RecipientType.BCC, bccAddress);

				msg.setSubject(subject);
				msg.setSentDate(new Date());

				// BODY
				MimeBodyPart mbp = new MimeBodyPart();
				if (isHTMLFormat) {
					mbp.setContent(body.toString(), "text/html");
				} else {
					mbp.setText(body.toString());
				}

				multipart.addBodyPart(mbp);

				msg.setContent(multipart);

				if (authLogin) {
					Transport t = session.getTransport("smtp");
					t.connect(user, password);
					t.sendMessage(msg, msg.getAllRecipients());
				} else {
					Transport.send(msg);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

			return true;

		} else {
			return false;
		}
	}

}
