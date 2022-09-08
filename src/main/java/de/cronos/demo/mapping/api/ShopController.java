package de.cronos.demo.mapping.api;

import de.cronos.demo.mapping.api.model.ShopStatistics;
import de.cronos.demo.mapping.api.model.StatisticsMapper;
import de.cronos.demo.mapping.customers.CustomerRepository;
import de.cronos.demo.mapping.customers.model.CustomerMapper;
import de.cronos.demo.mapping.customers.model.events.CreateCustomerEvent;
import de.cronos.demo.mapping.customers.model.events.UpdateCustomerEvent;
import de.cronos.demo.mapping.customers.model.read.CustomerDetails;
import de.cronos.demo.mapping.customers.model.read.CustomerInfo;
import de.cronos.demo.mapping.orders.OrderRepository;
import de.cronos.demo.mapping.orders.model.OrderEntity;
import de.cronos.demo.mapping.orders.model.OrderMapper;
import de.cronos.demo.mapping.orders.model.OrderState;
import de.cronos.demo.mapping.orders.model.events.PlaceOrderEvent;
import de.cronos.demo.mapping.orders.model.read.OrderDetails;
import de.cronos.demo.mapping.orders.model.read.OrderInfo;
import de.cronos.demo.mapping.products.ProductRepository;
import de.cronos.demo.mapping.products.model.ProductMapper;
import de.cronos.demo.mapping.products.model.read.ProductDetails;
import de.cronos.demo.mapping.products.model.read.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static de.cronos.demo.mapping.api.model.StatisticsMapper.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/b2c")
public class ShopController {

    protected final StatisticsMapper statisticsMapper;
    protected final CustomerRepository customerRepository;
    protected final CustomerMapper customerMapper;
    protected final ProductRepository productRepository;
    protected final ProductMapper productMapper;
    protected final OrderRepository orderRepository;
    protected final OrderMapper orderMapper;

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Statistics -------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */
    @GetMapping("/statistics")
    public ResponseEntity<ShopStatistics> getStatistics() {
        return ResponseEntity.ok(statisticsMapper.basedOn(Map.of(
                SOURCE_FIELD_SHOP_SECURE, "true",
                SOURCE_FIELD_SHOP_OPEN, "yeeeehaaa",
                SOURCE_FIELD_SAP_ACTIVE, "X",
                SOURCE_FIELD_LAST_COMPLAIN, LocalDateTime.now().minusYears(50).toString()
        )));
    }


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
    public ResponseEntity<CustomerDetails> createCustomer(@RequestBody @Valid CreateCustomerEvent event) {
        // Option 1: Use Matryoshka style mapping with MapStruct
        //           pros: delegation of consistency checks to declarative framework (at compile time!); error handling
        //                 will most certainly be easier (compared to fluent Streams usage due to missing error channel)
        //           cons: imperative approach with local state hiding implementation details (can be seen as an
        //                 advantage either);
        return ResponseEntity.ok(
                customerMapper.toDetails(
                        customerRepository.save(
                                customerMapper.from(event)
                        )
                )
        );
    }

    @PutMapping("/customers")
    @Transactional
    public ResponseEntity<CustomerDetails> updateCustomer(@RequestBody @Valid UpdateCustomerEvent update) {
        // Option 2: Manually use functional style method chaining in combindation with lombok's "@With" feature
        //           (looks clean... but consistency might be a problem due to manual sync between event and entity)
        return customerRepository.findById(update.getCustomerId())
                // Option 2a: Use stateless approach based on event
                //            pros: no obsolete entity instance creation
                //            cons: mapping single attribute to entity looks wierd and "orElse(entity)" suffix is fishy
                //                  folklore... reducing the readability even more
                .map(customer -> update.getEmail().map(customer::withEmail).orElse(customer))
                .map(customer -> update.getFirstName().map(customer::withFirstName).orElse(customer))
                // Option 2b: Use stateless approach based on entity
                //            pros: descriptive and readable
                //            cons: unnecessary entity creation if event doesn't contain attribute changes
                .map(customer -> customer.withLastName(update.getLastName().orElse(customer.getLastName())))
                .map(customer -> customer.withBirthday(update.getBirthday().orElse(customer.getBirthday())))
                .map(customerRepository::save)
                .map(customerMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
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
        --- Products ---------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */

    @GetMapping("/products")
    public Page<ProductInfo> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toInfo);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDetails> getProductDetails(@PathVariable UUID productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDetails)
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
    public ResponseEntity<OrderDetails> placeOrder(@RequestBody @Valid PlaceOrderEvent event) {
        return customerRepository.findById(event.getCustomerId())
                .map(customer -> OrderEntity.builder()
                        .customer(customer)
                        .state(OrderState.NEW)
                        .product(productRepository.findById(event.getProductId()).orElseThrow())
                        .quantity(event.getQuantity())
                        .build())
                .map(orderRepository::save)
                .map(orderMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    @DeleteMapping("/orders/{orderId}")
    @Transactional
    public ResponseEntity<OrderDetails> cancelOrder(@PathVariable UUID orderId) {
        return orderRepository.findCancelableById(orderId)
                .map(order -> order.withState(OrderState.CANCELED))
                .map(orderRepository::save)
                .map(orderMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetails> getOrderDetails(@PathVariable UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
