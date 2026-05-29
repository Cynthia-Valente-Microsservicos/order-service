package store.order;

import lombok.Builder;

@Builder
public record ProductOut(
    String id,
    String name,
    Double price
) {}