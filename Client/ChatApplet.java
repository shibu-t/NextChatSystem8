/* チャット用アプレットプログラム */   

import java.awt.event.*;
import java.applet.*;
import java.awt.*;
import java.util.*;


public class ChatApplet extends Applet implements Runnable, ActionListener {
    TextField inputArea, privateArea;     // 入力用テキストフィールド
    TextArea freeArea;       // 出力用テキストエリア
    Client client = null;    // Client クラス
    String host = "localhost";
    int port = 28000;
    int FTPport = 25001;
    TextField hostField, portField;
    Thread thread = null;
    Button connectBut, closeBut, quitBut, privateBut, sendFileBut, receiveFileBut;
    
    String sendFileName;
    String receiveFileName;
    boolean sendFile = false;
    boolean receiveFile = false;

    public static void main(String args[]){
        Frame f = new Frame("ChatApplet");
		ChatApplet chatapplet = new ChatApplet();
        chatapplet.init();
        f.add("Center", chatapplet);
        f.setSize(400, 300);
        f.setVisible(true);
    }
 
    public void init() {
		// アプレットのレイアウト
		setLayout(new BorderLayout());
		Panel p = new Panel();
		Panel p1 = new Panel();
		Panel p2 = new Panel();
		p.setLayout(new BorderLayout());
		p1.setLayout(new GridLayout(1,5,10,10));
		p1.add(new Label("Host = ", Label.RIGHT));
		p1.add(hostField = new TextField(host));
		p1.add(new Label("Port = ", Label.RIGHT));
		p1.add(portField = new TextField(""+port));
		p1.add(connectBut = new Button("Connect"));
		p2.add(closeBut = new Button("Close"));
		p2.add(quitBut = new Button("Quit"));
		p.add("North", p1);
		p.add("Center", p2);
		
		Panel privateP = new Panel();
		privateP.setLayout(new BorderLayout());
		privateP.add("West", new Label("Person:"));
		privateP.add("Center", privateArea = new TextField("",5));
		
		Panel buttonG = new Panel();
		buttonG.add(privateBut = new Button("Private"));
		buttonG.add(sendFileBut = new Button("SendFile"));
		buttonG.add(receiveFileBut = new Button("ReceiveFile"));
		
		privateP.add("East",buttonG);
		
		Panel message = new Panel();
		message.setLayout(new BorderLayout());
		message.add("West", new Label("Send:"));
		message.add("Center", inputArea = new TextField("",30));
		freeArea = new TextArea("",0,0,freeArea.SCROLLBARS_VERTICAL_ONLY);
		
		Panel allMessageP = new Panel();
		allMessageP.setLayout(new BorderLayout());
		allMessageP.add("North", message);
		allMessageP.add("South", privateP);
		
		freeArea.setEditable(false);
		freeArea.setColumns(10);
		freeArea.setRows(10);
		add("North", allMessageP);
		add("Center", freeArea);
		add("South", p);

		// イベント処理の登録
		connectBut.addActionListener(this);
		closeBut.addActionListener(this);
		quitBut.addActionListener(this);
		inputArea.addActionListener(this);
		privateBut.addActionListener(this);
		sendFileBut.addActionListener(this);
		receiveFileBut.addActionListener(this);
		
		new FTPReceiveServer(FTPport);
	
    }
    
    public void start() {
        if (thread == null) {
	    	freeArea.setText("");
            thread = new Thread(this);
            thread.start();
        }
    }
    
    public void stop() {
        thread = null;
    }

