package ares.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import ares.shared.Flight;
import ares.shared.UserInfo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AReS implements EntryPoint {

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	@SuppressWarnings("unused")
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/** 
	 * Holds the Google account information for a logged in user. 
	 */
	private UserInfo userInfo = null;

	/**
	 * GWT Components.
	 */
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable flightsTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox toInputBox = new TextBox();
	private TextBox fromInputBox = new TextBox();
	private TextBox priceInputBox = new TextBox();
	private TextBox timeInputBox = new TextBox();
	private ListBox classComboBox = new ListBox();
	private ListBox typeComboBox = new ListBox();
	private ListBox fromListBox = new ListBox();
	private ListBox toListBox = new ListBox();
	private ListBox adultsListBox = new ListBox();
	private ListBox childrenListBox = new ListBox();
	private Button addFlightButton = new Button("Add");
	@SuppressWarnings("deprecation")
	DateTimeFormat dateFormat = DateTimeFormat.getShortDateFormat();
    DateBox dateBox = new DateBox();

	private Label toLabel = new Label("To:");
	private Label fromLabel = new Label("From:");
	private Label priceLabel = new Label("Price:");
	private Label timeLabel = new Label("Time:");
	private ArrayList<Flight> flights = new ArrayList<Flight>();


	private final FlightServiceAsync flightService = GWT.create(FlightService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		// Check login status using login service
		UserInfoServiceAsync userService = GWT.create(UserInfoService.class);
		userService.getUserInfo(GWT.getHostPageBaseURL(), new AsyncCallback<UserInfo>()
				{
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(UserInfo result) 
			{
				userInfo = result;
				if (userInfo.isLoggedIn())
				{
					// Check if user is an administrator
					// Remember to add email addresses for Aya & Leslie
					if (userInfo.getEmailAddress() == "sean.schaefer2@gmail.com"
							|| userInfo.getEmailAddress() == "agvilla3@asu.edu" 
							|| userInfo.getEmailAddress() == "tltang1@asu.edu")
						loadAresAdmin();
					else
						loadAresSearch();
				}

				else
				{
					loadAresDisplay();
				}
			}
				});
	}

	/**
	 * This page will load if the user is logged in as an administrator, and then can 
	 * add and remove flights from the persistence Datastore. 
	 */
	private void loadAresAdmin()
	{	
		flightsTable.setText(0, 0, "From");
		flightsTable.setText(0, 1, "To");
		flightsTable.setText(0, 2, "Price");
		flightsTable.setText(0, 3, "Time");
		flightsTable.setText(0, 4, "Class");
		flightsTable.setText(0, 5, "Type");
		flightsTable.setText(0, 6, "Remove");
		flightsTable.getRowFormatter().addStyleName(0, "flightListHeader");
		flightsTable.addStyleName("watchList");

		loadFlights(true);

		classComboBox.addItem("Economy");
		classComboBox.addItem("Business");
		typeComboBox.addItem("Domestic");
		typeComboBox.addItem("International");

		// Add the parameters for adding a flight to the HorizontalPanel
		addPanel.add(fromLabel);
		addPanel.add(fromInputBox);addPanel.add(toLabel);
		addPanel.add(toInputBox);
		addPanel.add(priceLabel);
		addPanel.add(priceInputBox);
		addPanel.add(timeLabel);
		addPanel.add(timeInputBox);
		addPanel.add(classComboBox);
		addPanel.add(typeComboBox);
		addPanel.add(addFlightButton);

		// Setup the VerticalPanel
		mainPanel.add(addPanel);
		mainPanel.add(flightsTable);

		// Associate the RootPanel to the HTML page
		RootPanel.get("flightList").add(mainPanel);
		toInputBox.setFocus(true);

		// Add Handler for when addFlightButton is clicked
		addFlightButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addFlight();
			}
		});
	}

	/**
	 * This page will load if the user is logged in, and will allow for searching/booking of 
	 * available domestic and international flights. 
	 */
	private void loadAresSearch() 
	{
		VerticalPanel fullPanel = new VerticalPanel();
		HorizontalPanel flightPanel = new HorizontalPanel();
		HorizontalPanel passengerPanel = new HorizontalPanel();

		Label flightLabel = new Label("Flight Details");
		flightLabel.addStyleName("searchLabels");
		Label passengerLabel = new Label("Passenger Details");
		passengerLabel.addStyleName("searchLabels");
		Label dateLabel = new Label("Date:");
		Label adultsLabel = new Label("Adults (12+):");
		Label childrenLabel = new Label("Children (2-11):");
		Label classLabel = new Label("Class:");
		Button findButton = new Button("Find Flight");

		flightService.getLocations(new AsyncCallback<List<String>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(List<String> locations) {
				for (String loc: locations) {
					fromListBox.addItem(loc);
				}
			}
		});

		flightService.getDestinations(new AsyncCallback<List<String>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(List<String> destinations) {
				for (String dest: destinations) {
					toListBox.addItem(dest);
				}
			}
		});

		classComboBox.addItem("Economy");
		classComboBox.addItem("Business");

		for (int i = 1; i <= 6; i++)
			adultsListBox.addItem(Integer.toString(i));

		for (int i = 0; i <= 4; i++)
			childrenListBox.addItem(Integer.toString(i));
		
		flightsTable.setText(0, 0, "From");
		flightsTable.setText(0, 1, "To");
		flightsTable.setText(0, 2, "Price");
		flightsTable.setText(0, 3, "Time");
		flightsTable.setText(0, 4, "Class");
		flightsTable.setText(0, 5, "Type");
		flightsTable.setText(0, 6, "Reserve");
		flightsTable.getRowFormatter().addStyleName(0, "flightListHeader");
		flightsTable.addStyleName("watchList");
		flightsTable.setVisible(false);
		
		// Set the value in the text box when the user selects a date
		dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));

		flightPanel.add(fromLabel);
		flightPanel.add(fromListBox);
		flightPanel.add(toLabel);
		flightPanel.add(toListBox);
		flightPanel.add(dateLabel);
		flightPanel.add(dateBox);
		flightPanel.add(classLabel);
		flightPanel.add(classComboBox);

		passengerPanel.add(adultsLabel);
		passengerPanel.add(adultsListBox);
		passengerPanel.add(childrenLabel);
		passengerPanel.add(childrenListBox);

		fullPanel.add(flightLabel);
		fullPanel.add(flightPanel);
		fullPanel.add(passengerLabel);
		fullPanel.add(passengerPanel);
		fullPanel.add(findButton);
		fullPanel.add(flightsTable);
		
		RootPanel.get("flightList").add(fullPanel);
		findButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				findFlight();
			}
		});
	}

	/**
	 * This page will load if the user is not logged in, and will only display tables
	 * of available domestic and international flights (???). 
	 */
	private void loadAresDisplay()
	{
		// Assemble login panel.
		Anchor signInLink = new Anchor("Sign In");
		VerticalPanel loginPanel = new VerticalPanel();
		Label loginLabel = new Label("Please sign in to your Google Account to access the AReS application.");
		signInLink.setHref(userInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);

		HorizontalPanel topPanel = new HorizontalPanel();
		VerticalPanel fullPanel = new VerticalPanel();

		Label helpLabel = new Label("Need help?");
		helpLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.alert("If you need help, please email AReS support at sschaef2@asu.edu"
						+ " and include your inquiry. We will respond as soon as possible.");
			}
		});

		Label flightsLabel = new Label("Available Flights");

		topPanel.add(loginPanel);
		topPanel.add(helpLabel);
		fullPanel.add(topPanel);
		fullPanel.add(flightsLabel);

		flightsTable.setText(0, 0, "From");
		flightsTable.setText(0, 1, "To");
		flightsTable.setText(0, 2, "Price");
		flightsTable.setText(0, 3, "Time");
		flightsTable.setText(0, 4, "Class");
		flightsTable.setText(0, 5, "Type");
		flightsTable.getRowFormatter().addStyleName(0, "flightListHeader");
		flightsTable.addStyleName("watchList");

		loadFlights(false);
		fullPanel.add(flightsTable);

		RootPanel.get("flightList").add(fullPanel);
	}

	private void findFlight()
	{
		//Get parameters from GUI elements
		String location = fromListBox.getItemText(fromListBox.getSelectedIndex());
		String destination = toListBox.getItemText(toListBox.getSelectedIndex());
		String seatClass = classComboBox.getItemText(classComboBox.getSelectedIndex());
		
		
		//Call getFlights
		flightService.getFlights(location, destination, seatClass, new AsyncCallback<List<Flight>>() {
			public void onFailure(Throwable error) 
			{
				handleError(error);
			}
			
			public void onSuccess(List<Flight> flights)
			{
				displayFlights(flights, false, true);
				flightsTable.setVisible(true);
			}
		});		
	}
	
	/**
	 * Adds a flight to the table and to the datastore. 
	 */
	private void addFlight()
	{
		String location = fromInputBox.getText().trim();
		if (location.isEmpty())
		{
			Window.alert(location + " is not a valid entry for starting location.");
			fromInputBox.selectAll();
			return;
		}
		String destination = toInputBox.getText().trim();
		if (destination.isEmpty())
		{
			Window.alert(destination + " is not a valid entry for destination.");
			toInputBox.selectAll();
			return;
		}
		String time = timeInputBox.getText().trim();
		if (time.isEmpty())
		{
			Window.alert(time + " is not a valid entry for time.");
			timeInputBox.selectAll();

		}
		double price = new Double(priceInputBox.getText().trim());
		if (price <= 0.0)
		{
			Window.alert(price + " is not a valid entry for price.");
			priceInputBox.selectAll();
			return;
		}

		String seatClass = classComboBox.getItemText(classComboBox.getSelectedIndex());
		String type = typeComboBox.getItemText(typeComboBox.getSelectedIndex());

		Flight flight = new Flight(location, destination, time, price, seatClass, type);
		if (flights.contains (flight))
			return;

		//Add the flight to the Datastore
		addFlight(flight);
	}


	private void addFlight(final Flight flight)
	{
		flightService.addFlight(flight, 
				new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Void ignore) {
				displayFlight(flight, true, false);
			}
		});
	}

	private void loadFlights(final boolean isAdmin)
	{
		flightService.getFlights(new AsyncCallback<List<Flight>>(){
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(List<Flight> flights) {
				if (flights.isEmpty())
					Window.alert("All flights are empty");
				displayFlights(flights, isAdmin, false);
			}
		});
	}

	private void displayFlights(List<Flight> flights, boolean isAdmin, boolean isSearch)
	{
		for (Flight flight : flights) {
			displayFlight(flight, isAdmin, isSearch);
		}	
	}

	private void displayFlight(final Flight flight, boolean isAdmin, boolean isSearch){
		int row = flightsTable.getRowCount();
		flights.add(flight);

		flightsTable.setText(row, 0, flight.getLocation());
		flightsTable.setText(row, 1, flight.getDestination());
		flightsTable.setText(row, 2, Double.toString(flight.getPrice()));
		flightsTable.setText(row, 3, flight.getTime());
		flightsTable.setText(row, 4, flight.getSeatClass());
		flightsTable.setText(row, 5, flight.getType());

		// Add a button to remove this flight from the table.
		if (isAdmin)
		{
			Button removeFlightButton = new Button("x");
			removeFlightButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					removeFlight(flight);
				}
			});
			flightsTable.setWidget(row, 6, removeFlightButton);	
		}
		
		// Add a button to book this flight if search
		if (isSearch)
		{
			Button bookButton = new Button("Book");
			bookButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (Window.confirm("Are you sure you wish to book this flight? Press OK to book."))
						try {
							displayTicketInformation(flight);
						} catch (UnsupportedEncodingException e) {
							handleError(e);
							e.printStackTrace();
						}
				}
			});
			flightsTable.setWidget(row, 6, bookButton);
		}
	}

	private void removeFlight(final Flight flight) {
		flightService.removeFlight(flight, new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
			}
			public void onSuccess(Void ignore) {
				undisplayFlight(flight);
			}
		});
	}

	private void undisplayFlight(Flight flight)
	{
		int removedIndex = flights.indexOf(flight);
		flights.remove(removedIndex);
		flightsTable.removeRow(removedIndex+1);
	}

	private void displayTicketInformation(final Flight flight) throws UnsupportedEncodingException
	{
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Your Ticket Information");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		final Button cancelButton = new Button("Unbook");
		closeButton.getElement().setId("closeButton");
		cancelButton.getElement().setId("cancelButton");

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<p>Thank you for booking your flight with AReS. " +
				"Please see ticket information below:</p>" +
				"<b> From: </b>" + flight.getLocation() + "<br><b> To: </b>" + 
				flight.getDestination() + "<br><b> Time: </b>" + flight.getTime() +
				"<br><b> Price: </b>" + flight.getPrice() + "<br><b> Class: </b>" + 
				flight.getSeatClass() + "<br><b> Date: </b>" + dateBox.getValue() + 
				"<br><b> Adults: </b>" + adultsListBox.getItemText(adultsListBox.getSelectedIndex()) 
				+ "<br><b> Children: </b>" + childrenListBox.getItemText(childrenListBox.getSelectedIndex())
				+ "<p> We hope you enjoy your trip!</p>"));
		
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		dialogVPanel.add(cancelButton);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
		
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
						dialogBox.hide();
				}
			});
		
		// Add a handler to cancel the ticket
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (Window.confirm("Are you sure you want to cancel? Press OK to cancel."))
				{
					UserInfoServiceAsync userService = GWT.create(UserInfoService.class);
					userService.sendCancelConfirmation(flight, dateBox.getValue().toString(), 
							adultsListBox.getItemText(adultsListBox.getSelectedIndex()), 
							childrenListBox.getItemText(childrenListBox.getSelectedIndex()),
							userInfo.getEmailAddress(), userInfo.getNickname(), 
							new AsyncCallback<Void>() {
								public void onFailure(Throwable error) {
									handleError(error);
								}
								
								public void onSuccess(Void result) {
								}
							});
					Window.alert("Your flight has been cancelled.");
					dialogBox.hide();
				}
			}
		});
		
		//show the Dialog Box
		dialogBox.center();
		
		UserInfoServiceAsync userService = GWT.create(UserInfoService.class);
		userService.sendTicketConfirmation(flight, dateBox.getValue().toString(), 
				adultsListBox.getItemText(adultsListBox.getSelectedIndex()), 
				childrenListBox.getItemText(childrenListBox.getSelectedIndex()),
				userInfo.getEmailAddress(), userInfo.getNickname(), 
				new AsyncCallback<Void>() {
					public void onFailure(Throwable error) {
						handleError(error);
					}
					
					public void onSuccess(Void result) {
					}
				});
	}

	private void handleError(Throwable error) 
	{
		Window.alert(error.getMessage());
	}
}
