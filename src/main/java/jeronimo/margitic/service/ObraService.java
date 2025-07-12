package jeronimo.margitic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jeronimo.margitic.exception.MaximoDescubiertoExcedidoException;
import jeronimo.margitic.exception.MaximoObrasEnEjecucionException;
import jeronimo.margitic.model.Cliente;
import jeronimo.margitic.model.EstadoObra;
import jeronimo.margitic.model.Obra;
import jeronimo.margitic.repository.ObraRepository;

@Service
public class ObraService {

    @Value("${MAXIMO_DESCUBIERTO}")
    private Float maximoDescubiertoPermitido;
    //private Float maximoDescubiertoPermitido = Float.parseFloat(maximoDescubiertoString);

    @Autowired
    ObraRepository obraRepository;
    @Autowired
    ClienteService clienteService;

    public Optional<Obra> obtenerObraPorId(int id) {
        return obraRepository.findById(id);
    }

    public List<Obra> obtenerTodas() {
        return obraRepository.findAll();
    }

    public Obra crearObra(Obra obra){
        // Poner el estado de la obra fijandose en Cliente
        validarObra(obra);
        Obra obraNueva = obraRepository.save(obra);
        return obraNueva;
    }

    public void eliminarObra(int id) {
        obraRepository.deleteById(id);
    }

    public Obra actualizarObra(Obra obra) {
        validarObra(obra);
        Obra obraActualizada = obraRepository.save(obra);
        return obraActualizada;
    }

    private boolean validarObra(Obra obra) {
        boolean respuesta = false;
        obra.setEstadoObra(EstadoObra.PENDIENTE);;
        try{
            verificarMaximoObrasEnEjecucion(obra);
            verificarMaximoDescubierto(obra);
            validarCoordenadas(obra.getCoordenadas());
            obra.setEstadoObra(EstadoObra.HABILITADA);
            respuesta = true;
        }
        catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return respuesta;
    }

    private boolean validarCoordenadas(String coordenadas) {
        if(coordenadas == null || coordenadas.isEmpty()) {
            throw new IllegalArgumentException("Las coordenadas no pueden ser nulas o vacías.");
        }else if (!coordenadas.matches("\\[(\\+|\\-|)(([0-8]\\d?)(\\.\\d+)?|90(\\.0+)?)\\,(\\+|\\-|)((\\d?\\d|1[0-7]\\d)(\\.\\d+)?|180(\\.0+)?)\\]")){
            throw new IllegalArgumentException("Las coordenadas no tienen el formato correcto.");
        }
        return true;
    }

    private void verificarMaximoDescubierto(Obra obra) throws MaximoDescubiertoExcedidoException {
        if (obra.getCliente().getMaximoDescubierto() - obra.getPresupuestoEstimado() < maximoDescubiertoPermitido) {
            throw new MaximoDescubiertoExcedidoException("La obra excede el maximo descubierto permitido para el cliente.");
        }
    }

    public void verificarMaximoObrasEnEjecucion(Obra obra) throws MaximoObrasEnEjecucionException{
        Cliente cliente = obra.getCliente();
        if (cliente.getMaximoObrasEnEjecucion() + 1 > cliente.getMaximoObrasEnEjecucion()) {
            throw new MaximoObrasEnEjecucionException("El cliente ha alcanzado el máximo de obras en ejecución permitidas.");
        }
    }

    public Obra asignarObra(Obra obra){
        if (obra.getEstadoObra() == EstadoObra.HABILITADA) {
            clienteService.actualizarMaximoDescubierto(obra.getCliente(), obra.getPresupuestoEstimado());
            clienteService.actualizarObrasEnEjecucion(obra.getCliente(), 1);
        }
        obraRepository.save(obra);
        return obra;
    }

    public void finalizarObra(Obra obra) {
        obra.setEstadoObra(EstadoObra.FINALIZADA);
        clienteService.actualizarObrasEnEjecucion(obra.getCliente(), -1);
        obraRepository.save(obra);
    }

    private void habilitarObra(Cliente cliente) {
        Obra obraPendiente = obtenerTodas().stream().filter(o -> o.getEstadoObra() == EstadoObra.PENDIENTE).filter(o -> o.getCliente().equals(cliente)).findFirst().orElse(null);
        try{
            if (obraPendiente != null) {
                verificarMaximoObrasEnEjecucion(obraPendiente);
                verificarMaximoDescubierto(obraPendiente);
                obraPendiente.setEstadoObra(EstadoObra.HABILITADA);
                asignarObra(obraPendiente);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void habilitarObraEspecifica(Obra obra) {
        try {
            verificarMaximoObrasEnEjecucion(obra);
            verificarMaximoDescubierto(obra);
            obra.setEstadoObra(EstadoObra.HABILITADA);
            asignarObra(obra);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void pendienteObra(Obra obra) {
        obra.setEstadoObra(EstadoObra.PENDIENTE);
        obraRepository.save(obra);
    }
    
}
