package jeronimo.margitic.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

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
    @Temporal(TemporalType.DATE)
    @Column(columnDefinition = "DATE")
    LocalDate fechaNacimiento;
    String calleDomicilio;
    String numeroDomicilio;
    @ApiModelProperty(notes="Ingresar el celuar con codigo de area sin 0 y sin 15")
    @Column(length = 10)
    String numeroTelefono;
    
}
