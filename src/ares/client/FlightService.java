package ares.client;

import java.util.List;

import ares.shared.Flight;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("flight")
public interface FlightService extends RemoteService {

	public void addFlight(Flight flight);
	public void removeFlight(Flight flight);
	public List<Flight> getFlights();
	public List<String> getLocations();
	public List<String> getDestinations();
	public List<Flight> getFlights(String type);
}
