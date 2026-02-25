package com.deliverytech.delivery_api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String categoria;

    @NotBlank
    private String endereco;

    @DecimalMin("0.0")
    @NotNull(message = "Taxa de entrega é obrigatória")
    @Positive(message = "Taxa de entrega deve ser positiva")
    private BigDecimal taxaEntrega;

    @NotNull
    @Min(1)
    private Integer tempoEntregaMinutos;

    private String telefone;
    private String email;

    @Positive(message = "Avaliação deve ser positiva")
    @DecimalMax(value = "5.0", message = "Avaliação deve ser igual a 5.0")
    private BigDecimal avaliacao;


}
