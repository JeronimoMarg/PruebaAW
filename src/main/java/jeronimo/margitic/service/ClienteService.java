package jeronimo.margitic.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.andrewoma.dexx.collection.Map;

import jeronimo.margitic.exception.*;
import jeronimo.margitic.model.Cliente;
import jeronimo.margitic.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    private RestTemplate restTemplate;
    
    private final String url_pedidos = "http:///pedido-service:8080/api/pedidos";

    //Obtiene un cliente acorde a un id.
    public Optional<Cliente> obtenerClientePorId(int id){
        return clienteRepository.findById(id);
    }

    //Obtiene un cliente acorde a un dni.
    public Optional<Cliente> obtenerClientePorDni(long dni){
        return clienteRepository.findByDni(dni);
    }

    //Obtiene todos los clientes.
    public List<Cliente> obtenerTodos(){
        return clienteRepository.findAll();
    }

    //Obtiene todos los clientes pero retorna una pagina.
    public Page<Cliente> obtenerTodosPagina(){
        Page<Cliente> paginasCliente = clienteRepository.findAll(PageRequest.of(1,5));
        /* Opcionalmente, se puede pasar a DTO
        if(paginasCliente.hasContent()){
            List<ClienteDTO> lista = paginasCliente.stream().map(m->new ClienteDTO()).collect(Collectors.toList());
        }
        */
        return paginasCliente;
    }

    //Crea un cliente.
    public Cliente crearCliente(Cliente cliente) throws Exception{
        //Primero, validar los datos del cliente.
        validarCliente(cliente);
        //Guardar en BD.
        Cliente clienteNuevo = clienteRepository.save(cliente);
        return clienteNuevo;
    }

    //Elimina un cliente segun un id.
    public void eliminarCliente(int id){
        clienteRepository.deleteById(id);
    }

    //Actualiza un cliente.    
    public Cliente actualizarCliente(Cliente cliente) throws Exception{
        //Primero se validan los datos.
        validarCliente(cliente);
        //Guardar en BD.
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return clienteActualizado;
    }

    //Validacion de los datos de cliente.
    public boolean validarCliente(Cliente cliente) throws Exception{
        boolean respuesta = false;
        try{
            validarDni(cliente.getDni());
            validarFechaNacimiento(cliente.getFechaNacimiento());
            validarNumeroTelefono(cliente.getNumeroTelefono());
            validarCorreoElectronico(cliente.getCorreoElectronico());
            respuesta = true;
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
            throw(e);
        }
        return respuesta;
    }

    //Validacion de formato de correo electronico.
    private boolean validarCorreoElectronico(String correoElectronico) throws IllegalArgumentException {
        if(correoElectronico == null || correoElectronico.isEmpty()) {
            throw new IllegalArgumentException("El correo electronico no puede ser nulo o vacio.");
        } else if (!correoElectronico.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("El correo electronico no corresponde al formato adecuado.");
        }
        return true;
    }

    //Validacion de dni (cantidad de digitos)
    private boolean validarDni(long dni) throws IllegalArgumentException, DniExistenteException{
        if(String.valueOf(dni).length() <= 8){
            throw new IllegalArgumentException("El dni debe corresponder a un numero valido.");
        }else if(obtenerClientePorDni(dni).isPresent()){
            throw new DniExistenteException("El dni " + dni + " ya corresponde a un cliente guardado.");
        }
        return true;
    }

    //Validacion de fecha de nacimiento
    private boolean validarFechaNacimiento(LocalDate fecha) throws IllegalArgumentException{
        LocalDate ahora = LocalDate.now();
        Period anios = Period.between(fecha, ahora);
        if(fecha == null){
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }else if (fecha.isAfter(ahora) && anios.getYears() < 18){
            throw new IllegalArgumentException("La fecha no puede ser despues que hoy y el cliente debe tener mayor de 18 años.");
        }
        return true;
    }

    //Validacion de numero de telefono (cantidad digitos)
    private boolean validarNumeroTelefono(String numeroTelefono) throws IllegalArgumentException{
        if (numeroTelefono.isEmpty() || numeroTelefono == null){
            throw new IllegalArgumentException("El numero de telefono no puede ser nulo o vacio.");
        }else if(!numeroTelefono.matches("\\d{10}")){
            throw new IllegalArgumentException("El numero de telefono no corresponde al formato adecuado.");
        }
        return true;
    }

    //Actualiza el maximo descubierto de un cliente segun el presupuesto estimado de la obra.
    //Puede no usarse ya que el maximo descubierto cambia con un pedido.
    public Float actualizarMaximoDescubierto(Cliente cliente, Float presupuestoEstimado) {
        cliente.setMaximoDescubierto(cliente.getMaximoDescubierto() - presupuestoEstimado);
        return cliente.getMaximoDescubierto();
    }

    //Actualiza la cantidad de obras en ejecucion.
    //Si el numero es 1, entonces agrega una obra.
    //Si el numero es -1, entonces saca una obra.
    public void actualizarObrasEnEjecucion(Cliente cliente, int num) {
        cliente.setObrasEnEjecucion(cliente.getObrasEnEjecucion() + num);
    }

    //Verifica que el cliente tenga saldo para un Pedido.
    public boolean tieneSaldoParaOrden(Cliente cliente, float totalOrden){
        //Un cliente tiene saldo disponible si el monto de todos los pedidos que no fueron entregados o rechazados-
        //-mas el monto del pedido actual, no superan el maximo descubierto del cliente.

        //Obtener todos los pedidos asociados al cliente
        ResponseEntity<List<Map<String, Object>>> respuesta = consultaRESTPedidosCliente(cliente.getId());
        List<Map<String, Object>> ordenes = respuesta.getBody();

        float total = 0;

        if(ordenes != null){
            for (Map<String, Object> orden: ordenes){
                //Se suman todos los valores de la orden al total.
                Float aux = Float.valueOf(orden.get("total").toString());
                total += aux;
            }
        }
        total += totalOrden;
        
        return (cliente.getMaximoDescubierto() >= total);
    }

    //Consulta via endpoint REST todos los pedidos de un cliente.
    private ResponseEntity<List<Map<String, Object>>> consultaRESTPedidosCliente(int id_cliente){
        
        String url = url_pedidos + "/cliente/" + String.valueOf(id_cliente);

        ResponseEntity<List<Map<String, Object>>> respuesta = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            null,
            new org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return respuesta;
    }
}
