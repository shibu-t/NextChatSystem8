/* �`���b�g�p�A�v���b�g�v���O���� */   

import java.awt.event.*;
import java.applet.*;
import java.awt.*;
import java.util.*;


public class ChatApplet extends Applet implements Runnable, ActionListener {
    TextField inputArea, privateArea;     // ���͗p�e�L�X�g�t�B�[���h
    TextArea freeArea;       // �o�͗p�e�L�X�g�G���A
    Client client = null;    // Client �N���X
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
		// �A�v���b�g�̃��C�A�E�g
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

		// �C�x���g�����̓o�^
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

    // �N���C�A���g�̃��C�����[�`��
    public void run() {
		try {
	    	String s ;
	    	while(thread != null) {
	        	s = client.read();       // ���b�Z�[�W�̓ǂ݂Ƃ�
	        	if(s == null)  clientClose();         
	        	else {
	        		if(s.charAt(0) == 5) { // ����R�}���h�̏���
            			System.out.println("����R�}���h����M");
            			processControlCommand(s);
            		} else {
	        			freeArea.append(s + "\n"); // �e�L�X�g�G���A�֏o��
	        		}
	        	}
	    	}
		} catch(Exception e) {
	    	if(thread != null) {
	        	System.out.println("��M���ɗ�O���������܂���");
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
            	String mes2 = person+"����փt�@�C�����F"+sendFileName+"�̑��M���s���܂���";
                freeArea.append(mes2 + "\n"); // �e�L�X�g�G���A�֏o��
            } catch(Exception e) {
                String mes2 = "�Ώ��s�\�ȃ��b�Z�[�W����M�����悤�ł��F"+s;
                freeArea.append(mes2 + "\n"); // �e�L�X�g�G���A�֏o��
            }
            sendFile = false;
        }else if(command.equals("receiveFile")) {
        	try {
        		String person = st.nextToken();
        		String otherFTPhost = st.nextToken();
        		int otherFTPport = Integer.parseInt(st.nextToken());
            	String sendFileName = st.nextToken();
            	FTPSendClient.fileSend(sendFileName, otherFTPhost, otherFTPport);
            	String mes2 = person+"����փt�@�C�����F"+sendFileName+"�̑��M���s���܂���";
                freeArea.append(mes2 + "\n"); // �e�L�X�g�G���A�֏o��
            } catch(Exception e) {
                String mes2 = "�Ώ��s�\�ȃ��b�Z�[�W����M�����悤�ł��F"+s;
                freeArea.append(mes2 + "\n"); // �e�L�X�g�G���A�֏o��
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
				String mes2 = person+"���񂩂�t�@�C�����F"+fileName+"���󂯎��܂�";
                freeArea.append(mes2 + "\n"); // �e�L�X�g�G���A�֏o��
            } catch(Exception e) {
                String mes2 = "�Ώ��s�\�ȃ��b�Z�[�W����M�����悤�ł��F"+s;
                freeArea.append(mes2 + "\n"); // �e�L�X�g�G���A�֏o��
            }
        }
    }

    
    
    // ����̐ڑ����s�Ȃ�
    public boolean clientOpen() {
    	sendFile = false;
		try {
	    	if(client == null){
	        	host = hostField.getText();
	        	port = Integer.valueOf(portField.getText()).intValue();;
	    		client = new Client();  // Client�N���X�̌Ăяo��
	        	client.connectServer(host, port);
	        	return true;
	    	}
		} catch (Exception e) {
	    	System.out.println("�ڑ����ɉ��炩�̗�O���������܂���");
	    	e.printStackTrace(System.err);
	    	return false;
		}
		return false;
    }

    // �ڑ��̐ؒf���s�Ȃ�
    public void clientClose() {
		try {
	    	if(client != null){
	    		client.close();
	    		client = null;
	    		thread = null;
	    	}
		} catch(Exception e) {
	    	System.out.println("�ؒf���ɉ��炩�̗�O���������܂���");
	    	e.printStackTrace(System.err);
		}
    }

    // ���b�Z�[�W�̑��M���s��
    public void writeMes(String mes) {
		try {
	    	client.write(mes);
		} catch(Exception e) {
	    	System.out.println("���M���ɉ��炩�̗�O���������܂���");
	    	e.printStackTrace(System.err);
		}
    }

    // �C�x���g����
    public void actionPerformed(ActionEvent e) {
		if(e.getSource() == connectBut){    // ����ڑ������s
	    	if(clientOpen()) start();
		}
		else if(e.getSource() == closeBut){ // ����ؒf�����s
	    	stop();
	    	clientClose();
		}
		else if(e.getSource() == quitBut){ // �A�v���b�g�̏I��
	    	stop();
	    	clientClose();
	    	System.exit(1);
		}
		else if(e.getSource() == inputArea && client != null) {
	    	// �e�L�X�g�t�B�[���h���̕�������T�[�o�[�֑��M����
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
			// sendFile�{�^���������ꂽ���̏���
			String person = privateArea.getText();
			String fileName = inputArea.getText();
			char c5 = (char) 05;
			char c6 = (char) 06;
			String mes = String.valueOf(c5)+"sendFile"
				+String.valueOf(c6)+person+String.valueOf(c6)+fileName;
			System.out.println(mes);
			writeMes(mes);
			String mes2 = person+"����փt�@�C�����F"+fileName+"�̑��M�����݂܂�";
            freeArea.append(mes2 + "\n"); // �e�L�X�g�G���A�֏o��
			
		} else if(e.getSource() == receiveFileBut) {
			// receiveFile�{�^���������ꂽ���̏���
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
				String mes2 = person+"���񂩂�t�@�C�����F"+fileName+"�̎�M�����݂܂�";
            	freeArea.append(mes2 + "\n"); // �e�L�X�g�G���A�֏o��
            }catch(Exception e2) {
			}
		}
    }
}
