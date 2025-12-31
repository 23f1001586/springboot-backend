package com.zenbuy.service;

import com.zenbuy.model.*;
import com.zenbuy.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;


import java.util.ArrayList;
import java.util.List;

@Service
@Profile("dev")   
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Long userId, OrderRequest request) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setSubtotal(request.getSubtotal());
        order.setShipping(request.getShipping());
        order.setDiscount(request.getDiscount());
        order.setTotal(request.getTotal());
        order.setPaymentMethod(request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty() 
            ? request.getPaymentMethod() : "CARD");
        // Set payment status from request, default to COMPLETED
        order.setPaymentStatus(request.getPaymentStatus() != null && !request.getPaymentStatus().isEmpty() 
            ? request.getPaymentStatus() : "COMPLETED");
        order.setStatus("CONFIRMED");
        // Set transaction ID if provided (for PayU, etc.)
        if (request.getTransactionId() != null && !request.getTransactionId().isEmpty()) {
            order.setTransactionId(request.getTransactionId());
        }
        // OrderDate and OrderNumber will be set by @PrePersist
        // Validate and set shipping address (handle nulls to prevent database errors)
        if (request.getShippingAddress() == null) {
            throw new RuntimeException("Shipping address is required");
        }
        order.setShippingFlatNo(request.getShippingAddress().getFlatNo() != null 
            ? request.getShippingAddress().getFlatNo() : "");
        order.setShippingLocality(request.getShippingAddress().getLocality() != null 
            ? request.getShippingAddress().getLocality() : "");
        order.setShippingCity(request.getShippingAddress().getCity() != null 
            ? request.getShippingAddress().getCity() : "");
        order.setShippingPincode(request.getShippingAddress().getPincode() != null 
            ? request.getShippingAddress().getPincode() : "");

        // Create order items and update product stock
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

            // Check stock availability
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Update stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(itemRequest.getName());
            orderItem.setPrice(itemRequest.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setImageUrl(itemRequest.getImageUrl());

            items.add(orderItem);
        }

        order.setItems(items);
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // DTOs for request
    public static class OrderRequest {
        private List<OrderItemRequest> items;
        private double subtotal;
        private double shipping;
        private double discount;
        private double total;
        private String paymentMethod;
        private String paymentStatus; // COMPLETED, PENDING, FAILED
        private String transactionId; // For PayU and other payment gateways
        private ShippingAddress shippingAddress;

        public List<OrderItemRequest> getItems() {
            return items;
        }

        public void setItems(List<OrderItemRequest> items) {
            this.items = items;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }

        public double getShipping() {
            return shipping;
        }

        public void setShipping(double shipping) {
            this.shipping = shipping;
        }

        public double getDiscount() {
            return discount;
        }

        public void setDiscount(double discount) {
            this.discount = discount;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public ShippingAddress getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(ShippingAddress shippingAddress) {
            this.shippingAddress = shippingAddress;
        }
    }

    public static class OrderItemRequest {
        private Long productId;
        private String name;
        private double price;
        private int quantity;
        private String imageUrl;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static class ShippingAddress {
        private String flatNo;
        private String locality;
        private String city;
        private String pincode;

        public String getFlatNo() {
            return flatNo;
        }

        public void setFlatNo(String flatNo) {
            this.flatNo = flatNo;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }
    }
}

