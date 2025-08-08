package jeronimo.margitic.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.andrewoma.dexx.collection.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jeronimo.margitic.model.Cliente;
import jeronimo.margitic.service.ClienteService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/clientes")
@Api(value="ClienteRest", description = "Permite gestionar clientes por medio de comunicacion REST y protocolos HTTP.")
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8080"})
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    @GetMapping("/{clienteId}")
    @ApiOperation(value = "Obtiene un cliente por id")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Cliente obtenido correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "El id no corresponde a ningun cliente.")
    })
    public ResponseEntity<Cliente> obtenerClientePorId (@PathVariable(name="clienteId") int id){
        Optional<Cliente> clienteBuscado = clienteService.obtenerClientePorId(id);
        return ResponseEntity.of(clienteBuscado);
        //En este caso, el .of() retorara 200 OK si el opcional tiene un valor
    }

    @GetMapping("/todos")
    @ApiOperation(value="Obtiene todos los clientes")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Clientes obtenidos correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "Recurso no encontrado.")
    })
    public ResponseEntity<List<Cliente>> obtenerClienteTodos() {
        List<Cliente> clientes = clienteService.obtenerTodos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/dni/{dni}")
    @ApiOperation(value="Obtiene un cliente por numero de DNI")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Cliente obtenido correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "El dni no corresponde a ningun cliente.")
    })
    public ResponseEntity<Cliente> obtenerClientePorDni (@PathVariable long dni){
        Optional<Cliente> clienteBuscado = clienteService.obtenerClientePorDni(dni);
        return ResponseEntity.of(clienteBuscado);
    }
    
    @PostMapping(path="/crear", consumes="application/json")
    @ApiOperation(value="Crea un cliente")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Cliente creado correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "No se pudo crear el cliente (verificar datos)")
    })
    public ResponseEntity<Cliente> crearCliente (@RequestBody Cliente clienteNuevo) {
        try{
            Cliente clienteCreado = clienteService.crearCliente(clienteNuevo);
            return ResponseEntity.status(201).body(clienteCreado);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    //Habria que ponerle que consume app/json tambien? Probar
    @PutMapping("/{id}")
    @ApiOperation(value="Actualiza un cliente")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Cliente actualizado correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "No se pudo actualizar el cliente (verificar datos o ID).")
    })
    public ResponseEntity<Cliente> actualizarCliente (@PathVariable int id, @RequestBody Cliente cliente) {
        Optional<Cliente> clienteAActualizar = clienteService.obtenerClientePorId(id);
        if(clienteAActualizar.isPresent()){
            try{
                Cliente actualizado = clienteService.actualizarCliente(cliente);
                return ResponseEntity.ok(actualizado);
            }catch (Exception e){
                return ResponseEntity.badRequest().build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value="Elimina un cliente")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Cliente eliminado correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "El id no corresponde a ningun cliente.")
    })
    public ResponseEntity<Cliente> eliminarCliente (@PathVariable int id){
        Optional<Cliente> clienteBorrado = clienteService.obtenerClientePorId(id);
        if(clienteBorrado.isPresent()){
            clienteService.eliminarCliente(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/verificarSaldo/{id}")
    public ResponseEntity<Boolean> verificarSaldoCliente(@PathVariable int id, @RequestBody Map<String, Object> totalOrden) {
        Optional<Cliente> clienteBuscado = clienteService.obtenerClientePorId(id);
        if (clienteBuscado.isPresent()) {
            float total = Float.valueOf(totalOrden.get("total").toString());
            boolean respuesta = clienteService.tieneSaldoParaOrden(clienteBuscado.get(), total);
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    

    
    

    
}
