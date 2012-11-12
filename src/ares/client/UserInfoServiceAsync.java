package ares.client;

import ares.shared.Flight;
import ares.shared.UserInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserInfoServiceAsync {

	public void getUserInfo(String requestUri, AsyncCallback<UserInfo> callback);
	public void sendTicketConfirmation(Flight flight, String date, String adults, String children,
			String emailAddress, String nickname, AsyncCallback<Void> callback);
	public void sendCancelConfirmation(Flight flight, String date, String adults, String children,
			String emailAddress, String nickname, AsyncCallback<Void> callback);

}
