package jeronimo.margitic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jeronimo.margitic.model.Obra;
import jeronimo.margitic.repository.ObraRepository;

@Service
public class ObraService {

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
        try{
            clienteService.validarCliente(obra.getCliente());
            validarCoordenadas(obra.getCoordenadas());
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
    
}
