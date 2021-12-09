package dev.heizer.nfewebscrapingrestapi.models;

import javax.persistence.*;

@Entity
@Table(name = "service", schema = "public")
public class NfeService
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id", nullable = false)
    private Long id;

    @Column(name = "service_name")
    private String name;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}
}
