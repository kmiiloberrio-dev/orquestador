package com.entrevistador.orquestador.application.usescases;

import com.entrevistador.orquestador.dominio.model.dto.InformacionEmpresaDto;
import com.entrevistador.orquestador.dominio.model.dto.MensajeValidacionMatch;
import com.entrevistador.orquestador.dominio.model.dto.NotificacionFrontendDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrquestadorEntrevista {

    Mono<Void> receptorInformacionEmpresa(String idEntrevista, String idInformacionEmpresaRag);

    Mono<Void> enviarNotificacionFront(NotificacionFrontendDto notificacion);

    Mono<Void> receptorHojaDeVidaMatch(MensajeValidacionMatch mensajeValidacionMatch);
}
