package jeronimo.margitic.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"dni", "numerotelefono", "correoElectronico"})})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id_cliente")
    private int id;
    private String nombre;
    private String apellido;
    private long dni;
    @ApiModelProperty(notes="Ingresar la fecha de nacimiento en formato DD/MM/AA")
    @Column(columnDefinition = "DATE")
    private LocalDate fechaNacimiento;
    private String calleDomicilio;
    private String numeroDomicilio;
    @ApiModelProperty(notes="Ingresar el celuar con codigo de area sin 0 y sin 15")
    @Column(length = 10)
    private String numeroTelefono;
    private String correoElectronico;
    private Float maximoDescubierto;    //Indica la cantidad maxima de dinero que puede quedar debiendo en la cuenta corriente.
    private int obrasEnEjecucion;
    private int maximoObrasEnEjecucion; //Indica la cantidad maxima de obras que un
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioHabilitado> usuariosHabilitados;    //Son los usuarios que estan habilitados a operar en nombre del cliente

}
