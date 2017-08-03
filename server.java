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

/**
*	This is the Server class.
*	This class is responsible for all the tasks of the server.
*	Server Class breaks every received request from the Clients into smaller requests and distributes it among all the connected client.
*	After all the clients returns the result for the smaller requests the server then combines the result in proper order and sends it back to the Client that requested the operation.
*
*	At the moment the Server can handle only one request at a time.
*	The next request can only start after the current one finishes.
**/
class Server{
	public static String currentRequestorClientID = null;
	public static int totalClientConnected = -1;		
	public static ConnectionHandler[] connection = new ConnectionHandler[50];//At max 50 clients.

	public static int numberOfWorkingClients=-1;
	public static String[] resultOfClients= new String[50];

	public static void main(String args[]){

		try{
			ServerSocket serverSocket = new ServerSocket(9090);
			System.out.println("Server Started");
			while(true){
				Socket clientSocket = serverSocket.accept();
				
				totalClientConnected++;
				connection[totalClientConnected] = new ConnectionHandler(clientSocket,totalClientConnected+"", new ConnectionHelpers(){
					
					public void sendRequest(String clientRequest, String clientID){
						//System.out.println("This is the ConnectionHelpers Method -- r="+r);
						String[] request = clientRequest.split(" ");

						if(currentRequestorClientID != null){
							connection[Integer.parseInt(clientID)].requestComplete("Server is Busy.!");
							return;
						}
						
						System.out.println("REQUEST LENGTh IS = "+request.length);
						if(request.length != 4){
							connection[Integer.parseInt(clientID)].requestComplete("Unkown Command for Server");
							return;
						}

						currentRequestorClientID = request[0];
						System.out.println("GOT THE REQUEST FROM = "+clientRequest);
						numberOfWorkingClients = totalClientConnected+1;

						int jobs[] = splittingJobs(Integer.parseInt(request[2]),Integer.parseInt(request[3]),totalClientConnected+1);

						for(int i=0;i<=totalClientConnected;i++)
							connection[i].sendWorkRequest(request[1]+" "+jobs[i]+" "+(jobs[i+1]-1));

					}
			
					public void returnResult(String result, String clientID){
						numberOfWorkingClients--;
						System.out.println("Clients Still Working = "+numberOfWorkingClients);	
						resultOfClients[Integer.parseInt(clientID)] = result;				
						if(numberOfWorkingClients == 0){
							//Task is completed. Notify the client Which requested the task.
							//Gather the result
							String resultForClient = "";
							for(int i = 0;i<=totalClientConnected;i++){
								resultForClient = resultForClient+resultOfClients[i];				
							}
							//send it to the client
							System.out.println("Returning Result for ID="+Integer.parseInt(currentRequestorClientID));
							//System.out.println("Result = \n"+resultForClient);
							connection[Integer.parseInt(currentRequestorClientID)].requestComplete(resultForClient);
							currentRequestorClientID = null;

						}
					}
				});
				//System.out.println("GOT THE REQUEST === === R= "+request);
				new Thread(connection[totalClientConnected]).start();
			}
		}catch(Exception e){
			System.out.println("Error in Server "+e.getMessage());
			System.exit(-1);
		}
	}

	/**
	*	This method splits the job of finding prime numbers equally amoung noOfClients.
	*	Parameters :
	*		jobStart - integer value of the start value in the range.
	*		jobEnd - integer value of the end value in the range.
	*		noOfClients - Count of the connected number of Clients.
	*
	*	Return :
	*		An Integer Array that represents the the start value of the range for every client.
	*		eg- A[0] is the start value of the Client 0 & A[1]-1 will be the end value. 
	**/
	public static int[] splittingJobs(int jobStart,int jobEnd,int noOfClients)
	{
		int job[]=new int[noOfClients+1];
		double lengthOfEachJob=1.0*(jobEnd-jobStart)/noOfClients;
		int i=0;
		
		double startOfEachJob=jobStart;
		
		job[i++]=jobStart;
		
		while((i<noOfClients+1))
		{
			double temporary=startOfEachJob+=lengthOfEachJob;
			startOfEachJob=(int)startOfEachJob;
			job[i++]=(int)startOfEachJob;
			startOfEachJob=temporary;
		}
		
		if(job[i-1]!=jobEnd)
		{
			job[i-1]=jobEnd;
		}
		return job;
	}
}

/**
*	This interface provides extra connection helpers to the server to interact with the ConnectionHandlers for that Client.
*
**/
interface ConnectionHelpers{
	public void sendRequest(String clientRequest,String clientID);
	public void returnResult(String result,String clientID);
}

