package store.order;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface OrderRepository extends CrudRepository<OrderModel, String>{
    List<OrderModel> findByIdAccount(String idAccount);
}
