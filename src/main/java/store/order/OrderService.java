package store.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private OrderProducer orderProducer;

    public Order createOrder(OrderIn in, String idAccount) {
        
        List<OrderItem> domainItems = in.items().stream()
            .map(itemIn -> {
                ProductOut produto = productClient.fetchProductById(itemIn.idProduct());
                
                return OrderItem.builder()
                    .idProduct(itemIn.idProduct())
                    .quantity(itemIn.quantity())
                    .total(produto.price() * itemIn.quantity())
                    .build();
            })
            .toList();

        double totalPedido = domainItems.stream()
            .mapToDouble(OrderItem::total)
            .sum();

        Order orderDomain = Order.builder()
            .idAccount(idAccount)
            .date(LocalDateTime.now())
            .items(domainItems)
            .total(totalPedido)
            .build();

        OrderModel orderModel = new OrderModel(orderDomain);
        OrderModel savedModel = orderRepository.save(orderModel);
        Order orderFinal = savedModel.to();

        orderProducer.sendOrderEvent(OrderParser.to(orderFinal));

        return orderFinal;
    }

    public List<Order> findOrdersByAccount(String idAccount) {
        List<OrderModel> orderModels = orderRepository.findByIdAccount(idAccount);
        
        return orderModels.stream()
            .map(OrderModel::to)
            .toList();
    }

    public Order findOrderById(String id) {
        Optional<OrderModel> orderModel = orderRepository.findById(id);
        return orderModel.map(OrderModel::to).orElse(null);
    }
}