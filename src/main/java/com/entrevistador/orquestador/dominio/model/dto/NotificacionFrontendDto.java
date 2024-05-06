package com.entrevistador.orquestador.dominio.model.dto;

import com.entrevistador.orquestador.dominio.model.enums.NotificacionFrontendEnum;
import lombok.Builder;

@Builder
public class NotificacionFrontendDto {
    private NotificacionFrontendEnum tipoNotificacion;
    private String mensajeNotificacion;
}
