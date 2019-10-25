package com.mycompany;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Properties;

import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import java.util.logging.*;


public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
	String loginStr, passwordStr, addressStr, messageStr, topicStr;
	Label sendStatus;

	public HomePage(final PageParameters parameters) {
		super(parameters);
		StatelessForm form = new StatelessForm("form");
		TextArea emailAddress = new TextArea("emailAddress", Model.of("emailAddress"));
		TextArea login = new TextArea("login", Model.of("login"));
		TextArea password = new TextArea("password", Model.of("password"));
		TextArea topic = new TextArea("topic", Model.of("topic"));
		TextArea<String> message = new TextArea<String>("message", Model.of("")) {
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("cols", "60");
				tag.put("rows", "30");
			}

			@Override
			public FormComponent<String> setModelObject(String object) {
				object = "";
				return null;
			}
		};

		Button button = new Button("button", Model.of("Send")) {
			@Override
			public void onSubmit() {
				try {


					loginStr = login.getValue();
					passwordStr = password.getValue();
					messageStr = message.getValue();
					addressStr = emailAddress.getValue();
					topicStr = topic.getValue();
					Sender sender = new Sender(loginStr, passwordStr);
					sender.send(topicStr, messageStr, loginStr, addressStr);
					form.clearInput();
					sendStatus.setDefaultModelObject("Success - mail was sent at " + java.time.LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
				}
				catch (Exception e)
				{
					sendStatus.setDefaultModelObject(e.getMessage());

				}
			}
		};


		this.setStatelessHint(true);
		form.add(new Label("addressLabel", "Enter getter's email"));
		form.add(emailAddress);
		form.add(new Label("loginLabel", "Enter your login"));
		form.add(login);
		form.add(new Label("passwordLabel", "Enter your password"));
		form.add(password);
		form.add(new Label("topicLabel", "Enter your message header"));
		form.add(topic);
		form.add(new Label("messageLabel", "Enter your message below"));
		form.add(message);
		sendStatus = new Label("sendStatusLabel", Model.of(""));
		form.add(sendStatus);
		form.add(button);
		this.add(form);




	}

}

class Sender {

	private String username;
	private String password;
	private Properties props;

	public Sender(String username, String password) {
		this.username = username;
		this.password = password;

		props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.socketFactory.port", "587");
	//	props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "587");
	}

	public void send(String subject, String text, String fromEmail, String toEmail){
		Session session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			//от кого
			message.setFrom(new InternetAddress(username));
			//кому
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			//тема сообщения
			message.setSubject(subject);
			//текст
			message.setText(text);

			//отправляем сообщение
			Transport.send(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}