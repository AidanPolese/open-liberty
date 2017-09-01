/**
 *
 */
package jaxrs21sse.basic;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DataObject {

    static DataObject[] DATA_OBJECTS = new DataObject[] {
                                                          new DataObject(7, "shiny", 3.14, new Date()),
                                                          new DataObject(Long.MAX_VALUE, "big", Double.MAX_VALUE, new Date(new Date().getTime() + 10000)),
                                                          new DataObject(Long.MIN_VALUE, "small", Double.MIN_VALUE, new Date(new Date().getTime() - 100000))
    };

    long id;
    String description;
    double cost;
    Date timeStamp;

    public DataObject() {}

    public DataObject(long id, String description, double cost, Date timeStamp) {
        this.id = id;
        this.description = description;
        this.cost = cost;
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataObject) {
            DataObject other = (DataObject) o;
            Instant thisInstant = this.timeStamp.toInstant();
            Instant otherInstant = other.timeStamp.toInstant();
            return (thisInstant.isAfter(otherInstant.minus(10, ChronoUnit.SECONDS)) &&
                    thisInstant.isBefore(otherInstant.plus(10, ChronoUnit.SECONDS)) &&
                    this.cost == other.cost &&
                    this.description.equals(other.description) &&
                    this.id == other.id);
        }
        return false;
    }

    public long getLongID() {
        return id;
    }

    public void setLongID(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
