package com.deliverytech.delivery_api.dto.request;

import java.util.List;

import com.deliverytech.delivery_api.model.Endereco;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long restauranteId;


    @NotNull
    private Endereco enderecoEntrega;

    @NotNull
    private List<ItemPedidoRequest> itens;

}
