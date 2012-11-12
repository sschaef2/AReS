package ares.client;

import java.io.UnsupportedEncodingException;

import ares.shared.Flight;
import ares.shared.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userInfo")
public interface UserInfoService extends RemoteService {
	public UserInfo getUserInfo(String requestUri);
	public void sendTicketConfirmation(Flight flight, String date, String adults, String children,
			String emailAddress, String nickname) throws UnsupportedEncodingException;
	public void sendCancelConfirmation(Flight flight, String date, String adults, String children,
			String emailAddress, String nickname) throws UnsupportedEncodingException;
}
