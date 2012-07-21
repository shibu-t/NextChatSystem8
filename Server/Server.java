import java.io.*;  
import java.net.*;

public class Server implements Runnable {
    final int MAX_CHANNELS = 256; // 最大チャネル数
    Channel channel[] = new Channel[MAX_CHANNELS];  
    ServerSocket serversocket;    // 接続受け付け用ServerSocket
    int port;                     // ポート番号
    Thread thread;
 
    public static void main(String args[]) {
		if (args.length < 1) 
	  		System.out.println("%java Server port_number");
		else  {
	    	int p = Integer.parseInt(args[0]);
	    	new Server(p);
		}
    }

    public Server(int port) {
    	this.port = port;
		this.start();
    }

    // メインサーバーの切断を行なう
    public void serverClose() {
		try {
	    	System.out.println("Server#Close()");
	    	serversocket.close();
	    	serversocket = null;
		} catch (IOException e) {e.printStackTrace(System.err);}
    }

    // 全チャネルの切断を行なう
    public void clientClose() {
		System.out.println("Server#Close "+channel.length);
		for (int i = 0; i < channel.length; i++) {
	    	System.out.println("Server#Close("+i+")");
	    	channel[i].close();
	    	channel[i] = null;
		}
    }
    
    public void quit() {
		clientClose();
		serverClose();
		System.exit(1);
    }
    
    public void start() {
		if (thread == null) {
	    	thread = new Thread(this);
	    	thread.start();
		}
    }
    
    public void stop() {
		if (thread != null) {
	    	// thread.stop();
	    	thread = null;
		}
    }
    
    public void run() {
		int i;
		try {
	    	serversocket = new ServerSocket(port);   // メインサーバー開放
	    	while (true) {                     // 空いているチャネルを探す
				for (i = 0; i < MAX_CHANNELS; i++) {
		    		if (channel[i] == null || channel[i].thread == null) {
						break;
		    		}
				}
				if (i == MAX_CHANNELS)      // 最大のクライアント数なら終了
		  			return;
				Socket socket = serversocket.accept();    // 接続待ち
				channel[i] = new Channel(socket, this);// 新チャネル作成
	    	}
		} catch(IOException e) {
	    	System.out.println("Server Err!");
	    	return;
		}
    }
    
    // 全チャネルへブロードキャスト
    synchronized void broadcast(String message) {
		for (int i = 0; i < MAX_CHANNELS; i++) {
	    	if (channel[i] != null && channel[i].socket != null) {
				channel[i].write(message);
	    	}
		}
    }
    
    synchronized boolean sendPerson(String person, String message) {
    	boolean sendOK = false;
		for (int i = 0; i < MAX_CHANNELS; i++) {
	    	if (channel[i] != null && channel[i].socket != null 
	    			&& channel[i].handle != null 
	    			&& channel[i].handle.equals(person)) {
				channel[i].write(message);
				
				//Step1: FTPReceiveServerを利用しファイルの受信準備
				FTPReceiveServer(channel[i]);
				
				sendOK = true;
	    	}
		}
		return sendOK;
    }
    
    
    synchronized boolean checkPerson(String person) {
    	boolean sendOK = false;
		for (int i = 0; i < MAX_CHANNELS; i++) {
	    	if (channel[i] != null && channel[i].socket != null 
	    			&& channel[i].handle != null 
	    			&& channel[i].handle.equals(person)) {
				sendOK = true;
	    	}
		}
		return sendOK;
    }
}



