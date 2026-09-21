package com.trainingdepot.gear.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.trainingdepot.gear.model.Order;
import com.trainingdepot.gear.model.OrderItem;
import com.trainingdepot.gear.model.User;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(User user) {
        String body = "<h2>Welcome to the Training Depot, " + escape(user.getUsername()) + ".</h2>"
                + "<p>Your depot access has been created. Look for equipment, build your kit, and check out whenever you're ready.</p>";
        send(user.getEmail(), "Depot access granted", body);
    }

    public void sendOrderConfirmation(User user, Order order) {
        StringBuilder rows = new StringBuilder();
        for (OrderItem item : order.getOrderItems()) {
            rows.append("<tr><td style='padding:4px 10px;'>")
                    .append(escape(item.getProductName()))
                    .append("</td><td style='padding:4px 10px;text-align:center;'>")
                    .append(item.getQuantity())
                    .append("</td><td style='padding:4px 10px;text-align:right;'>&#8377;")
                    .append(item.getLineTotal())
                    .append("</td></tr>");
        }
        String body = "<h2>Order " + order.getOrderNumber() + " confirmed.</h2>"
                + "<p>Your equipment has been reserved from the depot floor and is being prepared.</p>"
                + "<table style='border-collapse:collapse;width:100%;max-width:480px;'>"
                + "<thead><tr><th style='text-align:left;padding:4px 10px;'>Item</th>"
                + "<th style='padding:4px 10px;'>Qty</th><th style='padding:4px 10px;'>Total</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table>"
                + "<p><strong>Order total: &#8377;" + order.getTotalAmount() + "</strong></p>";
        send(user.getEmail(), "Order " + order.getOrderNumber() + " confirmed", body);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (Exception e) {
            // Email is a courtesy, not a dependency: a misconfigured SMTP server
            // should never fail a registration or a paid order.
            log.warn("Could not send email '{}' to {}: {}", subject, to, e.getMessage());
        }
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
