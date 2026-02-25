package com.deliverytech.delivery_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery_api.model.Endereco;
import com.deliverytech.delivery_api.model.StatusPedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponse {

    private Long id;
    private Long clienteId;
    private Long restauranteId;
    private Endereco enderecoEntrega;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private LocalDateTime dataPedido;
    private List<ItemPedidoResponse> itens;


}
