import java.io.*;
import java.net.*;

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