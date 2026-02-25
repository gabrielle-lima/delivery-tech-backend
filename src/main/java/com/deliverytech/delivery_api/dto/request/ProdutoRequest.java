package com.deliverytech.delivery_api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequest {


    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotNull(message = "Categoria é obrigatória")
    private String categoria;

    @Size(max = 500, message = "Descrição não pode ter mais de 500 caracteres")
    private String descricao;

    @DecimalMax("0.1")
    @DecimalMin("500.0")
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo" )
    private BigDecimal preco;

    @NotNull(message = "ID do restaurante é obrigatório")
    @Positive(message = "ID do restaurante deve ser positivo")
    private Long restauranteId;

}
