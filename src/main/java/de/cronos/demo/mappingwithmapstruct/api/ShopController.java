package de.cronos.demo.mappingwithmapstruct.api;

import de.cronos.demo.mappingwithmapstruct.customers.CustomerDetails;
import de.cronos.demo.mappingwithmapstruct.customers.CustomerInfo;
import de.cronos.demo.mappingwithmapstruct.customers.CustomerMapper;
import de.cronos.demo.mappingwithmapstruct.customers.CustomerRepository;
import de.cronos.demo.mappingwithmapstruct.customers.events.CreateCustomerEvent;
import de.cronos.demo.mappingwithmapstruct.customers.events.UpdateCustomerEvent;
import de.cronos.demo.mappingwithmapstruct.orders.*;
import de.cronos.demo.mappingwithmapstruct.orders.events.CreateOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/b2c")
public class ShopController {

    protected final CustomerRepository customerRepository;
    protected final CustomerMapper customerMapper;
    protected final OrderRepository orderRepository;
    protected final OrderMapper orderMapper;

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Customer ---------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */

    @GetMapping("/customers")
    public Page<CustomerInfo> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toInfo);
    }

    @PostMapping("/customers")
    @Transactional
    public ResponseEntity<CustomerInfo> createCustomer(@RequestBody @Valid CreateCustomerEvent event) {
        // Option 1: Use Matryoshka style mapping with MapStruct
        return ResponseEntity.ok(
                customerMapper.toInfo(
                        customerRepository.save(
                                customerMapper.from(event)
                        )
                )
        );
    }

    @PutMapping("/customers")
    @Transactional
    public ResponseEntity<CustomerInfo> updateCustomer(@RequestBody @Valid UpdateCustomerEvent update) {
        return customerRepository.findById(update.getCustomerId())
                .map(customer -> customer.withEmail(update.getEmail().orElse(customer.getEmail())))
                .map(customer -> customer.withFirstName(update.getFirstName().orElse(customer.getFirstName())))
                .map(customer -> customer.withLastName(update.getLastName().orElse(customer.getLastName())))
                .map(customer -> customer.withBirthday(update.getBirthday().orElse(customer.getBirthday())))
                .map(customerRepository::save)
                .map(customerMapper::toInfo)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerDetails> getCustomerDetails(@PathVariable UUID customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Order ------------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */

    @GetMapping("/orders")
    public Page<OrderInfo> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toInfo);
    }

    @PostMapping("/orders")
    @Transactional
    public ResponseEntity<OrderInfo> placeOrder(@RequestBody @Valid CreateOrderEvent event) {
        // Option 2: Use functional style method chaining and builder
        return customerRepository.findById(event.getCustomerId())
                .map(customer -> OrderEntity.builder()
                        .customer(customer)
                        .state(OrderState.NEW)
                        .articleName(event.getArticleName())
                        .articleQuantity(event.getArticleQuantity())
                        .build())
                .map(orderRepository::save)
                .map(orderMapper::toInfo)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetails> getOrderDetails(@PathVariable UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
