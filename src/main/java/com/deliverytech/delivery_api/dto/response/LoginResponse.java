package com.deliverytech.delivery_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Schema(
    description = "Resposta quando realizar o login",
    title = "Login Response DTO"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1...")
    private String token;

    @Schema(description = "Nome de exibição do usuário", example = "Carlos Silva")
    private String username;

    @Schema(description = "Mensagem informativa sobre o status do login", example = "Login realizado com sucesso!")
    private String message;

}
 
