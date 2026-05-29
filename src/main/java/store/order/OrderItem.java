package store.order;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder @Accessors(chain = true, fluent = true)
public class OrderItem {

    private String id;
    private String idProduct;
    private Integer quantity;
    private Double total;

}