import java.io.*;
import java.net.*;
import java.util.*;

/**
*	This class represents a client.
*	The class is responsible for sending requests, receiving requests, computing the result of the requests and send the results back to server.
**/
class Client{

	public static String clientID;
	public static BufferedReader br;//Read from Server.
	public static PrintWriter pw;//Write to Server.
	
	public static void main(String args[]){
		try{
			Socket socket = new Socket("home",9090);
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream(),true);
			clientID = br.readLine();
			System.out.println("ClientID received as "+clientID);
			startReadThread();
			startWriteThread();

		}catch(Exception e){
			System.out.println("Exception in listen socket "+e);
			System.exit(-1);
		}
	}

	/**
	*	This method creates a Thread that listens for Server messages for the client.  
	**/
	public static void startReadThread(){
		new Thread(new Runnable(){
		
			public void run(){
				try{
					while(true){
						String serverCommand = br.readLine();
						System.out.println("\nServer : \n"+serverCommand);
						
						if(serverCommand.substring(0,5).equals("prime")){
							String tempResp =executeOperation(serverCommand);
							pw.println(".r"+tempResp);
						}
						System.out.print("Enter command or .q to exit: ");
					}
				}
				catch(Exception e){
					System.out.println("Exception in listen socket Read Thread "+e);
					System.exit(-1);
				}
			}
		
		}).start();		
	}

	/**
	*	This method starts a Thread that is responsible to read all the commands that a user types in the Clients console window.
	**/
	public static void startWriteThread(){
		
		new Thread(new Runnable(){
		
			public void run(){
				try{
					Scanner sc = new Scanner(System.in);
					while(true){
						System.out.print("Enter command or .q to exit: ");
						String message = sc.nextLine();
					
						if(message.equals(".q")){
							System.out.println("Disconnected from Server.!!");
							pw.println(message);
							System.exit(-1);
						}
						else
							pw.println(clientID+" "+message);
					}
				}
				catch(Exception e){
					System.out.println("Exception in listen socket Write Thread "+e);
					System.exit(-1);
				}
			}
		
		}).start();
	}

	/**
	*	This method performs the requested operation.
	*	Parameter:
	*		command - It is string form of the requested operation.
	*	Return Value:
	*		A String form of the result of the operation.
	**/
	public static String executeOperation(String command){
		String[] comm = command.split(" ");
		System.out.println("Executing AT CLIENT --> PRIME req="+command);
		return calculatePrimeNumbers(Integer.parseInt(comm[1]),Integer.parseInt(comm[2]));

	}

	/**
	*	This method finds the prime numbers in range.
	*	Parameter:
	*		start - start value of the range.
	*		end - end value of the range.
	*	Return Value:
	*		A String of all prime numbers in the given range.
	**/
	public static String calculatePrimeNumbers(int start, int end){
		int count;
		StringBuilder str = new StringBuilder("");
		for (int i = start; i <= end; i++) {
   			count = 0;
   			for (int j = 2; j <= i / 2; j++) {
    			if (i % j == 0) {
     				count++;
     				break;
    			}
   			}
   			if (count == 0) {
				str.append(i+" ");
    			//System.out.println(i);
   			}
  		}
		System.out.println("Executing AT CLIENT --> PRIME COMPLETE.");
		return str.toString();
	}

}
