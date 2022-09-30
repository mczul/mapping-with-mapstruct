package de.cronos.demo.mapping.api;

import de.cronos.demo.mapping.SecurityConfig;
import de.cronos.demo.mapping.api.model.StatisticsMapper;
import de.cronos.demo.mapping.common.AppConstants;
import de.cronos.demo.mapping.customers.CustomerMapper;
import de.cronos.demo.mapping.customers.CustomerRepository;
import de.cronos.demo.mapping.orders.OrderEntity;
import de.cronos.demo.mapping.orders.OrderMapper;
import de.cronos.demo.mapping.orders.OrderRepository;
import de.cronos.demo.mapping.orders.events.PlaceOrderEvent;
import de.cronos.demo.mapping.orders.events.QueryOrderEvent;
import de.cronos.demo.mapping.orders.summary.OrderInfo;
import de.cronos.demo.mapping.products.ProductMapper;
import de.cronos.demo.mapping.products.ProductRepository;
import de.cronos.demo.mapping.products.statistics.ProductStatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static de.cronos.demo.mapping.orders.OrderMapperTest.randomDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testing should always be context driven - this sample shows technical concepts that will not fit
 * scaling requirements (e.g. the code below will violate the DRY principle when testing more than one endpoint).
 * <p>
 * There are several options to solve this scaling issue but all will reduce readability. To put it in a nutshell:
 * This sample shows low level strategies and testing features of Spring Boot, JUnit 5 and Mockito... it's not a
 * copy & paste template for hundreds of tests.
 * <p>
 * Wisdom to keep in mind:
 * <ul>
 *  <li>
 *      Keep your tests organized or redundancy and inconsistency will grow with the amount of code appended to the
 *      end of the file. Your colleagues and your future me will never read 1000 lines of fishy statements but attach
 *      new stuff.
 *  </li>
 *  <li>Focus on what really matters, don't maximize code coverage</li>
 *  <li>Set boundaries and realize responsibilities</li>
 *  <li>Testing means switching perspective. Be evil and creative.</li>
 *  <li>Write less test code and use parameterized inputs to improve meaningfulness</li>
 * </ul>
 */

