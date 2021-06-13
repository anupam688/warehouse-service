package com.demo.warehouseservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Product extends DatabaseEntity implements Serializable {

    @Id
    @SequenceGenerator(name = "PRODUCT_ID_GENERATOR", sequenceName = "TBL_PRODUCT_IDS", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_ID_GENERATOR")
    private Long id;

    private String name;

    private BigDecimal price;

    @Transient
    private Long stock;
}
