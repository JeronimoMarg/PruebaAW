package jeronimo.margitic.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jeronimo.margitic.model.Obra;
import jeronimo.margitic.service.ObraService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/obras")
@Api(value="ObraRest", description = "Permite gestionar obras con REST y HTTP.")
public class ObraController {
    
    @Autowired
    ObraService obraService;

    @GetMapping("/{obraId}")
    @ApiOperation(value = "Obtiene una obra por id")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Obra no obtenida correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "El id no corresponde a ninguna obra.")
    })
    public ResponseEntity<Obra> obtenerObraPorId (@PathVariable(name="obraId") int id){
        Optional<Obra> obraBuscada = obraService.obtenerObraPorId(id);
        return ResponseEntity.of(obraBuscada);
        //En este caso, el .of() retorara 200 OK si el opcional tiene un valor
        //Caso contrario retornara 404 Not Found.
    }

    @GetMapping("/todos")
    @ApiOperation(value="Obtiene todas las obras")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Obras obtenidas correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "No se obtuvieron obras.")
    })
    public ResponseEntity<List<Obra>> obtenerObrasTodas() {
        List<Obra> obras = obraService.obtenerTodas();
        if(obras.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok(obras);
        }
    }
    
    @PostMapping(path="/crear", consumes="application/json")
    @ApiOperation(value="Crea una obra")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Obra creada correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "No se pudo crear la obra (verificar datos)")
    })
    public ResponseEntity<Obra> crearObra (@RequestBody Obra obraNueva) {
        Obra obraCreada = obraService.crearObra(obraNueva);
        return ResponseEntity.status(201).body(obraCreada);
        //return ResponseEntity.ok(clienteCreado);
    }

    @PutMapping("/{id}")
    @ApiOperation(value="Actualiza una obra")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Obra actualizada correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "No se pudo actualizar la obra (verificar datos o ID).")
    })
    public ResponseEntity<Obra> actualizarObra (@PathVariable int id, @RequestBody Obra obra) {
        Optional<Obra> obraAActualizar = obraService.obtenerObraPorId(id);
        if(obraAActualizar.isPresent()){
            obraService.actualizarObra(obra);
            return ResponseEntity.ok(obra);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value="Elimina una obra")
    @ApiResponses(value = {
        @ApiResponse(code = 200 , message = "Obra eliminada correctamente."),
        @ApiResponse(code = 401 , message = "No autorizado."),
        @ApiResponse(code = 403 , message = "Prohibido"),
        @ApiResponse(code = 404 , message = "El id no corresponde a ninguna obra.")
    })
    public ResponseEntity<Obra> eliminarObra (@PathVariable int id){
        Optional<Obra> obraBorrada = obraService.obtenerObraPorId(id);
        if(obraBorrada.isPresent()){
            obraService.eliminarObra(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

}
