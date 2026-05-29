package store.order;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder @Accessors(chain = true, fluent = true)
public class Order {

    private String id;
    private String idAccount;
    private LocalDateTime date;
    private List<OrderItem> items;
    private Double total;

}