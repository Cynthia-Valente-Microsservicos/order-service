package store.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "orders", schema = "orders")
@Getter @Setter @Accessors(chain = true, fluent = true)
@NoArgsConstructor @AllArgsConstructor
public class OrderModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "id_account", nullable = false)
    private String idAccount;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "total", nullable = false)
    private Double total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemModel> items = new ArrayList<>();

    public OrderModel(Order o){
        this.id = o.id();
        this.idAccount = o.idAccount();
        this.date = o.date();
        this.total = o.total();
        
        if (o.items() != null) {
            this.items = o.items().stream()
                .map(itemDomain -> new OrderItemModel()
                    .id(itemDomain.id())
                    .idProduct(itemDomain.idProduct())
                    .quantity(itemDomain.quantity())
                    .total(itemDomain.total())
                    .order(this)
                )
                .toList();
        }
    }

    public Order to() {
        List<OrderItem> domainItems = null;
        if (this.items != null) {
            domainItems = this.items.stream()
                .map(itemModel -> OrderItem.builder()
                    .id(itemModel.id())
                    .idProduct(itemModel.idProduct())
                    .quantity(itemModel.quantity())
                    .total(itemModel.total())
                    .build()
                )
                .toList();
        }

        return Order.builder()
            .id(this.id)
            .idAccount(this.idAccount)
            .date(this.date)
            .items(domainItems)
            .total(this.total)
            .build();
    }
}
