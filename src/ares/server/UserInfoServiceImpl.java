package ares.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ares.client.UserInfoService;
import ares.shared.Flight;
import ares.shared.UserInfo;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserInfoServiceImpl extends RemoteServiceServlet implements
		UserInfoService {
	
	public UserInfo getUserInfo(String requestUri) 
	{
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        UserInfo userInfo = new UserInfo();

        if (user != null)
        {
        	userInfo.setNickname(user.getNickname());
        	userInfo.setLoggedIn(true); 
        	userInfo.setEmailAddress(user.getEmail());
        	userInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
        }
        
        else
        {
        	userInfo.setLoggedIn(false);
        	userInfo.setLoginUrl(userService.createLoginURL(requestUri));
        }
        
		return userInfo;
	}
	
	public void sendTicketConfirmation(Flight flight, String date, String adults, String children, 
			String emailAddress, String nickname) throws UnsupportedEncodingException
	{
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        String msgBody = "Thank you for booking your flight with AReS. Please see ticket information below:" + 
        		"\n\nFrom: " + flight.getLocation() + "\nTo: " + 
				flight.getDestination() + "\nTime: " + flight.getTime() +
				"\nPrice: " + flight.getPrice() + "\nClass: " + 
				flight.getSeatClass() + "\nDate: " + date + 
				"\nAdults: " + adults + "\nChildren: " + children;

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("sean.schaefer2@gmail.com", "AReS Admin"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(emailAddress, nickname));
            msg.setSubject("Your Booked Ticket with AReS");
            msg.setText(msgBody);
            Transport.send(msg);
    
        } catch (AddressException e) {
            // ...
        } catch (MessagingException e) {
            // ...
        }
	}

	public void sendCancelConfirmation(Flight flight, String date, String adults, String children, 
			String emailAddress, String nickname) throws UnsupportedEncodingException
	{
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        String msgBody = "Your flight with AReS has been cancelled, as detailed below:" + 
        		"\n\nFrom: " + flight.getLocation() + "\nTo: " + 
				flight.getDestination() + "\nTime: " + flight.getTime() +
				"\nPrice: " + flight.getPrice() + "\nClass: " + 
				flight.getSeatClass() + "\nDate: " + date + 
				"\nAdults: " + adults + "\nChildren: " + children;

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("sean.schaefer2@gmail.com", "AReS Admin"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(emailAddress, nickname));
            msg.setSubject("Your Cancelled Ticket with AReS");
            msg.setText(msgBody);
            Transport.send(msg);
    
        } catch (AddressException e) {
            // ...
        } catch (MessagingException e) {
            // ...
        }
	}
}
