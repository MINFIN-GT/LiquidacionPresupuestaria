package utils;

import java.util.Date;
import java.util.Map;
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

	public static final String MAIL_SUBJECT = "¡Conozca en qué fue invertido su pago del impuesto sobre circulación de vehículos 2016!";
	public static final String MAIL_BODY = "Estimado <b>#USUARIO#</b>,<br />" + "<br />"
			+ "Como parte de nuestra tarea de transparencia en el manejo de los impuestos de los contribuyentes.  "
			+ "Atentamente le informamos que su pago de impuesto sobre circulación de vehículos de <b>Q #MONTO#</b>, fue destinado hacia los siguientes programas:<br />"
			+ "<br />" + "#MUNI#" + "<br />" + "#FONDO#" + "<br />"
			+ "Agradecemos su aporte responsable con el país y le invitamos a seguir con el cumplimiento de sus deberes ciudadano.  "
			+ "Por nuestra parte, le reiteramos por velar porque los recursos de los guatemaltecos sean asignados a las necesidades más sentidas de la población.<br />"
			+ "<br />" + "También le invitamos a seguirnos en las redes sociales para conocer más,<br />" + "<br />"
			+ "<a href=\"https://es-la.facebook.com/minfingt\">" + "<img src=\"cid:imageFB\" "
			+ "alt=\"https://es-la.facebook.com/minfingt\"/>" + "</a>"
			+ "<a href=\"https://twitter.com/minfingt?lang=es\">" + "<img src=\"cid:imageTW\" "
			+ "alt=\"https://twitter.com/minfingt?lang=es\"/>" + "</a>" + "<br />" + "<br />" + "Atentamente,<br />"
			+ "<br />"
			+ "<div style=\"text-align:center;\">Ministerio de Finanzas Públicas y Superintendencia de Administración Tributaria"
			+ "<br />" + "<br />" + "<img src=\"cid:imageMF\" alt=\"MinFin\"/>"
			+ "<img src=\"cid:imageSAT\" alt=\"SAT\"/>" + "</div>";

	public static final String TEXT_MUNI = "<b>#RENGLON#</b><br />"
			+ "MUNICIPALIDAD DE #MUNICIPIO#, #DEPARTAMENTO#<br />" + "Aporte: <b>Q #MONTO#</b><br />";

	public static final String TEXT_FONDO = "#ENTIDAD#, #UNIDAD#<br />" + "#MUNICIPIO#, #DEPARTAMENTO#<br />"
			+ "<b>#RENGLON#</b><br />" + "Aporte: <b>Q #MONTO#</b><br />";

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
			String subject, boolean isHTMLFormat, StringBuffer body, Map<String, String> images) {

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

				if (images != null) {
					for (String key : images.keySet()) {
						MimeBodyPart imagePart = new MimeBodyPart();
						imagePart.attachFile(images.get(key));
						imagePart.setContentID("<" + key + ">");
						imagePart.setDisposition(MimeBodyPart.INLINE);
						multipart.addBodyPart(imagePart);
					}
				}

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
