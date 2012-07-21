/*各チャネル用のサーバプログラム*/   
import java.io.*;
import java.net.*;
import java.util.*;

public class Channel implements Runnable {
    Server server;             // チャットサーバ本体
    Socket socket = null;      // ソケット
    BufferedReader input;      // 入力用ストリーム
    OutputStreamWriter output; // 出力用ストリーム
    public Thread thread;      // チャネルを駆動するためのスレッド
    String handle;             // クライアントのハンドル
    String userIP;			   // 新規：クライアントのIPアドレス
    boolean sendFile = false, receiveFile = false;;
    String sendFilePerson;
    String sendFileName;
    String receiveFilePerson;
    String receiveFileName;
    
    int port;

    // 引数はチャネル番号、ソケット、Server.
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

    // クライアントへ文字列を出力する
    synchronized void write(String s) {
		try {
	    	output.write(s + "\r\n");
	    	output.flush();
		} catch (IOException e) {
	    	System.out.println("Write Err");
	    	close(); // エラーを起こしたら、接続を切断する
		}
    }

    
    /*
     *  チャネルのメインルーチン。
     *  クライアントからの入力を受け付ける
     */
    public void run() {
		String s;
		try {
			userIP = ""+socket.getInetAddress();
	    	// ソケットから入出力ストリームを得る
	    	input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
	    	output = new OutputStreamWriter(socket.getOutputStream());
	    	write("# ようこそ！ Chatサーバーへ．");   // 歓迎の挨拶 
	    	write("# 御名前を入力して下さい．");      // ハンドル名登録
	    	handle = input.readLine();
	    	write("# 登録致しました，"+ handle + "様．");
	    	

	    	while (thread != null) {    // 入力待ちのループ 
				s = input.readLine();  // 文字列入力を待つ
				if (s == null)  close();
            	else {
            		if(s.length() > 0 && s.charAt(0) == 5) { // 制御コマンドの処理
            			System.out.println("制御コマンドを受信");
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
                        		String mes2 = "対処不能なメッセージのようです";
                        			write(mes2);
          
                        	}
                        } else if(command.equals("sendFile")) {
                        	// sendFileの処理
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
                        				mes = "自分には送信できません";
                        			} else {
                        				mes = person +"は存在しません";
                        			}
                        			write(mes);
                        		}
                        	} catch(Exception e) {
                        		String mes2 = "対処不能なメッセージのようです";
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
                        				mes = "自分には送信できません";
                        			} else {
                        				mes = person +"は存在しません";
                        			}
                        			write(mes);
                        		}
                        	} catch(Exception e) {
                        		String mes2 = "対処不能なメッセージのようです";
                        		write(mes2);
                        	}
                        } else if(command.equals("receiveFile")) {
                        	// receiveFileの処理
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
                        				mes = "自分には送信できません";
                        			} else {
                        				mes = person +"は存在しません";
                        			}
                        			write(mes);
                        		}
                        	} catch(Exception e) {
                        		String mes2 = "対処不能なメッセージのようです";
                        		write(mes2);
                        	}
                        }
            		} else { // 通常メッセージの処理
		    			// 全クライアントにブロードキャストする
		    			server.broadcast(handle + " : " + s);
		    		}
				}
	    	}
		} catch(IOException e) {
	    	close(); // エラーを起こしたら、接続を切断する
		}
    }
    
    
    
    
    // 接続を切断する
    public void close() {
		try {
	    	input.close();     // ストリームを閉じる
	    	output.close();
	    	socket.close();    // ソケットを閉じる
	    	socket = null;
	    	server.broadcast("# 回線切断 :" + handle);
  	    	stop();
		} catch(IOException e) {
	    	System.out.println("Close Err");
		}
    }
}
