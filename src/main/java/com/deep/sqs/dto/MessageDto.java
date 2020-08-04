package com.deep.sqs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Mensagem enviada para a fila.")
public class MessageDto {

	@ApiModelProperty("MessageID da mensagem utilizado nas filas FIFO.")
	private String messageId;

	@ApiModelProperty("GroupID da mensagem utilizado nas filas FIFO.")
	private String groupId;
	
	@ApiModelProperty("Conteúdo da mensagem. Caso não seja fornecido, será lançada uma BusinessExcetion, simulando um erro de regra de negócio.")
	private String content;

	@ApiModelProperty("Simula o tempo de processamento de uma mensagem. Valor fornecido deve ser em milissegundos.")
	private int delay;

	@ApiModelProperty("Força o lançamento de uma RuntimeException para simular o retry da própria fila.")
	private boolean forceException;

}
