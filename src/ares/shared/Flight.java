package ares.shared;

import java.io.Serializable;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Flight implements Serializable
{

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;
  @Persistent
  private String location;
  @Persistent
  private String destination;
  @Persistent
  private String time;
  @Persistent
  private double price;
  @Persistent
  private String seatClass;
  @Persistent
  private String type;

  public Flight() {
  }

  public Flight(String location, String destination, String time, double price,
			String seatClass, String type) 
  {
    this();
    this.location = location;
    this.destination = destination;
    this.time = time;
    this.price = price;
    this.seatClass = seatClass;
    this.type = type;
  }

  public Long getId() {
    return this.id;
  }

  public String getLocation() {
    return this.location;
  }

  public String getDestination() {
    return this.destination;
  }

  public String getTime() {
	  return this.time;
  }
  
  public double getPrice() {
	  return this.price;
  }
  
  public String getSeatClass() {
	  return this.seatClass;
  }
  
  public String getType() {
	  return type;
  }
  public void setLocation(String location) {
    this.location = location;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }
  
  public void setTime(String time) {
	  this.time = time;
  }
  
  public void setSeatClass(String seatClass) {
	  this.seatClass = seatClass;
  }
  
  public void setType(String type) {
	  this.type = type;
  }
  
  public boolean equals(Flight testFlight)
  {
	  if (this.location.equals(testFlight.getLocation()) && this.destination.equals(testFlight.getDestination())
			  && this.price == testFlight.getPrice() && this.time.equals(testFlight.getTime()) 
			  && this.seatClass.equals(testFlight.getSeatClass()) && this.type.equals(testFlight.getType()))
		  return true;
	  return false;
  }
}