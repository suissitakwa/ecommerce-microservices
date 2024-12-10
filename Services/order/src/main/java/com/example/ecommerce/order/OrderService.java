package com.example.ecommerce.order;

import com.example.ecommerce.customer.CustomerClient;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.kafka.OrderConfirmation;
import com.example.ecommerce.kafka.OrderProducer;
import com.example.ecommerce.orderLine.OrderLineService;
import com.example.ecommerce.payment.PaymentClient;
import com.example.ecommerce.payment.PaymentRequest;
import com.example.ecommerce.product.ProductClient;
import com.example.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.ecommerce.orderLine.OrderLineRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService{
    private final OrderRepository repository;
    private final CustomerClient customerClient;
   private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
   private final OrderProducer orderProducer;
    private final OrderMapper mapper ;


    public Integer createOrder(OrderRequest request) {
        //check the customer -->openFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Can notification place order::No provided customer with this ID"));
        //purchase products ---> use product microservice Rest template
        var purchasedProducts = productClient.purchaseProducts(request.products());

        //persist order
        var order = this.repository.save(mapper.toOrder(request));

        //persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        //start payment process
        var paymentRequest=new PaymentRequest(request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer);
        paymentClient.requestOrderPayment(paymentRequest);
        //send order confirmation ---> notification microservice(kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts));

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(()->new EntityNotFoundException("No order found with the provided ID"));
    }
}
