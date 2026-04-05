package com.example.demo;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    /** Creates a Razorpay order and returns its ID */
    public String createOrder(double amountInRupees) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);
        JSONObject options = new JSONObject();
        options.put("amount", (int)(amountInRupees * 100)); // paise
        options.put("currency", "INR");
        options.put("receipt", "rcpt_" + System.currentTimeMillis());
        com.razorpay.Order order = client.orders.create(options);
        return order.get("id");
    }

    /** Verifies Razorpay payment signature */
    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String signature) {
        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generated = HexFormat.of().formatHex(hash);
            return generated.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    public String getKeyId() { return keyId; }
}
