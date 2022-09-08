package de.cronos.demo.mapping.api;

import de.cronos.demo.mapping.SecurityConfig;
import de.cronos.demo.mapping.api.model.StatisticsMapper;
import de.cronos.demo.mapping.common.AppConstants;
import de.cronos.demo.mapping.customers.CustomerRepository;
import de.cronos.demo.mapping.customers.model.CustomerMapper;
import de.cronos.demo.mapping.orders.OrderRepository;
import de.cronos.demo.mapping.orders.model.OrderMapper;
import de.cronos.demo.mapping.products.ProductRepository;
import de.cronos.demo.mapping.products.model.ProductMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        protected static final String BASE_PATH = "/b2c/customers";

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
        @WithMockUser(roles = AppConstants.ROLE_NAME_USER)
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
                "0 | 10 | lastName  | desc | firstName | asc",
                "1 |  5 | firstName | asc  | lastName  | desc"
        })
        @WithMockUser(roles = AppConstants.ROLE_NAME_ADMIN)
        void get_as_authorized_user(
                int pageIndex, int pageSize,
                String firstSortingAttribute, String firstSortingDirection,
                String secondSortingAttribute, String secondSortingDirection
        ) throws Exception {
            // given
            final var expectedContentType = MediaType.APPLICATION_JSON;
            given(customerRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of()));

            // when
            mvc.perform(get(BASE_PATH)
                            .queryParam(queryParamNamePageIndex, String.valueOf(pageIndex))
                            .queryParam(queryParamNamePageSize, String.valueOf(pageSize))
                            .queryParam(queryParamNamePageSort, firstSortingAttribute + "," + firstSortingDirection)
                            .queryParam(queryParamNamePageSort, secondSortingAttribute + "," + secondSortingDirection)
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
                    .hasValue(new Sort.Order(Direction.fromString(firstSortingDirection), firstSortingAttribute));

            final var actualSecondOrder = Optional.ofNullable(pageableCaptor.getValue().getSort()
                    .getOrderFor(secondSortingAttribute));
            assertThat(actualSecondOrder).as("Second sorting parameter not considered")
                    .hasValue(new Sort.Order(Direction.fromString(secondSortingDirection), secondSortingAttribute));
        }

    }

}
