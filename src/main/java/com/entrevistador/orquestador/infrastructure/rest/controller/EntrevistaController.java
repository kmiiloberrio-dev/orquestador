package com.entrevistador.orquestador.infrastructure.rest.controller;

import com.entrevistador.orquestador.application.dto.EstadoEntrevistaDto;
import com.entrevistador.orquestador.application.dto.FormularioDto;
import com.entrevistador.orquestador.application.usescases.SolicitudEntrevista;
import com.entrevistador.orquestador.infrastructure.adapter.constants.ValidationsMessagesData;
import com.entrevistador.orquestador.infrastructure.adapter.mapper.EntrevistaMapper;
import com.entrevistador.orquestador.infrastructure.adapter.util.SanitizeStringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping("/v1/entrevistadores")
@RequiredArgsConstructor
@Validated
public class EntrevistaController {
    private final SolicitudEntrevista solicitudEntrevista;
    private final EntrevistaMapper    mapper;

    @PostMapping(value = "/solicitudes-entrevistas")
    public Mono<ResponseEntity<String>> crearSolicitudEntrevista(
            @NotNull(message = ValidationsMessagesData.NOT_NULL_MESSAGE) @RequestParam String username,
            @Valid @RequestBody FormularioDto formularioDto
    ) {
        return Mono.just(this.mapper.mapFormularioDtoToFormulario(formularioDto))
                .flatMap(formulario ->
                        this.solicitudEntrevista.generarSolicitudEntrevista(SanitizeStringUtil.sanitize(username), formulario))
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                        .body("Archivo PDF cargado con exito")));
    }

    @GetMapping(value = "/{id}")
    public Mono<EstadoEntrevistaDto> obtenerEstadoEntrevistaPorId(@PathVariable String id) {
        return this.solicitudEntrevista.obtenerEstadoEntrevistaPorId(SanitizeStringUtil.sanitize(id))
                .map(this.mapper::mapEstadoEntrevistaToEstadoEntrevistaDto);
    }

    @GetMapping()
    public Mono<EstadoEntrevistaDto> obtenerEstadoEntrevistaPorUsuario(@RequestParam String username) {
        return this.solicitudEntrevista.obtenerEstadoEntrevistaPorUsuario(SanitizeStringUtil.sanitize(username))
                .map(this.mapper::mapEstadoEntrevistaToEstadoEntrevistaDto);
    }
}