package com.bioscope.backend.v01.sender;


import com.bioscope.backend.v01.models.EmailModel;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Component
@Slf4j
public class EmailSender {

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    private final TemplateEngine templateEngine;

    public EmailSender(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendEmail(EmailModel emailModel) throws IOException {
        Context context = new Context();
        context.setVariables(emailModel.getVariables());
        String content = templateEngine.process(emailModel.getTemplate(), context);
        Email from = new Email(fromEmail);
        Email to = new Email(emailModel.getTo());
        Content emailContent = new Content("text/html", content);
        Mail mail = new Mail(from, emailModel.getSubject(), to, emailContent);
        mail.setReplyTo(new Email(fromEmail));
        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.buildPretty());
        Response response = sendGrid.api(request);
        log.info("Email sent to {} with status code {}", emailModel.getTo(), response.getStatusCode());
    }
}
