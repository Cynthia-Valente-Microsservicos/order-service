package store.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendOrderEvent(OrderOut orderOut) {
        try {
            String message = objectMapper.writeValueAsString(orderOut);
            String key = (orderOut.id() != null) ? orderOut.id() : "";

            kafkaTemplate.send("order-events", key, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("[KAFKA] Falha ao entregar evento " + key + ": " + ex.getMessage());
                    } else {
                        System.out.println("[KAFKA] Evento entregue: " + key
                            + " | partition=" + result.getRecordMetadata().partition()
                            + " | offset=" + result.getRecordMetadata().offset());
                    }
                });
        } catch (Exception e) {
            System.err.println("[KAFKA] Erro ao serializar pedido: " + e.getMessage());
        }
    }
}
