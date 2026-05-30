package store.order;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderResource implements OrderController {

    @Autowired
    private OrderService orderService;

    @Override
    public ResponseEntity<Void> create(
        @RequestBody OrderIn in, 
        @RequestHeader("idAccount") String idAccount
    ) {
        orderService.createOrder(in, idAccount); 
        
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<OrderSummaryOut>> findAllByAccount(
        @RequestHeader("idAccount") String idAccount
    ) {
        List<Order> orders = orderService.findOrdersByAccount(idAccount);
        List<OrderSummaryOut> summaryOutList = OrderParser.toSummaryList(orders); 
        return ResponseEntity.ok(summaryOutList);
    }

    @Override
    public ResponseEntity<OrderOut> findByOrderId(@PathVariable("id") String id) {
        Order order = orderService.findOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(OrderParser.to(order));
    }

    @Override
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }
}