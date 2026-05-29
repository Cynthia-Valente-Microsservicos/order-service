package store.order;

import java.util.List;

public class OrderParser {

    // Converte o domínio para a estrutura detalhada do POST (com itens)
    public static OrderOut to(Order order) {
        if (order == null) return null;

        List<OrderItemOut> itemsOut = order.items() == null ? List.of() : order.items().stream()
            .map(item -> OrderItemOut.builder()
                .id(item.id())
                .product(OrderProductOut.builder().id(item.idProduct()).build())
                .quantity(item.quantity())
                .total(item.total())
                .build())
            .toList();

        return OrderOut.builder()
            .id(order.id())
            .date(order.date())
            .items(itemsOut)
            .total(order.total())
            .build();
    }

    public static List<OrderSummaryOut> toSummaryList(List<Order> orders) {
    if (orders == null) return List.of();

    return orders.stream()
        .map((Order order) -> OrderSummaryOut.builder()
            .id(order.id())
            .date(order.date())
            .total(order.total())
            .build())
        .toList();
}
}