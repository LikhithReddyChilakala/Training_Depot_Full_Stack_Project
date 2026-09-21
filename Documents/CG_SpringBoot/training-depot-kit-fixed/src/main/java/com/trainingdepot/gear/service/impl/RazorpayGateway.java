package com.trainingdepot.gear.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

/**
 * The single place that talks to the Razorpay SDK. Every amount that reaches
 * this class must already be the server-authoritative total in rupees - it
 * is converted to paise here and nowhere else.
 */
@Service
public class RazorpayGateway {

    private final String keyId;
    private final String keySecret;

    public RazorpayGateway(@Value("${razorpay.key.id}") String keyId,
            @Value("${razorpay.key.secret}") String keySecret) {
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    public String getKeyId() {
        return keyId;
    }

    public static class RazorpayOrderResult {
        private final String razorpayOrderId;
        private final long amountInPaise;

        public RazorpayOrderResult(String razorpayOrderId, long amountInPaise) {
            this.razorpayOrderId = razorpayOrderId;
            this.amountInPaise = amountInPaise;
        }

        public String getRazorpayOrderId() {
            return razorpayOrderId;
        }

        public long getAmountInPaise() {
            return amountInPaise;
        }
    }

    public RazorpayOrderResult createOrder(BigDecimal amountInRupees, String receipt) throws RazorpayException {
        long amountInPaise = amountInRupees
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        RazorpayClient client = new RazorpayClient(keyId, keySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);
        com.razorpay.Order razorpayOrder = client.orders.create(orderRequest);
        return new RazorpayOrderResult(razorpayOrder.get("id"), amountInPaise);
    }

    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            // com.razorpay.Utils#verifyPaymentSignature takes the attributes
            // JSONObject (order id, payment id, AND the signature itself, all
            // three as keys) plus the secret - two arguments total, not three.
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);
            return Utils.verifyPaymentSignature(attributes, keySecret);
        } catch (Exception e) {
            return false;
        }
    }

    /** Best-effort lookup of how the shopper paid (card/upi/netbanking); never lets a lookup failure break checkout. */
    public String fetchPaymentMethod(String razorpayPaymentId) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            com.razorpay.Payment payment = client.payments.fetch(razorpayPaymentId);
            return payment.get("method");
        } catch (Exception e) {
            return null;
        }
    }
}
