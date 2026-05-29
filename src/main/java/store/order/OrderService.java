package store.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

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

        return savedModel.to();
    }

    public List<Order> findOrdersByAccount(String idAccount) {
        List<OrderModel> orderModels = orderRepository.findByIdAccount(idAccount);
        
        return orderModels.stream()
            .map(OrderModel::to)
            .toList();
    }
}