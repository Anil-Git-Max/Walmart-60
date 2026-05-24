package com.walmart.cart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;
}
