package dev.heizer.nfewebscrapingrestapi.models;

import javax.persistence.*;

@Entity
@Table(name = "authorizer", schema = "public")
public class Authorizer
{
    @Id
    @Column(name = "authorizer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authorizer_name")
    private String name;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    @Override
    public String toString()
    {
        return "Authorizer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
