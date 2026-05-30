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

            kafkaTemplate.send("order-events", orderOut.id(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("[KAFKA] Falha ao entregar evento " + orderOut.id() + ": " + ex.getMessage());
                    } else {
                        System.out.println("[KAFKA] Evento entregue: " + orderOut.id()
                            + " | partition=" + result.getRecordMetadata().partition()
                            + " | offset=" + result.getRecordMetadata().offset());
                    }
                });
        } catch (Exception e) {
            System.err.println("[KAFKA] Erro ao serializar o pedido: " + e.getMessage());
        }
    }
}