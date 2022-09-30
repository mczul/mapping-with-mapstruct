package de.cronos.demo.mapping.api;

import de.cronos.demo.mapping.api.model.ShopStatistics;
import de.cronos.demo.mapping.api.model.StatisticsMapper;
import de.cronos.demo.mapping.customers.CustomerRepository;
import de.cronos.demo.mapping.customers.model.CustomerMapper;
import de.cronos.demo.mapping.customers.model.events.CreateCustomerEvent;
import de.cronos.demo.mapping.customers.model.events.UpdateCustomerEvent;
import de.cronos.demo.mapping.customers.model.read.CustomerDetails;
import de.cronos.demo.mapping.customers.model.read.CustomerInfo;
import de.cronos.demo.mapping.customers.model.read.CustomerRecord;
import de.cronos.demo.mapping.customers.model.read.CustomerStatistics;
import de.cronos.demo.mapping.orders.OrderMapper;
import de.cronos.demo.mapping.orders.OrderRepository;
import de.cronos.demo.mapping.orders.OrderState;
import de.cronos.demo.mapping.orders.events.PlaceOrderEvent;
import de.cronos.demo.mapping.orders.events.QueryOrderEvent;
import de.cronos.demo.mapping.orders.summary.OrderDetails;
import de.cronos.demo.mapping.orders.summary.OrderInfo;
import de.cronos.demo.mapping.products.ProductMapper;
import de.cronos.demo.mapping.products.ProductRepository;
import de.cronos.demo.mapping.products.statistics.ProductStatistics;
import de.cronos.demo.mapping.products.statistics.ProductStatisticsRepository;
import de.cronos.demo.mapping.products.summary.ProductDetails;
import de.cronos.demo.mapping.products.summary.ProductInfo;
import de.cronos.demo.mapping.products.summary.ProductRecord;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static de.cronos.demo.mapping.api.model.StatisticsMapper.*;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/b2c")
public class ShopController {

    protected final StatisticsMapper statisticsMapper;
    protected final CustomerRepository customerRepository;
    protected final CustomerMapper customerMapper;
    protected final ProductRepository productRepository;
    protected final ProductStatisticsRepository productStatisticsRepository;
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
        --- Customer: Read ---------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */

    @GetMapping("/customers/infos")
    @Transactional(readOnly = true)
    public Page<CustomerInfo> getCustomerInfos(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toInfo);
    }

    @GetMapping("/customers/details")
    @Transactional(readOnly = true)
    public Page<CustomerDetails> getCustomerDetails(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toDetails);
    }

    @GetMapping("/customers/statistics")
    @Transactional(readOnly = true)
    public Page<CustomerStatistics> getCustomerStatistics(Pageable pageable) {
        return customerRepository.loadActiveCustomerStatistics(pageable);
    }

    @GetMapping("/customers/records")
    @Transactional(readOnly = true)
    public Page<CustomerRecord> getCustomerRecords(Pageable pageable) {
        return customerRepository.loadCustomerRecords(pageable);
    }

    @GetMapping("/customers/{customerId}")
    @Transactional(readOnly = true)
    public ResponseEntity<CustomerDetails> getCustomerDetailsById(@PathVariable UUID customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Customer: Write --------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */

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

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Products: Read ---------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */

    @GetMapping("/products/infos")
    @Transactional(readOnly = true)
    public Page<ProductInfo> getProductInfos(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toInfo);
    }

    @GetMapping("/products/details")
    @Transactional(readOnly = true)
    public Page<ProductDetails> getProductDetails(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDetails);
    }

    @GetMapping("/products/records")
    @Transactional(readOnly = true)
    public Page<ProductRecord> getProductRecords(Pageable pageable) {
        return productRepository.loadProductRecords(pageable);
    }

    @GetMapping("/products/statistics")
    @Transactional(readOnly = true)
    public Page<ProductStatistics> getProductStatistics(Pageable pageable) {
        return productStatisticsRepository.findAll(pageable);
    }

    @GetMapping("/products/{productId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ProductDetails> getProductDetailsById(@PathVariable UUID productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Order: Read ------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */

    @GetMapping("/orders/infos")
    @Transactional(readOnly = true)
    public Page<OrderInfo> getOrderInfos(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toInfo);
    }

    @GetMapping("/orders/details")
    @Transactional(readOnly = true)
    public Page<OrderDetails> getOrderDetails(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDetails);
    }

    @PostMapping("/orders/query")
    @Transactional(readOnly = true)
    public Page<OrderInfo> executeOrderQuery(Pageable pageable, @RequestBody @Valid QueryOrderEvent queryOrderEvent) {
        final var spec = orderRepository.buildSpec(queryOrderEvent);
        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toInfo);
    }

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Order: Write -----------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
     */
    @PostMapping("/orders")
    @Transactional
    public ResponseEntity<OrderDetails> placeOrder(@RequestBody @Valid PlaceOrderEvent event) {
        // Following code is OK and does not hide implementation details... nice and transparent.
        // On the other hand: What if we add new attributes to our OrderEntity? We need to traverse all places where new
        // instances are created and update every single usage.
        /*
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
                .orElse(ResponseEntity.badRequest().build());
         */

        // This version uses MapStruct... the OrderMapper provides overloaded "from" functions that have
        // descriptive mappings and prevent inconsistencies (see annotation process configuration in "pom.xml"...
        // "mapstruct.unmappedTargetPolicy=ERROR")
        /*
        return Optional.ofNullable(orderMapper.from(event))
                .map(orderRepository::save)
                .map(orderMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
        */

        // This version looks like the solution above but uses a nice lib called "vavr" to solve an important downside
        // of the Java Stream API: By default there is no "error channel" concept other frameworks provide (e.g. RxJS).
        return Try.of(() -> orderMapper.from(event))
                .onFailure(throwable -> log.warn("Failed to create order entity!", throwable))
                .map(orderRepository::save)
                .onFailure(throwable -> log.warn("Failed to save order!", throwable))
                .map(orderMapper::toDetails)
                .onFailure(throwable -> log.warn("Failed to build order details!", throwable))
                .map(ResponseEntity::ok)
                .getOrElseGet(throwable -> ResponseEntity.internalServerError().build());
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
    @Transactional(readOnly = true)
    public ResponseEntity<OrderDetails> getOrderDetailsById(@PathVariable UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
