package ares.server;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ares.client.FlightService;
import ares.shared.Flight;

@SuppressWarnings("serial")
public class FlightServiceImpl extends RemoteServiceServlet implements
FlightService {

	private static final Logger LOG = Logger.getLogger(FlightServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public void addFlight(Flight flight) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(flight);
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void removeFlight(Flight flight) 
	{
		PersistenceManager pm = getPersistenceManager();
		try 
		{
			long deleteCount = 0;
			Query q = pm.newQuery(Flight.class);
			List<Flight> flights = (List<Flight>) q.execute();
			for (Flight testFlight : flights) 
			{
				if (flight.equals(testFlight)) 
				{
					deleteCount++;
					pm.deletePersistent(testFlight);
				}
			}
			if (deleteCount != 1) 
			{
				LOG.log(Level.WARNING, "removeFlight deleted "+deleteCount+" Flights");
			}
		} 
		finally 
		{
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Flight> getFlights() 
	{
		PersistenceManager pm = getPersistenceManager();
		List<Flight> flights = null;
		try 
		{
			Query q = pm.newQuery(Flight.class);
			List<Flight> results = (List<Flight>) q.execute();

			flights = new ArrayList<Flight>();
			for (Flight f : results)
			{
				f.getLocation();
				flights.add(f);
			}
		} 
		finally 
		{
			pm.close();
		}
		return flights;
	}

	@SuppressWarnings("unchecked")
	public List<String> getLocations()
	{
		PersistenceManager pm = getPersistenceManager();
		List<String> locations = new ArrayList<String>();
		try 
		{
			Query q = pm.newQuery(Flight.class);
			List<Flight> flights = (List<Flight>) q.execute();

			if (! flights.isEmpty())
			{
				for (Flight flight : flights) 
				{
					if (locations.isEmpty() || (! locations.contains(flight.getLocation())))
						locations.add(flight.getLocation());
				}
			}
		}
		finally 
		{
			pm.close();
		}
		return locations;  
	}

	@SuppressWarnings("unchecked")
	public List<String> getDestinations()
	{
		PersistenceManager pm = getPersistenceManager();
		List<String> destinations = new ArrayList<String>();
		try 
		{
			Query q = pm.newQuery(Flight.class);
			List<Flight> flights = (List<Flight>) q.execute();

			if (! flights.isEmpty())
			{
				for (Flight flight : flights) 
				{
					if (destinations.isEmpty() || (! destinations.contains(flight.getDestination())))
						destinations.add(flight.getDestination());
				}
			}
		}
		finally 
		{
			pm.close();
		}
		return destinations;  
	}
	
	@SuppressWarnings("unchecked")
	public List<Flight> getFlights(String location, String destination, String seatClass)
	{
		PersistenceManager pm = getPersistenceManager();
		List<Flight> flights = new ArrayList<Flight>();
		List<Flight> results = new ArrayList<Flight>();
		try {
			if (! seatClass.equals(""))
			{
				Query q = pm.newQuery(Flight.class, "location == loc && destination == dest " +
					"&& seatClass == sc");
				q.declareParameters("String loc, String dest, String sc");
				results = (List<Flight>) q.execute(location, destination, seatClass);
			}
			else
			{
				Query q = pm.newQuery(Flight.class, "location == loc && destination == dest");
				q.declareParameters("String loc, String dest");
				results = (List<Flight>) q.execute(location, destination);
			}
			
			for (Flight f : results) 
			{
				f.getLocation();
				flights.add(f);
			}
			
		} finally {
			pm.close();
		}
		return flights;
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}
}

