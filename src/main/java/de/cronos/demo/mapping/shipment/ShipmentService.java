package de.cronos.demo.mapping.shipment;

import de.cronos.demo.mapping.common.mapping.JavaUtilTimeMapper;
import de.cronos.demo.mapping.orders.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShipmentService {
    protected final JavaUtilTimeMapper timeMapper;

    public Optional<OffsetDateTime> estimate(OrderEntity order) {
        return switch (order.getState()) {
            case NEW -> Optional.of(LocalDateTime.now().plusDays(1)).map(timeMapper::toOffsetDateTime);
            case ACCEPTED -> Optional.of(LocalDateTime.now().plusHours(18)).map(timeMapper::toOffsetDateTime);
            case IN_PROGRESS -> Optional.of(LocalDateTime.now().plusHours(3)).map(timeMapper::toOffsetDateTime);
            case SUCCESS -> Optional.of(order.getCreated()).map(timeMapper::toOffsetDateTime);
            case CANCELED, FAILURE -> Optional.empty();
        };
    }

}
