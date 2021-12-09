package dev.heizer.nfewebscrapingrestapi.models;

import java.util.List;

public class NfeServiceCountDTO
{
    private String stateName;
    private List<Authorizer> authorizers;
    private long count;

    public String getStateName() {return stateName;}

    public void setStateName(String stateName) {this.stateName = stateName;}

    public List<Authorizer> getAuthorizers() {return authorizers;}

    public void setAuthorizers(List<Authorizer> authorizers) {this.authorizers = authorizers;}

    public long getCount() {return count;}

    public void setCount(long count) {this.count = count;}

    public void incrementCount(long value) {count += value;}

    @Override
    public String toString()
    {
        return "NfeServiceCountDTO{" +
                "stateName='" + stateName + '\'' +
                ", authorizers=" + authorizers +
                ", count=" + count +
                '}';
    }
}
