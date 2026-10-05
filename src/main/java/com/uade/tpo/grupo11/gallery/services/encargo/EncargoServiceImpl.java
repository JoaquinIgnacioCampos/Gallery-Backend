package com.uade.tpo.grupo11.gallery.services.encargo;

import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.*;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.exceptions.*;
import com.uade.tpo.grupo11.gallery.repositories.*;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Logica de negocio de los encargos: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class EncargoServiceImpl implements EncargoService {

    @Autowired
    private EncargoRepository encargoRepository;
    @Autowired
    private PerfilArtistaRepository artistaRepository;
    @Autowired
    private TamanioLienzoRepository tamanioLienzoRepository;
    @Autowired
    private MarcoRepository marcoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private CompraRepository compraRepository;

    // Busca el encargo por id. Solo pueden verlo el cliente y el artista del encargo.
    @Override
    public Encargo getEncargoById(Long id, Usuario usuarioLogueado) {
        Encargo encargo = encargoRepository.findById(id)
                .orElseThrow(() -> new EncargoNotFoundException(id));

        OwnershipGuard.verificar(usuarioLogueado,
                encargo.getUsuario().getId(), encargo.getArtista().getUsuario().getId());

        return encargo;
    }

    // Devuelve los encargos del artista. Solo el propio artista (o ADMIN) puede listarlos.
    @Override
    public List<Encargo> getEncargosByArtista(Long artistaId, Usuario usuarioLogueado) {
        PerfilArtista artista = artistaRepository.findById(artistaId)
                .orElseThrow(() -> new PerfilArtistaNotFoundException(artistaId));

        OwnershipGuard.verificar(usuarioLogueado, artista.getUsuario().getId());

        List<Encargo> encargos = encargoRepository.findByArtistaId(artistaId);
        if (encargos.isEmpty()) {
            throw new RecursoNoEncontradoException("El artista con id " + artistaId + " no tiene encargos registrados");
        }
        return encargos;
    }

    // Devuelve los encargos del usuario. Solo el propio usuario (o ADMIN) puede listarlos.
    @Override
    public List<Encargo> getEncargosByUsuario(Long usuarioId, Usuario usuarioLogueado) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        OwnershipGuard.verificar(usuarioLogueado, usuarioId);

        List<Encargo> encargos = encargoRepository.findByUsuarioId(usuarioId);
        if (encargos.isEmpty()) {
            throw new RecursoNoEncontradoException("El usuario con id " + usuarioId + " no tiene encargos registrados");
        }
        return encargos;
    }

    // Crea el encargo con los datos del request. El cliente sale del usuario logueado,
    // no del body: si viniera ahi, cualquiera podria pedir un encargo a nombre de otro.
    //Ahora tampoco un artista se puede hacer un encargo a si mismo

    //Habria que modificar los accesos para que un artista fije el precio que se alla charlado por mensaje con el cliene,
    // pero le debe aparecer al cliene una aviso de cuanto va a pagar antes de hacer la compra o algo por el estilo
    @Override
    public Encargo createEncargo(EncargoRequest request, Usuario usuarioLogueado) {
        PerfilArtista artista = artistaRepository.findById(request.getArtista_id())
                .orElseThrow(() -> new PerfilArtistaNotFoundException(request.getArtista_id()));

        if (!artista.isAcepta_encargos()) {
            throw new PerfilArtistaNoAceptaEncargosException(artista.getId());
        }

        // No se puede pedir un encargo a uno mismo.
        if (artista.getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new AutoencargoNoPermitidoException(usuarioLogueado.getId());
        }

        TamanioLienzo tamanio = tamanioLienzoRepository.findById(request.getTamanio_id())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getTamanio_id()));
        Marco marco = marcoRepository.findById(request.getMarco_id())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarco_id()));

        Encargo encargo = new Encargo();
        encargo.setArtista(artista);
        encargo.setUsuario(usuarioLogueado);
        encargo.setTamanio(tamanio);
        encargo.setMarco(marco);
        encargo.setTipo_pintura(request.getTipo_pintura());
        encargo.setTipo_lienzo(request.getTipo_lienzo());
        encargo.setEstado_encargo(EstadoEncargo.PENDIENTE);
        encargo.setDescripcion_encargo(request.getDescripcion_encargo());

        return encargoRepository.save(encargo);
    }

    // El cliente paga el encargo terminado. El pago es simulado desde el front, pero el paso a PAGADO lo hace el back.
    @Override
    @Transactional
    public Encargo pagarEncargo(Long encargoId, Usuario usuarioLogueado) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        if (!encargo.getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new AccesoDenegadoException("El encargo con id " + encargoId + " no te pertenece");
        }

        if (encargo.getEstado_encargo() != EstadoEncargo.TERMINADO) {
            throw new TransicionEstadoInvalidaException(encargo.getEstado_encargo(), EstadoEncargo.PAGADO);
        }

        encargo.setEstado_encargo(EstadoEncargo.PAGADO);
        return encargoRepository.save(encargo);
    }

    // El artista mueve el encargo al estado siguiente si la transicion es valida. Tanto el cliente como el Artista pueden cancelar el encargo.
    // Al terminar el encargo se emite su factura, con el precio congelado al aprobarlo.
    @Override
    @Transactional
    public Encargo cambiarEstado(Long encargoId, EstadoEncargo nuevoEstado, Usuario usuarioLogueado) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        boolean esArtistaDelEncargo = encargo.getArtista().getUsuario().getId().equals(usuarioLogueado.getId());
        boolean esClienteDelEncargo = encargo.getUsuario().getId().equals(usuarioLogueado.getId());
        boolean esAdmin = usuarioLogueado.getRol_usuario() == com.uade.tpo.grupo11.gallery.entities.enums.Rol.ADMIN;

        if (!esArtistaDelEncargo && !esClienteDelEncargo && !esAdmin) {
            throw new AccesoDenegadoException("El encargo con id " + encargoId + " no te pertenece");
        }

        // El cliente solo puede cancelar; el resto de las transiciones son privativas del artista (o ADMIN).
        if (esClienteDelEncargo && !esArtistaDelEncargo && !esAdmin && nuevoEstado != EstadoEncargo.CANCELADO) {
            throw new AccesoDenegadoException("Solo el artista puede mover el encargo a este estado");
        }

        EstadoEncargo estadoActual = encargo.getEstado_encargo();

        if (!transicionesValidas(estadoActual).contains(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(estadoActual, nuevoEstado);
        }

        encargo.setEstado_encargo(nuevoEstado);
        if (nuevoEstado == EstadoEncargo.TERMINADO) {
            generarFactura(encargo);
        }
        return encargoRepository.save(encargo);
    }

    // Solo el artista del encargo (o ADMIN) carga el precio, y solo mientras el encargo siga pendiente.
    @Override
    public Encargo definirPrecio(Long encargoId, java.math.BigDecimal precio, Usuario usuarioLogueado) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        boolean esArtistaDelEncargo = encargo.getArtista().getUsuario().getId().equals(usuarioLogueado.getId());
        if (!esArtistaDelEncargo) {
            OwnershipGuard.soloAdmin(usuarioLogueado);
        }

        verificarPrecioEditable(encargo);

        encargo.setPrecio_acordado(precio);
        return encargoRepository.save(encargo);
    }

    // Solo el cliente del encargo puede aceptar el precio: ni el artista ni ADMIN aceptan por el cliente.
    // Aceptar aprueba el encargo y congela el precio.
    @Override
    public Encargo aceptarPrecio(Long encargoId, Usuario usuarioLogueado) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        if (!encargo.getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new AccesoDenegadoException("El encargo con id " + encargoId + " no te pertenece");
        }

        verificarPrecioEditable(encargo);

        if (encargo.getPrecio_acordado() == null) {
            throw new PrecioEncargoNoDefinidoException(encargoId);
        }

        encargo.setEstado_encargo(EstadoEncargo.APROBADO);
        return encargoRepository.save(encargo);
    }

    // El precio se negocia solo mientras el encargo esta pendiente: al aprobarlo queda congelado.
    private void verificarPrecioEditable(Encargo encargo) {
        if (encargo.getEstado_encargo() == EstadoEncargo.CANCELADO) {
            throw new EncargoCanceladoException(encargo.getId());
        }
        if (encargo.getEstado_encargo() != EstadoEncargo.PENDIENTE) {
            throw new EncargoCerradoPrecioException(encargo.getId());
        }
    }

    // La factura se emite al terminar el encargo, con el precio congelado al aprobarlo.
    private void generarFactura(Encargo encargo) {
        java.math.BigDecimal precioFinal = encargo.getPrecio_acordado();

        Compra compra = Compra.builder()
                .usuario(encargo.getUsuario())
                .fecha_compra(java.time.LocalDateTime.now())
                .total_compra(precioFinal)
                .build();
        compra = compraRepository.save(compra);

        Factura factura = Factura.builder()
                .artista(encargo.getArtista())
                .compra(compra)
                .detalle_factura("Encargo #" + encargo.getId() + ": " + encargo.getDescripcion_encargo())
                .precio_total_factura(precioFinal)
                .fecha_creacion_factura(java.time.LocalDateTime.now())
                .build();
        encargo.setFactura(facturaRepository.save(factura));
    }

    // Dice a que estados se puede pasar desde el actual. Volver a PENDIENTE reabre la negociacion del precio. TERMINADO pasa a PAGADO solo con el pago; PAGADO y CANCELADO son finales.
    private List<EstadoEncargo> transicionesValidas(EstadoEncargo estadoActual) {
        return switch (estadoActual) {
            case PENDIENTE -> List.of(EstadoEncargo.CANCELADO);
            case APROBADO -> List.of(EstadoEncargo.EN_PROCESO, EstadoEncargo.PENDIENTE, EstadoEncargo.CANCELADO);
            case EN_PROCESO -> List.of(EstadoEncargo.TERMINADO, EstadoEncargo.PENDIENTE, EstadoEncargo.CANCELADO);
            case TERMINADO, PAGADO, CANCELADO -> List.of();
        };
    }

}
