package com.ecommerce.order.service;

import com.ecommerce.order.dto.CheckoutRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.Payment;
import com.ecommerce.order.entity.PaymentMethod;
import com.ecommerce.order.repository.PaymentMethodRepository;
import com.ecommerce.order.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public Payment processPayment(Order order, PaymentMethod paymentMethod, CheckoutRequest request) {
        Payment payment = new Payment(order, paymentMethod, order.getTotalAmount());
        
        // Generate transaction reference
        String transactionRef = generateTransactionRef(paymentMethod.getCode());
        payment.setTransactionRef(transactionRef);

        // Process based on payment method
        switch (paymentMethod.getCode()) {
            case PaymentMethod.UPI:
                return processUpiPayment(payment, request);
            case PaymentMethod.CARD:
                return processCardPayment(payment, request);
            case PaymentMethod.COD:
                return processCodPayment(payment);
            default:
                throw new RuntimeException("Unsupported payment method: " + paymentMethod.getCode());
        }
    }

    public List<PaymentMethod> getActivePaymentMethods() {
        return paymentMethodRepository.findByActiveTrue();
    }

    public PaymentMethod createPaymentMethod(String code, String displayName, String description) {
        if (paymentMethodRepository.existsByCode(code)) {
            throw new RuntimeException("Payment method with code already exists: " + code);
        }

        PaymentMethod paymentMethod = new PaymentMethod(code, displayName, true);
        paymentMethod.setDescription(description);
        return paymentMethodRepository.save(paymentMethod);
    }

    public PaymentMethod updatePaymentMethod(Long id, String displayName, String description, Boolean active) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment method not found"));

        if (displayName != null) {
            paymentMethod.setDisplayName(displayName);
        }
        if (description != null) {
            paymentMethod.setDescription(description);
        }
        if (active != null) {
            paymentMethod.setActive(active);
        }

        return paymentMethodRepository.save(paymentMethod);
    }

    public void deletePaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment method not found"));

        // Check if payment method is being used
        List<Payment> existingPayments = paymentRepository.findByPaymentMethodCode(paymentMethod.getCode());
        if (!existingPayments.isEmpty()) {
            // Deactivate instead of delete if there are existing payments
            paymentMethod.setActive(false);
            paymentMethodRepository.save(paymentMethod);
        } else {
            paymentMethodRepository.delete(paymentMethod);
        }
    }

    private Payment processUpiPayment(Payment payment, CheckoutRequest request) {
        // Simulate UPI payment processing
        try {
            // In real implementation, integrate with UPI gateway
            if (request.getUpiId() == null || request.getUpiId().trim().isEmpty()) {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("UPI ID is required");
                payment.setGatewayResponse("UPI ID validation failed");
            } else {
                // Simulate payment success (90% success rate)
                boolean isSuccess = Math.random() > 0.1;
                
                if (isSuccess) {
                    payment.setStatus(Payment.PaymentStatus.SUCCESS);
                    payment.setGatewayResponse("UPI payment successful via " + request.getUpiId());
                } else {
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setFailureReason("UPI payment declined");
                    payment.setGatewayResponse("Payment declined by UPI gateway");
                }
            }
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("UPI gateway error: " + e.getMessage());
            payment.setGatewayResponse("Gateway communication failed");
        }

        return paymentRepository.save(payment);
    }

    private Payment processCardPayment(Payment payment, CheckoutRequest request) {
        // Simulate card payment processing
        try {
            // Basic validation
            if (request.getCardNumber() == null || request.getCardNumber().length() < 16 ||
                request.getCardHolderName() == null || request.getCardHolderName().trim().isEmpty() ||
                request.getExpiryMonth() == null || request.getExpiryYear() == null ||
                request.getCvv() == null || request.getCvv().length() != 3) {
                
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("Invalid card details");
                payment.setGatewayResponse("Card validation failed");
            } else {
                // Simulate payment success (85% success rate)
                boolean isSuccess = Math.random() > 0.15;
                
                if (isSuccess) {
                    payment.setStatus(Payment.PaymentStatus.SUCCESS);
                    payment.setGatewayResponse("Card payment successful - " + 
                        maskCardNumber(request.getCardNumber()));
                } else {
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setFailureReason("Card payment declined");
                    payment.setGatewayResponse("Payment declined by card issuer");
                }
            }
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Card gateway error: " + e.getMessage());
            payment.setGatewayResponse("Gateway communication failed");
        }

        return paymentRepository.save(payment);
    }

    private Payment processCodPayment(Payment payment) {
        // COD payments are always pending until delivery
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setGatewayResponse("Cash on Delivery - Payment pending until delivery");
        
        return paymentRepository.save(payment);
    }

    private String generateTransactionRef(String paymentMethodCode) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return paymentMethodCode + "-" + timestamp + "-" + randomSuffix;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    // Admin methods for payment management
    public Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(status);
        if (reason != null) {
            payment.setGatewayResponse(reason);
        }

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public long getSuccessfulPaymentsCount() {
        return paymentRepository.countSuccessfulPayments();
    }
}