@WebMvcTest
@Import({SecurityConfig.class})
@DisplayName("Shop: REST Controller")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ShopControllerIT {
    public static final String JSON_PATH_PAGE_CONTENT = "$.content";
    public static final String JSON_PATH_PAGE_TOTAL_PAGES = "$.totalPages";
    public static final String JSON_PATH_PAGE_TOTAL_ELEMENTS = "$.totalElements";

    @Value("${spring.data.web.pageable.page-parameter:page}")
    protected String queryParamNamePageIndex;
    @Value("${spring.data.web.pageable.size-parameter:size}")
    protected String queryParamNamePageSize;
    @Value("${spring.data.web.sort.sort-parameter:sort}")
    protected String queryParamNamePageSort;

    /*
        This is a playground project for demonstration purposes - otherwise be aware of long lists of mocked
        dependencies as they are always a neat indicator for unclean responsibilities... SoC is important!
     */
    @MockBean
    protected StatisticsMapper statisticsMapper;
    @MockBean
    protected CustomerRepository customerRepository;
    @MockBean
    protected CustomerMapper customerMapper;
    @MockBean
    protected ProductRepository productRepository;
    @MockBean
    protected ProductStatisticsRepository productStatisticsRepository;
    @MockBean
    protected ProductMapper productMapper;
    @MockBean
    protected OrderRepository orderRepository;
    @MockBean
    protected OrderMapper orderMapper;

    @Autowired
    protected MockMvc mvc;

    /**
     * All of the following tests rely on knowledge of implementation details - e.g. the tests expect presence and usage
     * of the {@link CustomerRepository#findAll(Pageable)}. Tests should not reflect implementation details but focus
     * on the public surface (see <a href="http://shoulditestprivatemethods.com/">shoulditestprivatemethods.com</a>).
     * <p>
     * In contrast to this general rule of thumb: checking contracts between different layer (like web and persistence)
     * can be necessary in some cases (see {@link ArgumentCaptor} and Mockito's {@link org.mockito.Mockito#verify(Object)}
     * or {@link org.mockito.BDDMockito#given(Object)} for further details).
     */
    @Nested
    @DisplayName(Customers.BASE_PATH)
    class Customers {
        protected static final String BASE_PATH = "/b2c/customers/infos";

        @Captor
        protected ArgumentCaptor<Pageable> pageableCaptor;

        @Test
        @WithAnonymousUser
        void get_as_anonymous() throws Exception {
            // given
            given(customerRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of()));

            // when
            mvc.perform(get(BASE_PATH))
                    // then
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(""));

            verify(customerRepository, never()).findAll(any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = AppConstants.ROLE_NAME_B2C_CUSTOMER)
        void get_as_unauthorized_user() throws Exception {
            // given
            given(customerRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of()));

            // when
            mvc.perform(get(BASE_PATH))
                    // then
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(""));

            verify(customerRepository, never()).findAll(any(Pageable.class));
        }

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "0 | 10 | lastName  | DESC | firstName | ASC",
                "1 |  5 | firstName | ASC  | lastName  | DESC"
        })
        @WithMockUser(roles = AppConstants.ROLE_NAME_ADMIN)
        void get_as_authorized_user(
                int pageIndex, int pageSize,
                String firstSortingAttribute, Direction firstSortingDirection,
                String secondSortingAttribute, Direction secondSortingDirection
        ) throws Exception {
            // given
            final var expectedContentType = MediaType.APPLICATION_JSON;
            given(customerRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of()));

            // when
            mvc.perform(get(BASE_PATH)
                            .queryParam(queryParamNamePageIndex, String.valueOf(pageIndex))
                            .queryParam(queryParamNamePageSize, String.valueOf(pageSize))
                            .queryParam(queryParamNamePageSort, firstSortingAttribute + "," + firstSortingDirection.name().toLowerCase(Locale.ROOT))
                            .queryParam(queryParamNamePageSort, secondSortingAttribute + "," + secondSortingDirection.name().toLowerCase(Locale.ROOT))
                            .accept(expectedContentType))
                    // then
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(expectedContentType))
                    .andExpect(jsonPath(JSON_PATH_PAGE_CONTENT).exists())
                    .andExpect(jsonPath(JSON_PATH_PAGE_TOTAL_PAGES).value(1))
                    .andExpect(jsonPath(JSON_PATH_PAGE_TOTAL_ELEMENTS).value(0));

            verify(customerRepository, times(1)).findAll(pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().getPageSize()).as("Wrong page size").isEqualTo(pageSize);
            assertThat(pageableCaptor.getValue().getPageNumber()).as("Wrong page index").isEqualTo(pageIndex);
            assertThat(pageableCaptor.getValue().getSort().isSorted()).as("Not sorted").isTrue();

            final var actualFirstOrder = Optional.ofNullable(pageableCaptor.getValue().getSort()
                    .getOrderFor(firstSortingAttribute));
            assertThat(actualFirstOrder).as("First sorting parameter not considered")
                    .hasValue(new Sort.Order(firstSortingDirection, firstSortingAttribute));

            final var actualSecondOrder = Optional.ofNullable(pageableCaptor.getValue().getSort()
                    .getOrderFor(secondSortingAttribute));
            assertThat(actualSecondOrder).as("Second sorting parameter not considered")
                    .hasValue(new Sort.Order(secondSortingDirection, secondSortingAttribute));
        }

    }

    @Nested
    @DisplayName(Orders.BASE_PATH)
    class Orders {
        protected static final String BASE_PATH = "/b2c/orders";

        @Nested
        class PlaceOrder {

            @Test
            @WithAnonymousUser
            void with_csrf_token_as_anonymous_user() throws Exception {
                // given

                // when
                mvc.perform(
                                post(BASE_PATH)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                {
                                                    "customerId": "%s",
                                                    "productId":  "%s",
                                                    "quantity":    %d
                                                }
                                                """.formatted(UUID.randomUUID(), UUID.randomUUID(), 1)
                                        )
                        )

                        // then
                        .andExpect(status().isUnauthorized());
            }

            @Test
            @WithMockUser(roles = {AppConstants.ROLE_NAME_ADMIN})
            void with_csrf_token_as_unauthorized_user() throws Exception {
                // given

                // when
                mvc.perform(
                                post(BASE_PATH)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                {
                                                    "customerId": "%s",
                                                    "productId":  "%s",
                                                    "quantity":    %d
                                                }
                                                """.formatted(UUID.randomUUID(), UUID.randomUUID(), 1)
                                        )
                        )

                        // then
                        .andExpect(status().isForbidden());
            }

            @Test
            @WithMockUser(roles = {AppConstants.ROLE_NAME_B2C_CUSTOMER})
            void without_csrf_token_as_authorized_user() throws Exception {
                // given

                // when
                mvc.perform(
                                post(BASE_PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                {
                                                    "customerId": "%s",
                                                    "productId":  "%s",
                                                    "quantity":    %d
                                                }
                                                """.formatted(
                                                UUID.randomUUID(), UUID.randomUUID(), 1)
                                        )
                        )

                        // then
                        .andExpect(status().isForbidden());
            }

            @ParameterizedTest
            @CsvSource({
                    // Invalid customer UUID
                    "12345678-90ab-cdef-1234-567890ab0101x, 12345678-90ab-cdef-1234-567890ab0102, 1",
                    // Invalid product UUID
                    "12345678-90ab-cdef-1234-567890ab0101, 12345678-90ab-cdef-1234-567890ab0102x, 1",
                    // Invalid quantity
                    "12345678-90ab-cdef-1234-567890ab0101, 12345678-90ab-cdef-1234-567890ab0102, 0",

            })
            @WithMockUser(roles = {AppConstants.ROLE_NAME_B2C_CUSTOMER})
            void with_csrf_token_as_authorized_user_and_with_invalid_payload(String customerId, String productId, Integer quantity) throws Exception {
                // given

                // when
                mvc.perform(
                                post(BASE_PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(csrf())
                                        .content("""
                                                {
                                                    "customerId": "%s",
                                                    "productId":  "%s",
                                                    "quantity":    %d
                                                }
                                                """.formatted(customerId, productId, quantity)
                                        )
                        )

                        // then
                        .andExpect(status().isBadRequest());

                verify(orderRepository, never()).save(any(OrderEntity.class));
            }

            @Test
            @WithMockUser(roles = {AppConstants.ROLE_NAME_B2C_CUSTOMER})
            void with_csrf_token_as_authorized_user_when_persistence_throws_exception() throws Exception {
                // given
                final var orderEntity = mock(OrderEntity.class);
                given(orderMapper.from(any(PlaceOrderEvent.class))).willReturn(orderEntity);
                given(orderRepository.save(orderEntity)).willThrow(new DataIntegrityViolationException("Unique constraint violated."));

                // when
                mvc.perform(
                                post(BASE_PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(csrf())
                                        .content("""
                                                {
                                                    "customerId": "%s",
                                                    "productId":  "%s",
                                                    "quantity":    %d
                                                }
                                                """.formatted(UUID.randomUUID(), UUID.randomUUID(), 1)
                                        )
                        )

                        // then
                        .andExpect(status().isInternalServerError());
            }

            @Test
            @WithMockUser(roles = {AppConstants.ROLE_NAME_B2C_CUSTOMER})
            void with_csrf_token_as_authorized_user_and_with_valid_payload() throws Exception {
                // given
                //  -> we do not need to care about entities... the return value of the final
                //     outcome is the only relevant thing.
                final var expectedDetails = randomDetails();
                final var orderEntity = mock(OrderEntity.class);
                given(orderMapper.from(any(PlaceOrderEvent.class))).willReturn(orderEntity);
                given(orderRepository.save(orderEntity)).willReturn(orderEntity);
                given(orderMapper.toDetails(orderEntity)).willReturn(expectedDetails);

                // when
                mvc.perform(
                                post(BASE_PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(csrf())
                                        .content("""
                                                {
                                                    "customerId": "%s",
                                                    "productId":  "%s",
                                                    "quantity":    %d
                                                }
                                                """.formatted(
                                                        expectedDetails.getCustomer().getId(),
                                                        expectedDetails.getProduct().getId(),
                                                        expectedDetails.getQuantity()
                                                )
                                        )
                        )

                        // then
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(expectedDetails.getId().toString()))
                        .andExpect(jsonPath("$.customer.id").value(expectedDetails.getCustomer().getId().toString()))
                        .andExpect(jsonPath("$.product.id").value(expectedDetails.getProduct().getId().toString()))
                        .andExpect(jsonPath("$.quantity").value(expectedDetails.getQuantity()))
                        .andExpect(jsonPath("$.estimatedShipment").isNotEmpty());


                verify(orderRepository, times(1)).save(any(OrderEntity.class));
            }

        }

    }

    @Nested
    @DisplayName(OrderQuery.BASE_PATH)
    class OrderQuery {
        protected static final String BASE_PATH = "/b2c/orders/query";

        @Test
        @WithAnonymousUser
        void with_csrf_token_as_anonymous_user() throws Exception {
            // given

            // when
            mvc.perform(
                            post(BASE_PATH)
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}")
                    )

                    // then
                    .andExpect(status().isUnauthorized());
            verify(orderRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = {AppConstants.ROLE_NAME_B2C_CUSTOMER})
        void with_csrf_token_as_unauthorized_user() throws Exception {
            // given

            // when
            mvc.perform(
                            post(BASE_PATH)
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}")
                    )

                    // then
                    .andExpect(status().isForbidden());
            verify(orderRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = {AppConstants.ROLE_NAME_ADMIN})
        void without_csrf_token_as_authorized_user() throws Exception {
            // given

            // when
            mvc.perform(
                            post(BASE_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}")
                    )

                    // then
                    .andExpect(status().isForbidden());
            verify(orderRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = {AppConstants.ROLE_NAME_ADMIN})
        void with_csrf_token_as_authorized_user() throws Exception {
            // given
            final var mockedSpec = mock(Specification.class);
            given(orderRepository.buildSpec(any(QueryOrderEvent.class))).willReturn(mockedSpec);
            final var mockedPage = mock(Page.class);
            given(orderRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(mockedPage);
            given(orderMapper.toInfo(any(OrderEntity.class))).willReturn(OrderInfo.builder().build());

            // when
            mvc.perform(
                            post(BASE_PATH)
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "customerMail": "max.mustermann@web.de",
                                                "orderState": "NEW"
                                            }
                                            """
                                    )
                    )

                    // then
                    .andExpect(status().isOk());

            verify(orderRepository, times(+1)).findAll(
                    any(Specification.class), any(Pageable.class)
            );
        }

    }

}
