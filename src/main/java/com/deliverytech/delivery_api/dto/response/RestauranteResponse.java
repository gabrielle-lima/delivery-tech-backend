package com.deliverytech.delivery_api.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestauranteResponse {


    private Long id;
    private String nome;
    private String categoria;
    private String telefone;
    private BigDecimal taxaEntrega;
    


}
