package dev.heizer.nfewebscrapingrestapi.models;

public class Service
{
    private String name;
    private ServiceStatus status;

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public ServiceStatus getStatus() {return status;}

    public void setStatus(ServiceStatus status) {this.status = status;}

    @Override
    public String toString()
    {
        return "Service{" +
                "name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
