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

	  public void removeFlight(Flight flight) {
		  PersistenceManager pm = getPersistenceManager();
		  try {
			  long deleteCount = 0;
			  Query q = pm.newQuery(Flight.class);
	      List<Flight> flights = (List<Flight>) q.execute();
	      for (Flight testFlight : flights) {
	        if (flight.equals(testFlight)) {
	          deleteCount++;
	          pm.deletePersistent(flight);
	        }
	      }
	      if (deleteCount != 1) {
	        LOG.log(Level.WARNING, "removeStock deleted "+deleteCount+" Stocks");
	      }
	    } finally {
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
	  
	  public List<Flight> getFlights(String type){
		  PersistenceManager pm = getPersistenceManager();
		    List<Flight> flights = null;
		    try {
			      Query q = pm.newQuery(Flight.class, "type == t");
			      q.declareParameters("String t");
			      flights = (List<Flight>) q.execute(type);
			    } finally {
			      pm.close();
			    }
			    return flights;
	  }

	  private PersistenceManager getPersistenceManager() {
	    return PMF.getPersistenceManager();
	  }
	}

