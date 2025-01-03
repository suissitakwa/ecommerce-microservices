package com.example.ecommerce.notification;

import com.example.ecommerce.payment.PaymentMethod;
import org.apache.naming.factory.SendMailFactory;

import java.math.BigDecimal;

public record PaymentNotificationRequest(
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerFirstname,
        String customerLastname,
        String customerEmail
) {
}
