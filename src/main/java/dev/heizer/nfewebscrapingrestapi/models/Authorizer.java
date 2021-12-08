package dev.heizer.nfewebscrapingrestapi.models;

import java.util.ArrayList;
import java.util.List;

public class Authorizer
{
    private String name;
    private String state;
    private List<Service> services = new ArrayList<>() {};

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getState() {return state;}

    public void setState(String state) {this.state = state;}

    public List<Service> getServices() {return services;}

    public void setServices(List<Service> services) {this.services = services;}

    @Override
    public String toString()
    {
        return "Authorizer{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", services=" + services +
                '}';
    }
}
