/*�e�`���l���p�̃T�[�o�v���O����*/   
import java.io.*;
import java.net.*;
import java.util.*;

public class Channel implements Runnable {
    Server server;             // �`���b�g�T�[�o�{��
    Socket socket = null;      // �\�P�b�g
    BufferedReader input;      // ���͗p�X�g���[��
    OutputStreamWriter output; // �o�͗p�X�g���[��
    public Thread thread;      // �`���l�����쓮���邽�߂̃X���b�h
    String handle;             // �N���C�A���g�̃n���h��
    String userIP;			   // �V�K�F�N���C�A���g��IP�A�h���X
    boolean sendFile = false, receiveFile = false;;
    String sendFilePerson;
    String sendFileName;
    String receiveFilePerson;
    String receiveFileName;
    
    int port;

    // �����̓`���l���ԍ��A�\�P�b�g�AServer.
    Channel(Socket s, Server cs) {
		server = cs;
		socket = s;
		start();
    }
    
    public void start() {
		thread = new Thread(this);
		thread.start();
    }
	
    public void stop() {
		thread = null;
    }

    // �N���C�A���g�֕�������o�͂���
    synchronized void write(String s) {
		try {
	    	output.write(s + "\r\n");
	    	output.flush();
		} catch (IOException e) {
	    	System.out.println("Write Err");
	    	close(); // �G���[���N��������A�ڑ���ؒf����
		}
    }

    
    /*
     *  �`���l���̃��C�����[�`���B
     *  �N���C�A���g����̓��͂��󂯕t����
     */
    public void run() {
		String s;
		try {
			userIP = ""+socket.getInetAddress();
	    	// �\�P�b�g������o�̓X�g���[���𓾂�
	    	input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
	    	output = new OutputStreamWriter(socket.getOutputStream());
	    	write("# �悤�����I Chat�T�[�o�[�ցD");   // ���}�̈��A 
	    	write("# �䖼�O����͂��ĉ������D");      // �n���h�����o�^
	    	handle = input.readLine();
	    	write("# �o�^�v���܂����C"+ handle + "�l�D");
	    	

	    	while (thread != null) {    // ���͑҂��̃��[�v 
				s = input.readLine();  // ��������͂�҂�
				if (s == null)  close();
            	else {
            		if(s.length() > 0 && s.charAt(0) == 5) { // ����R�}���h�̏���
            			System.out.println("����R�}���h����M");
            			String cs = String.valueOf((char)06);
            			StringTokenizer st = new StringTokenizer(s, cs, false);
                        String command = st.nextToken().substring(1);
                        
                        if(command.equals("sendPerson")) {
                        	 try {
                        		String person = st.nextToken();
                        		String mes = st.nextToken();
                        		mes = handle + " : " + mes + " : only "+person;
                        		System.out.println(command+";"+person+";"+mes);
                        		boolean sendOK = server.sendPerson(person,mes);
                        		if(sendOK) {
                        			write(mes);
                        		} else {
                        			String mes2 = person +" is not exist";
                        			write(mes2);
                        		}
                        	} catch(Exception e) {
                        		String mes2 = "�Ώ��s�\�ȃ��b�Z�[�W�̂悤�ł�";
                        			write(mes2);
          
                        	}
                        } else if(command.equals("sendFile")) {
                        	// sendFile�̏���
                        	try {
                        		String person = st.nextToken();
                        		String fileName = st.nextToken();
                        		
                        		System.out.println(command+";"+person+";"+fileName);
                        		
                        		sendFilePerson = person;
                        		sendFileName = fileName;
                        		
                        		boolean sendOK = server.checkPerson(person);
                        		if(sendOK && handle.equals(person) == false) {
                        			
                        			String s5 = String.valueOf((char) 05);
									String s6 = String.valueOf((char) 06);
									
									port = FTPReceiveServer.getPort();
									
                        			String mes = s5+"receiveServerOK"+s6+port;
                        			server.sendPerson(person,mes);
                        			
                        		} else {
                        			String mes;
                        			if(handle.equals(person)) {
                        				mes = "�����ɂ͑��M�ł��܂���";
                        			} else {
                        				mes = person +"�͑��݂��܂���";
                        			}
                        			write(mes);
                        		}
                        	} catch(Exception e) {
                        		String mes2 = "�Ώ��s�\�ȃ��b�Z�[�W�̂悤�ł�";
                        			write(mes2);
                        	}
                        } else if(command.equals("receiveOK")) {
                        
                        	try {
                        		String person = st.nextToken();
                        		String FTPhost = st.nextToken();  
                        		String FTPport = st.nextToken();
                        		String fileName = st.nextToken();
                        	
	                        	boolean sendOK = server.checkPerson(person);
                        	
                        		if(sendOK && handle.equals(person) == false) {
                        			
                        			String s5 = String.valueOf((char) 05);
									String s6 = String.valueOf((char) 06);
                        			String mes = s5+"receiveOK"+s6+handle+s6+FTPhost+s6+FTPport+s6+fileName;
                        			server.sendPerson(person,mes);
                        			
                        		} else {
                        			String mes;
                        			if(handle.equals(person)) {
                        				mes = "�����ɂ͑��M�ł��܂���";
                        			} else {
                        				mes = person +"�͑��݂��܂���";
                        			}
                        			write(mes);
                        		}
                        	} catch(Exception e) {
                        		String mes2 = "�Ώ��s�\�ȃ��b�Z�[�W�̂悤�ł�";
                        		write(mes2);
                        	}
                        } else if(command.equals("receiveFile")) {
                        	// receiveFile�̏���
                        	try {
                        		String person = st.nextToken();
                        		String FTPhost = st.nextToken();  
                        		String FTPport = st.nextToken();
                        		String fileName = st.nextToken();
                        	
	                        	boolean sendOK = server.checkPerson(person);
                        	
                        		if(sendOK && handle.equals(person) == false) {
                        			
                        			String s5 = String.valueOf((char) 05);
									String s6 = String.valueOf((char) 06);
                        			String mes = s5+"receiveFile"+s6+handle+s6+FTPhost+s6+FTPport+s6+fileName;
                        			server.sendPerson(person,mes);
                        			
                        		} else {
                        			String mes;
                        			if(handle.equals(person)) {
                        				mes = "�����ɂ͑��M�ł��܂���";
                        			} else {
                        				mes = person +"�͑��݂��܂���";
                        			}
                        			write(mes);
                        		}
                        	} catch(Exception e) {
                        		String mes2 = "�Ώ��s�\�ȃ��b�Z�[�W�̂悤�ł�";
                        		write(mes2);
                        	}
                        }
            		} else { // �ʏ탁�b�Z�[�W�̏���
		    			// �S�N���C�A���g�Ƀu���[�h�L���X�g����
		    			server.broadcast(handle + " : " + s);
		    		}
				}
	    	}
		} catch(IOException e) {
	    	close(); // �G���[���N��������A�ڑ���ؒf����
		}
    }
    
    
    
    
    // �ڑ���ؒf����
    public void close() {
		try {
	    	input.close();     // �X�g���[�������
	    	output.close();
	    	socket.close();    // �\�P�b�g�����
	    	socket = null;
	    	server.broadcast("# ����ؒf :" + handle);
  	    	stop();
		} catch(IOException e) {
	    	System.out.println("Close Err");
		}
    }
}
