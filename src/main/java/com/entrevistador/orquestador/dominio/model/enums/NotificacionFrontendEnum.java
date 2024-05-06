package com.entrevistador.orquestador.dominio.model.enums;

import lombok.Getter;

@Getter
public enum NotificacionFrontendEnum {
    NOTIFICAR_MATCH_CV("Notificando Match CV");

    private String descripcion;

    NotificacionFrontendEnum(String descripcion) {
        this.descripcion = descripcion;
    }
}
