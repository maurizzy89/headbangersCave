package mauriNetwork.headbangersCave.entidades;

import mauriNetwork.headbangersCave.enumeraciones.Rol;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreu;
    private String password;
    private String foto;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechalta;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    
    @OneToMany
    private List<Publicacion> publicacionesUsuario;

}
