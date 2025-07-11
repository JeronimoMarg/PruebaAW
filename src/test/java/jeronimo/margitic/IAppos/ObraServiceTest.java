package jeronimo.margitic.IAppos;

import jeronimo.margitic.model.*;
import jeronimo.margitic.repository.ObraRepository;
import jeronimo.margitic.service.ClienteService;
import jeronimo.margitic.service.ObraService;
import jeronimo.margitic.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObraServiceTest {

    @Mock
    private ObraRepository obraRepository;
    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ObraService obraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Si usas variables de entorno, puedes setearlas aquí o mockear Dotenv si es necesario
    }

    @Test
    void testObtenerObraPorId() {
        Obra obra = new Obra();
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        Optional<Obra> result = obraService.obtenerObraPorId(1);
        assertTrue(result.isPresent());
        assertEquals(obra, result.get());
    }

    @Test
    void testObtenerTodas() {
        List<Obra> obras = Arrays.asList(new Obra(), new Obra());
        when(obraRepository.findAll()).thenReturn(obras);
        List<Obra> result = obraService.obtenerTodas();
        assertEquals(2, result.size());
    }

    @Test
    void testCrearObra() {
        Obra obra = mock(Obra.class);
        when(obraRepository.save(obra)).thenReturn(obra);
        Obra result = obraService.crearObra(obra);
        assertEquals(obra, result);
    }

    @Test
    void testEliminarObra() {
        obraService.eliminarObra(1);
        verify(obraRepository, times(1)).deleteById(1);
    }

    @Test
    void testActualizarObra() {
        Obra obra = mock(Obra.class);
        when(obraRepository.save(obra)).thenReturn(obra);
        Obra result = obraService.actualizarObra(obra);
        assertEquals(obra, result);
    }

    @Test
    void testAsignarObra() {
        Obra obra = mock(Obra.class);
        Cliente cliente = mock(Cliente.class);
        when(obra.getEstadoObra()).thenReturn(EstadoObra.HABILITADA);
        when(obra.getCliente()).thenReturn(cliente);
        when(obraRepository.save(obra)).thenReturn(obra);

        Obra result = obraService.asignarObra(obra);

        verify(clienteService).actualizarMaximoDescubierto(any(), any());
        verify(clienteService).actualizarObrasEnEjecucion(any(), eq(1));
        verify(obraRepository).save(obra);
        assertEquals(obra, result);
    }

    @Test
    void testFinalizarObra() {
        Obra obra = mock(Obra.class);
        Cliente cliente = mock(Cliente.class);
        when(obra.getCliente()).thenReturn(cliente);

        obraService.finalizarObra(obra);

        verify(obra).setEstadoObra(EstadoObra.FINALIZADA);
        verify(clienteService).actualizarObrasEnEjecucion(cliente, -1);
        verify(obraRepository).save(obra);
    }

    @Test
    void testPendienteObra() {
        Obra obra = mock(Obra.class);
        obraService.pendienteObra(obra);
        verify(obra).setEstadoObra(EstadoObra.PENDIENTE);
        verify(obraRepository).save(obra);
    }

    // Puedes agregar más tests para los métodos privados usando ReflectionTestUtils si es necesario,
    // o probarlos indirectamente a través de los métodos públicos.

}