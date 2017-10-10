import java.io.*;
import java.net.*;

/**
*	This Class Manages the connection between the client and server.
*	All the passing of data happens via the use of this class.
*	Every instance of this class will run inside a new thread which will enable the server to connect to multiple clients at the same time.
**/
class ConnectionHandler implements Runnable{
	Socket clientSocket;
	String clientID;
	ConnectionHelpers connectionHelpers;

	ConnectionHandler(Socket clientSocket, String clientID, ConnectionHelpers connectionHelpers){
		this.clientSocket = clientSocket;
		this.connectionHelpers = connectionHelpers;
		this.clientID = clientID+"";
	}

	public void run(){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(),true);
			
			pw.println(clientID);
		
			while(true){
				String message = br.readLine();
				
				//Check if the exit command is entered.
				if(!(message.equals(".q"))){
					//System.out.println("Client : "+message);
					//System.out.println("Message Received By ClientID="+clientID);
					
					//Check wether the message recevied by the server is a result.
					if(message.substring(0,2).equals(".r")){
						connectionHelpers.returnResult(message.substring(2),clientID);
					}
					else{//If not then it must be a request.
						connectionHelpers.sendRequest(message,clientID);
					}
				}
				else{
					System.out.println("Shutting Down Connection..!!");
					break;
				}
			}
		}catch(IOException e){
			System.out.println("Error 1 in client "+e);
		}
	}

	/**
	*	This method sends a request to the Client from the Server.
	**/
	public void sendWorkRequest(String req){
		try{
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(),true);
			pw.println(req);
			pw.flush();
		}catch(Exception e){
			System.out.println("Error 2 in client "+e);
		}		
	}

	/**
	*	This method is used to return the result to the Client after the Server has finished executing the request.
	**/
	public void requestComplete(String result){
		try{
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(),true);
			pw.println("Server Completed Your Request Your RESULT is :"+result);
			pw.flush();
			System.out.println("Requested Operation by Client "+clientID+ " is Complete.");
		
		}catch(Exception e){
			System.out.println("Error 3 in client "+e);
		}
	}
}