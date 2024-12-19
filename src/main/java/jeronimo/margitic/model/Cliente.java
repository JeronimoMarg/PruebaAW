package jeronimo.margitic.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"dni", "numerotelefono"})})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id_cliente")
    int id;
    String nombre;
    String apellido;
    long dni;
    @ApiModelProperty(notes="Ingresar la fecha de nacimiento en formato DD/MM/AA")
    @Column(columnDefinition = "DATE")
    LocalDate fechaNacimiento;
    String calleDomicilio;
    String numeroDomicilio;
    @ApiModelProperty(notes="Ingresar el celuar con codigo de area sin 0 y sin 15")
    @Column(length = 10)
    String numeroTelefono;
    
}
