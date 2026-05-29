package store.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-client", url = "http://product:8080")
public interface ProductClient {

    @GetMapping("/products/{id}")
    public ProductOut fetchProductById(@PathVariable("id") String id);
}