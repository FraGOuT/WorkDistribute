/**
*	This interface provides extra connection helpers to the server to interact with the ConnectionHandlers for that Client.
*
**/
interface ConnectionHelpers{
	public void sendRequest(String clientRequest,String clientID);
	public void returnResult(String result,String clientID);
}