    // クライアントのメインルーチン
    public void run() {
		try {
	    	String s ;
	    	while(thread != null) {
	        	s = client.read();       // メッセージの読みとり
	        	if(s == null)  clientClose();         
	        	else {
	        		if(s.charAt(0) == 5) { // 制御コマンドの処理
            			System.out.println("制御コマンドを受信");
            			processControlCommand(s);
            		} else {
	        			freeArea.append(s + "\n"); // テキストエリアへ出力
	        		}
	        	}
	    	}
		} catch(Exception e) {
	    	if(thread != null) {
	        	System.out.println("受信中に例外が発生しました");
	        	e.printStackTrace(System.err);
				clientClose();
	    	}
		}
    }
    
    
    public void processControlCommand(String s) {
    	String cs = String.valueOf((char)06);
        StringTokenizer st = new StringTokenizer(s, cs, false);
        String command = st.nextToken().substring(1);
        
        if(command.equals("receiveOK")) {
        	try {
        		String person = st.nextToken();
        		String otherFTPhost = st.nextToken();
        		int otherFTPport = Integer.parseInt(st.nextToken());
            	String sendFileName = st.nextToken();
            	FTPSendClient.fileSend(sendFileName, otherFTPhost, otherFTPport);
            	String mes2 = person+"さんへファイル名："+sendFileName+"の送信を行いました";
                freeArea.append(mes2 + "\n"); // テキストエリアへ出力
            } catch(Exception e) {
                String mes2 = "対処不能なメッセージを受信したようです："+s;
                freeArea.append(mes2 + "\n"); // テキストエリアへ出力
            }
            sendFile = false;
        }else if(command.equals("receiveFile")) {
        	try {
        		String person = st.nextToken();
        		String otherFTPhost = st.nextToken();
        		int otherFTPport = Integer.parseInt(st.nextToken());
            	String sendFileName = st.nextToken();
            	FTPSendClient.fileSend(sendFileName, otherFTPhost, otherFTPport);
            	String mes2 = person+"さんへファイル名："+sendFileName+"の送信を行いました";
                freeArea.append(mes2 + "\n"); // テキストエリアへ出力
            } catch(Exception e) {
                String mes2 = "対処不能なメッセージを受信したようです："+s;
                freeArea.append(mes2 + "\n"); // テキストエリアへ出力
            }
            sendFile = false;
         
        } else if(command.equals("sendFile")) {
        	try {
            	String person = st.nextToken();
				String fileName = st.nextToken();
				java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
				String ownHost = addr.getHostAddress();
				char c5 = (char) 05;
				char c6 = (char) 06;
				String c6s = String.valueOf(c6);
				String mes = String.valueOf(c5)+"receiveOK"
					+ c6s + person + c6s + ownHost + c6s + FTPport + c6s +fileName;
				System.out.println(mes);
				writeMes(mes);
				String mes2 = person+"さんからファイル名："+fileName+"を受け取ります";
                freeArea.append(mes2 + "\n"); // テキストエリアへ出力
            } catch(Exception e) {
                String mes2 = "対処不能なメッセージを受信したようです："+s;
                freeArea.append(mes2 + "\n"); // テキストエリアへ出力
            }
        }
    }

    
    
    // 回線の接続を行なう
    public boolean clientOpen() {
    	sendFile = false;
		try {
	    	if(client == null){
	        	host = hostField.getText();
	        	port = Integer.valueOf(portField.getText()).intValue();;
	    		client = new Client();  // Clientクラスの呼び出し
	        	client.connectServer(host, port);
	        	return true;
	    	}
		} catch (Exception e) {
	    	System.out.println("接続時に何らかの例外が発生しました");
	    	e.printStackTrace(System.err);
	    	return false;
		}
		return false;
    }

    // 接続の切断を行なう
    public void clientClose() {
		try {
	    	if(client != null){
	    		client.close();
	    		client = null;
	    		thread = null;
	    	}
		} catch(Exception e) {
	    	System.out.println("切断時に何らかの例外が発生しました");
	    	e.printStackTrace(System.err);
		}
    }

    // メッセージの送信を行う
    public void writeMes(String mes) {
		try {
	    	client.write(mes);
		} catch(Exception e) {
	    	System.out.println("送信中に何らかの例外が発生しました");
	    	e.printStackTrace(System.err);
		}
    }

    // イベント処理
    public void actionPerformed(ActionEvent e) {
		if(e.getSource() == connectBut){    // 回線接続を実行
	    	if(clientOpen()) start();
		}
		else if(e.getSource() == closeBut){ // 回線切断を実行
	    	stop();
	    	clientClose();
		}
		else if(e.getSource() == quitBut){ // アプレットの終了
	    	stop();
	    	clientClose();
	    	System.exit(1);
		}
		else if(e.getSource() == inputArea && client != null) {
	    	// テキストフィールド内の文字列をサーバーへ送信する
	    	writeMes(inputArea.getText());
	    	inputArea.setText("");
		} else if(e.getSource() == privateBut) {
			String person = privateArea.getText();
			String message = inputArea.getText();
			char c5 = (char) 05;
			char c6 = (char) 06;
			String mes = String.valueOf(c5)+"sendPerson"
				+String.valueOf(c6)+person+String.valueOf(c6)+message;
			System.out.println(mes);
			writeMes(mes);
		} else if(e.getSource() == sendFileBut) {
			// sendFileボタンが押された時の処理
			String person = privateArea.getText();
			String fileName = inputArea.getText();
			char c5 = (char) 05;
			char c6 = (char) 06;
			String mes = String.valueOf(c5)+"sendFile"
				+String.valueOf(c6)+person+String.valueOf(c6)+fileName;
			System.out.println(mes);
			writeMes(mes);
			String mes2 = person+"さんへファイル名："+fileName+"の送信を試みます";
            freeArea.append(mes2 + "\n"); // テキストエリアへ出力
			
		} else if(e.getSource() == receiveFileBut) {
			// receiveFileボタンが押された時の処理
			String person = privateArea.getText();
			String fileName = inputArea.getText();
			try {
				java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
				String ownHost = addr.getHostAddress();
			
				char c5 = (char) 05;
				char c6 = (char) 06;
				String c6s = String.valueOf(c6);
				String mes = String.valueOf(c5)+"receiveFile"
					+c6s+person+c6s+ownHost+c6s+FTPport+c6s+fileName;
				System.out.println(mes);
				writeMes(mes);
				String mes2 = person+"さんからファイル名："+fileName+"の受信を試みます";
            	freeArea.append(mes2 + "\n"); // テキストエリアへ出力
            }catch(Exception e2) {
			}
		}
    }
}
