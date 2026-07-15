package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Mock payment gateway")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(
            summary = "Process a payment for an order",
            description = "Simulates a real gateway: CARD has a 90% success rate, UPI 95%, COD always succeeds."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment processed (check the status field — SUCCESS or FAILED)"),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid payment method"),
            @ApiResponse(responseCode = "409", description = "A payment already exists for this order")
    })
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get the payment for an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No payment found for this order")
    })
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.findByOrderId(orderId));
    }
}
