package com.deliverytech.delivery_api.model;
public enum StatusPedido {
    CRIADO("Criado"),
    PENDENTE("Pendente"),
    CONFIRMADO("Confirmado"),
    SAIU_PARA_ENTREGA("Saiu para entrega"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");


    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    };


    public String getDescricao() {
        return descricao;
    }
}
