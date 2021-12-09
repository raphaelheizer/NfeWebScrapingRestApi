package dev.heizer.nfewebscrapingrestapi.models;

import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "service_history", schema = "public")
public class NfeServiceHistory extends RepresentationModel<NfeServiceHistory>
{
    @Id
    @Column(name = "service_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "service_history_service_fkey", referencedColumnName = "service_id")
    private NfeService nfeService;

    @OneToOne
    @JoinColumn(name = "service_history_authorizer_fkey")
    private Authorizer authorizer;

    @Column(name = "service_history_availability")
    @Enumerated(EnumType.STRING)
    private NfeServiceStatusEnum status;

    @Column(name = "service_history_timestamp")
    private Timestamp timestamp = new Timestamp(new Date().getTime());

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public NfeServiceStatusEnum getStatus() {return status;}

    public void setStatus(NfeServiceStatusEnum status) {this.status = status;}

    @Column(insertable = false)
    public Timestamp getTimestamp() {return timestamp;}

    public Authorizer getAuthorizer() {return authorizer;}

    public void setAuthorizer(Authorizer authorizer) {this.authorizer = authorizer;}

    public NfeService getService() {return nfeService;}

    public void setService(NfeService nfeService) {this.nfeService = nfeService;}

    @Override
    public String toString()
    {
        return "ServiceHistory{" +
                "id=" + id +
                ", service=" + nfeService +
                ", authorizer=" + authorizer +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
