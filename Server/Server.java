import java.io.*;  
import java.net.*;

public class Server implements Runnable {
    final int MAX_CHANNELS = 256; // �ő�`���l����
    Channel channel[] = new Channel[MAX_CHANNELS];  
    ServerSocket serversocket;    // �ڑ��󂯕t���pServerSocket
    int port;                     // �|�[�g�ԍ�
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

    // ���C���T�[�o�[�̐ؒf���s�Ȃ�
    public void serverClose() {
		try {
	    	System.out.println("Server#Close()");
	    	serversocket.close();
	    	serversocket = null;
		} catch (IOException e) {e.printStackTrace(System.err);}
    }

    // �S�`���l���̐ؒf���s�Ȃ�
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
	    	serversocket = new ServerSocket(port);   // ���C���T�[�o�[�J��
	    	while (true) {                     // �󂢂Ă���`���l����T��
				for (i = 0; i < MAX_CHANNELS; i++) {
		    		if (channel[i] == null || channel[i].thread == null) {
						break;
		    		}
				}
				if (i == MAX_CHANNELS)      // �ő�̃N���C�A���g���Ȃ�I��
		  			return;
				Socket socket = serversocket.accept();    // �ڑ��҂�
				channel[i] = new Channel(socket, this);// �V�`���l���쐬
	    	}
		} catch(IOException e) {
	    	System.out.println("Server Err!");
	    	return;
		}
    }
    
    // �S�`���l���փu���[�h�L���X�g
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
				
				//Step1: FTPReceiveServer�𗘗p���t�@�C���̎�M����
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



