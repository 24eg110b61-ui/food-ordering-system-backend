package com.foodorder.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Orders order;

    // Snapshot of the food at order time (stays correct even if food is edited/deleted)
    private Long foodId;
    private String foodName;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private int quantity;
}
