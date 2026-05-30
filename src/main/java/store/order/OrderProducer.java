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
            
            kafkaTemplate.send("order-events", orderOut.id(), message);
            
            System.out.println("[KAFKA] Evento de pedido enviado com sucesso: " + orderOut.id());
        } catch (Exception e) {
            System.err.println("[KAFKA] Erro ao serializar ou enviar o pedido: " + e.getMessage());
        }
    }
}