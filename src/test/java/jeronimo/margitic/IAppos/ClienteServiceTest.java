package jeronimo.margitic.IAppos;

import jeronimo.margitic.exception.DniExistenteException;
import jeronimo.margitic.model.Cliente;
import jeronimo.margitic.repository.ClienteRepository;
import jeronimo.margitic.service.ClienteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        Optional<Cliente> result = clienteService.obtenerClientePorId(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testObtenerClientePorDni() {
        Cliente cliente = new Cliente();
        cliente.setDni(123456789L);
        when(clienteRepository.findByDni(123456789L)).thenReturn(Optional.of(cliente));
        Optional<Cliente> result = clienteService.obtenerClientePorDni(123456789L);
        assertTrue(result.isPresent());
        assertEquals(123456789L, result.get().getDni());
    }

    @Test
    void testObtenerTodos() {
        List<Cliente> clientes = Arrays.asList(new Cliente(), new Cliente());
        when(clienteRepository.findAll()).thenReturn(clientes);
        List<Cliente> result = clienteService.obtenerTodos();
        assertEquals(2, result.size());
    }

    @Test
    void testObtenerTodosPagina() {
        List<Cliente> clientes = Arrays.asList(new Cliente(), new Cliente());
        Page<Cliente> page = new PageImpl<>(clientes);
        when(clienteRepository.findAll(any(PageRequest.class))).thenReturn(page);
        Page<Cliente> result = clienteService.obtenerTodosPagina();
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testCrearCliente() {
        Cliente cliente = getValidCliente();
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        Cliente result = clienteService.crearCliente(cliente);
        assertNotNull(result);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void testEliminarCliente() {
        clienteService.eliminarCliente(1);
        verify(clienteRepository).deleteById(1);
    }

    @Test
    void testActualizarCliente() {
        Cliente cliente = getValidCliente();
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        Cliente result = clienteService.actualizarCliente(cliente);
        assertNotNull(result);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void testValidarClienteValido() {
        Cliente cliente = getValidCliente();
        when(clienteRepository.findByDni(cliente.getDni())).thenReturn(Optional.empty());
        boolean result = clienteService.validarCliente(cliente);
        assertTrue(result);
    }

    @Test
    void testValidarClienteDniExistente() {
        Cliente cliente = getValidCliente();
        when(clienteRepository.findByDni(cliente.getDni())).thenReturn(Optional.of(cliente));
        boolean result = clienteService.validarCliente(cliente);
        assertFalse(result);
    }

    @Test
    void testValidarClienteFechaInvalida() {
        Cliente cliente = getValidCliente();
        cliente.setFechaNacimiento(LocalDate.now().plusDays(1));
        boolean result = clienteService.validarCliente(cliente);
        assertFalse(result);
    }

    @Test
    void testValidarClienteTelefonoInvalido() {
        Cliente cliente = getValidCliente();
        cliente.setNumeroTelefono("123");
        boolean result = clienteService.validarCliente(cliente);
        assertFalse(result);
    }

    @Test
    void testValidarClienteCorreoInvalido() {
        Cliente cliente = getValidCliente();
        cliente.setCorreoElectronico("correo-invalido");
        boolean result = clienteService.validarCliente(cliente);
        assertFalse(result);
    }

    @Test
    void testActualizarMaximoDescubierto() {
        Cliente cliente = new Cliente();
        cliente.setMaximoDescubierto(1000f);
        Float result = clienteService.actualizarMaximoDescubierto(cliente, 200f);
        assertEquals(800f, result);
    }

    @Test
    void testActualizarObrasEnEjecucion() {
        Cliente cliente = new Cliente();
        cliente.setObrasEnEjecucion(2);
        clienteService.actualizarObrasEnEjecucion(cliente, 3);
        assertEquals(5, cliente.getObrasEnEjecucion());
    }

    private Cliente getValidCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setDni(123456789L);
        cliente.setFechaNacimiento(LocalDate.now().minusYears(20));
        cliente.setNumeroTelefono("1234567890");
        cliente.setCorreoElectronico("test@mail.com");
        cliente.setMaximoDescubierto(1000f);
        cliente.setObrasEnEjecucion(0);
        return cliente;
    }
}