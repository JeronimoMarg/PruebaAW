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

    //Obtiene una obra acorde al id.
    public Optional<Obra> obtenerObraPorId(int id) {
        return obraRepository.findById(id);
    }

    //Obtiene todas las obras.
    public List<Obra> obtenerTodas() {
        return obraRepository.findAll();
    }

    //Crea una obra.
    public Obra crearObra(Obra obra) throws Exception{
        //Se validan los datos de la obra.
        validarObra(obra);
        //Se guarda en la BD.
        Obra obraNueva = obraRepository.save(obra);
        return obraNueva;
    }

    //Elimina una obra segun id.
    public void eliminarObra(int id) {
        obraRepository.deleteById(id);
    }

    //Actualiza una obra.
    public Obra actualizarObra(Obra obra) throws Exception{
        //Se validan los datos de la obra.
        validarObra(obra);
        //Se guarda en la BD.
        Obra obraActualizada = obraRepository.save(obra);
        return obraActualizada;
    }

    //Validacion de una obra
    private boolean validarObra(Obra obra) throws Exception {
        boolean respuesta = false;
        //Antes de la validacion una obra esta PENDIENTE.
        pendienteObra(obra);
        try{
            verificarMaximoObrasEnEjecucion(obra);
            verificarMaximoDescubierto(obra);
            validarCoordenadas(obra.getCoordenadas());
            clienteService.validarCliente(obra.getCliente());
            respuesta = true;
        }
        catch(Exception e){
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
        return respuesta;
    }

    //Valida las coordenadas de una obra [lat, long]
    private boolean validarCoordenadas(String coordenadas) {
        if(coordenadas == null || coordenadas.isEmpty()) {
            throw new IllegalArgumentException("Las coordenadas no pueden ser nulas o vacías.");
        }else if (!coordenadas.matches("\\[(\\+|\\-|)(([0-8]\\d?)(\\.\\d+)?|90(\\.0+)?)\\,(\\+|\\-|)((\\d?\\d|1[0-7]\\d)(\\.\\d+)?|180(\\.0+)?)\\]")){
            throw new IllegalArgumentException("Las coordenadas no tienen el formato correcto.");
        }
        return true;
    }

    //Valida las que el maximoDescubierto sea suficiente para el presupuestoEstimado de la obra
    private void verificarMaximoDescubierto(Obra obra) throws MaximoDescubiertoExcedidoException {
        if (obra.getCliente().getMaximoDescubierto() - obra.getPresupuestoEstimado() < maximoDescubiertoPermitido) {
            throw new MaximoDescubiertoExcedidoException("La obra excede el maximo descubierto permitido para el cliente.");
        }
    }

    //Valida que un cliente no exceda la cantidad de obras que tiene permitidas
    public void verificarMaximoObrasEnEjecucion(Obra obra) throws MaximoObrasEnEjecucionException{
        Cliente cliente = obra.getCliente();
        if (cliente.getMaximoObrasEnEjecucion() + 1 > cliente.getMaximoObrasEnEjecucion()) {
            throw new MaximoObrasEnEjecucionException("El cliente ha alcanzado el máximo de obras en ejecución permitidas.");
        }
    }

    //Se asigna una obra a un cliente
    public Obra asignarObra(Obra obra){
        if (obra.getEstadoObra() == EstadoObra.HABILITADA) {
            //Se actualiza el maximo descubierto (Se puede sacar)
            clienteService.actualizarMaximoDescubierto(obra.getCliente(), obra.getPresupuestoEstimado());
            //Se suma una obra en las obrasEnEjecucion del cliente
            clienteService.actualizarObrasEnEjecucion(obra.getCliente(), 1);
        }
        //Se guardan los cambios en la BD -- Opcional
        //obraRepository.save(obra);
        return obra;
    }

    //Se finaliza una obra
    public Obra finalizarObra(Obra obra) {
        obra.setEstadoObra(EstadoObra.FINALIZADA);
        //Se decrementa las obras en ejecucion del cliente.
        clienteService.actualizarObrasEnEjecucion(obra.getCliente(), -1);
        //Habilitar otra obra del cliente
        habilitarObra(obra.getCliente());
        //Se guardan los cambios -- Opcional
        //obraRepository.save(obra);
        return obra;
    }

    //Se habilita una obra para un cliente determinado.
    //Metodo usado cuando se finaliza una obra de un cliente.
    private Optional<Obra> habilitarObra(Cliente cliente) {
        //Se obtiene la primer obra en estado pendiente del cliente en cuestion.
        Optional<Obra> obraPendiente = obtenerTodas().stream().filter(o -> o.getEstadoObra() == EstadoObra.PENDIENTE).filter(o -> o.getCliente().equals(cliente)).findFirst();
        try{
            //Si la obra buscada existe entonces se verifica y se habilita.
            if (obraPendiente.isPresent()) {
                verificarMaximoObrasEnEjecucion(obraPendiente.get());
                verificarMaximoDescubierto(obraPendiente.get());
                obraPendiente.get().setEstadoObra(EstadoObra.HABILITADA);
                asignarObra(obraPendiente.get());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            pendienteObra(obraPendiente.get());
        }
        return obraPendiente;
    }

    //En este caso en vez de buscar una obra para un cliente, se pasa una obra en especifico.
    private Obra habilitarObraEspecifica(Obra obra) {
        try {
            //Se verifica y se habilita.
            verificarMaximoObrasEnEjecucion(obra);
            verificarMaximoDescubierto(obra);
            obra.setEstadoObra(EstadoObra.HABILITADA);
            asignarObra(obra);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            pendienteObra(obra);
        }
        return obra;
    }

    //
    public Obra pendienteObra(Obra obra) {
        obra.setEstadoObra(EstadoObra.PENDIENTE);
        //Se guardan los cambios -- Opcional
        //obraRepository.save(obra);
        return obra;
    }
    
}
