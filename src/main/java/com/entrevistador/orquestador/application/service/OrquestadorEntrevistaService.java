package com.entrevistador.orquestador.application.service;

import com.entrevistador.orquestador.application.usescases.OrquestadorEntrevista;
import com.entrevistador.orquestador.dominio.model.dto.EntrevistaDto;
import com.entrevistador.orquestador.dominio.model.dto.FeedbackDto;
import com.entrevistador.orquestador.dominio.model.dto.MensajeValidacionMatch;
import com.entrevistador.orquestador.dominio.model.dto.RagsIdsDto;
import com.entrevistador.orquestador.dominio.model.dto.SolicitudGeneracionEntrevistaDto;
import com.entrevistador.orquestador.dominio.model.enums.EstadoEntrevistaEnum;
import com.entrevistador.orquestador.dominio.port.EntrevistaDao;
import com.entrevistador.orquestador.dominio.port.jms.JmsPublisherClient;
import com.entrevistador.orquestador.dominio.service.ValidadorEventosSimultaneosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrquestadorEntrevistaService implements OrquestadorEntrevista {

    private final JmsPublisherClient jmsPublisherClient;
    private final EntrevistaDao entrevistaDao;
    private final ValidadorEventosSimultaneosService validadorEventosSimultaneosService;

    @Override
    public Mono<Void> receptorInformacionEmpresa(String idEntrevista, String idInformacionEmpresaRag) {
        log.info(String.format("Recibiendo informacion empresa para entervista id : %s", idEntrevista));
        return this.entrevistaDao.actualizarIdInformacionEmpresaRag(idEntrevista, idInformacionEmpresaRag)
                .then(this.validadorEventosSimultaneosService.ejecutar(idEntrevista))
                .flatMap(ragsIdsDto -> enviarInformacionEntrevistaAPreparador(ragsIdsDto, idEntrevista));
    }


    @Override
    public Mono<Void> receptorHojaDeVidaMatch(MensajeValidacionMatch mensajeValidacionMatch) {
        log.info(String.format("Recibiendo informacion validacion hoja de vida para entervista id : %s",
                mensajeValidacionMatch.getIdEntrevista()));
        return this.entrevistaDao.actualizarEstadoHojaDeVida(
                        mensajeValidacionMatch.getIdEntrevista(),
                        mensajeValidacionMatch.isMatchValido()
                )
                .then(this.validadorEventosSimultaneosService.ejecutar(mensajeValidacionMatch.getIdEntrevista()))
                .flatMap(ragsIdsDto -> enviarInformacionEntrevistaAPreparador(
                        ragsIdsDto,
                        mensajeValidacionMatch.getIdEntrevista()
                ));
    }

    private Mono<Void> enviarInformacionEntrevistaAPreparador(RagsIdsDto ragsIdsDto, String idEntrevista) {

        if (ragsIdsDto != null) {
            log.info(String.format("Enviando informacion a preparador para entervista id : %s", idEntrevista));
            return this.jmsPublisherClient.generarEntrevista(SolicitudGeneracionEntrevistaDto.builder()
                            .idEntrevista(idEntrevista)
                            .idHojaDeVida(ragsIdsDto.getIdHojaDeVidaRag())
                            .username(ragsIdsDto.getUsername())
                            .idInformacionEmpresa(ragsIdsDto.getIdInformacionEmpresaRag())
                            .build())
                    .then();
        }

        return Mono.empty();

    }

    @Override
    public Mono<Void> actualizarEstadoEntrevistaPorPreguntas(EntrevistaDto entrevista) {
        if (!entrevista.getPreguntas().isEmpty()) {
            return this.entrevistaDao.actualizarEstadoEntrevista(entrevista.getIdEntrevista(), EstadoEntrevistaEnum.PG);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> actualizarEstadoEntrevistaPorFeedback(FeedbackDto feedback) {
        if (!feedback.getProcesoEntrevista().isEmpty()) {
            return this.entrevistaDao.actualizarEstadoEntrevista(feedback.getIdEntrevista(), EstadoEntrevistaEnum.FG);
        }
        return Mono.empty();
    }
}

