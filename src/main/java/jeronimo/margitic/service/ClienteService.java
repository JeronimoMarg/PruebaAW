package jeronimo.margitic.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jeronimo.margitic.exception.*;
import jeronimo.margitic.model.Cliente;
import jeronimo.margitic.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;
    
    public Optional<Cliente> obtenerClientePorId(int id){
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> obtenerClientePorDni(long dni){
        return clienteRepository.findByDni(dni);
    }

    public List<Cliente> obtenerTodos(){
        return clienteRepository.findAll();
    }

    public Page<Cliente> obtenerTodosPagina(){
        Page<Cliente> paginasCliente = clienteRepository.findAll(PageRequest.of(1,5));
        /* Opcionalmente, se puede pasar a DTO
        if(paginasCliente.hasContent()){
            List<ClienteDTO> lista = paginasCliente.stream().map(m->new ClienteDTO()).collect(Collectors.toList());
        }
        */
        return paginasCliente;
    }

    public Cliente crearCliente(Cliente cliente) {
        validarCliente(cliente);
        Cliente clienteNuevo = clienteRepository.save(cliente);
        return clienteNuevo;
    }

    public void eliminarCliente(int id){
        clienteRepository.deleteById(id);
    }

    public Cliente actualizarCliente(Cliente cliente) {
        validarCliente(cliente);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return clienteActualizado;
    }

    public boolean validarCliente(Cliente cliente){
        boolean respuesta = false;
        try{
            validarDni(cliente.getDni());
            validarFechaNacimiento(cliente.getFechaNacimiento());
            validarNumeroTelefono(cliente.getNumeroTelefono());
            validarCorreoElectronico(cliente.getCorreoElectronico());
            respuesta = true;
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return respuesta;
    }

    private boolean validarCorreoElectronico(String correoElectronico) throws IllegalArgumentException {
        if(correoElectronico == null || correoElectronico.isEmpty()) {
            throw new IllegalArgumentException("El correo electronico no puede ser nulo o vacio.");
        } else if (!correoElectronico.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("El correo electronico no corresponde al formato adecuado.");
        }
        return true;
    }

    private boolean validarDni(long dni) throws IllegalArgumentException, DniExistenteException{
        if(String.valueOf(dni).length() <= 8){
            throw new IllegalArgumentException("El dni debe corresponder a un numero valido.");
        }else if(obtenerClientePorDni(dni).isPresent()){
            throw new DniExistenteException("El dni " + dni + " ya corresponde a un cliente guardado.");
        }
        return true;
    }

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

    private boolean validarNumeroTelefono(String numeroTelefono) throws IllegalArgumentException{
        if (numeroTelefono.isEmpty() || numeroTelefono == null){
            throw new IllegalArgumentException("El numero de telefono no puede ser nulo o vacio.");
        }else if(!numeroTelefono.matches("\\d{10}")){
            throw new IllegalArgumentException("El numero de telefono no corresponde al formato adecuado.");
        }
        return true;
    }

}
