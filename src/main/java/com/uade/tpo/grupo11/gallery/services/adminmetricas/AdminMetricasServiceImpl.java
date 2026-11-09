package com.uade.tpo.grupo11.gallery.services.adminmetricas;

import com.uade.tpo.grupo11.gallery.controllers.adminmetricas.AdminMetricasResponse;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.EncargoRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class AdminMetricasServiceImpl implements AdminMetricasService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private EncargoRepository encargoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObraRepository obraRepository;


    @Override
    public AdminMetricasResponse obtenerMetricas() {

        // Inicio y fin del día actual.
        LocalDate hoy = LocalDate.now();

        LocalDateTime desde = hoy.atStartOfDay();
        LocalDateTime hasta = hoy.plusDays(1).atStartOfDay();


        long ventasTotales = compraRepository.count();

        long ventasHoy =
                compraRepository.contarComprasEntre(
                        desde,
                        hasta
                );


        BigDecimal ingresosTotales =
                compraRepository.sumarIngresosTotales();

        BigDecimal ingresosHoy =
                compraRepository.sumarIngresosEntre(
                        desde,
                        hasta
                );


        long encargosTotales =
                encargoRepository.count();

        // PAGADO y CANCELADO son estados finales.
        long encargosActivos =
                encargoRepository.contarActivos(
                        List.of(
                                EstadoEncargo.PAGADO,
                                EstadoEncargo.CANCELADO
                        )
                );


        long usuariosRegistrados =
                usuarioRepository.count();

        long obrasPublicadas =
                obraRepository.countPublicadas();


        return new AdminMetricasResponse(
                ventasTotales,
                ventasHoy,
                ingresosTotales,
                ingresosHoy,
                encargosTotales,
                encargosActivos,
                usuariosRegistrados,
                obrasPublicadas
        );
    }
}