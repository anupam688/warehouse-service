package com.demo.warehouseservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductArticleDto implements Serializable {

    @JsonProperty("art_id")
    @SerializedName("art_id")
    private Long articleId;

    @JsonProperty("amount_of")
    @SerializedName("amount_of")
    private Long quantity;
}
