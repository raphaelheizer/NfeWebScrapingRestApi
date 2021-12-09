package dev.heizer.nfewebscrapingrestapi.models;

import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;

@Entity
@Table(name = "state", schema = "public")
public class NfeState extends RepresentationModel<NfeState>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_id", nullable = false)
    private Long id;

    @Column(name = "state_name")
    private String name;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    @Override
    public String toString()
    {
        return "NfeState{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
