package dev.heizer.nfewebscrapingrestapi.models;

import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "authorizer", schema = "public")
public class Authorizer extends RepresentationModel<Authorizer>
{
    @Id
    @Column(name = "authorizer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authorizer_name")
    private String name;

    @OneToMany
    @JoinColumn(name = "state_authorizer_fkey", referencedColumnName = "authorizer_id")
    private List<NfeState> state;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public List<NfeState> getState() {return state;}

    public void setState(List<NfeState> nfeState)
    {
        this.state = nfeState;
    }

    @Override
    public String toString()
    {
        return "Authorizer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
