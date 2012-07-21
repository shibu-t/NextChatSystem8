import java.net.*;
import java.io.*;

public class FTPSendServer extends Thread{
	private Channel master;
	private ServerSocket serverSocket;
	private Socket socket;
	private int port = -1;
	private String fileName;
	private int startPort = 28100;
	private int endPort = 30000;
	
	public FTPSendServer(Channel c, String fn) {
		master = c;
		fileName = fn;
		for(int i=startPort; i<endPort; i++) {
			try {
				// �T�[�o�[�\�P�b�g�̍쐬
				serverSocket = new ServerSocket(i);
				port = serverSocket.getLocalPort();
				break;
			} catch(Exception e) {
				// e.printStackTrace();
				System.out.println("Port:"+i+"�͊��Ɏg�p����Ă���悤�ł�");
			}
		}
	}
	
	public void run() {
		try {
			// �ڑ��^�C���A�E�g��ݒ�i10�b�Ɏw��j
			serverSocket.setSoTimeout(10000); 
			
			System.out.println("�N���C�A���g����" + port + "�Őڑ��ҋ@");
			// �N���C�A���g����̐ڑ��ҋ@
			socket = serverSocket.accept();
			System.out.println(socket.getInetAddress() + "����ڑ��󂯂�");

			// �ʐM�����J�n
			if(sendFile()) { // ��M����
				master.successSendFile();
			} else { // ��M���s
				master.failSendFile();
			}
			serverSocket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			master.failSendFile();
			allClose();
		}
	}
	
	
	public void allClose() {
		try {
			serverSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getPort() {
		return port;
	}


	public boolean sendFile() {
		File file = null; 
		int size = 0;
		try {
		
		
			//�t�@�C������荞�ޏ���
			file = new File(fileName);
			FileInputStream input = new FileInputStream(file);

			// �o�͗p�X�g���[���̏���
			BufferedOutputStream output = 
				new BufferedOutputStream(socket.getOutputStream());
			// ���̓X�g���[���̏���
			BufferedInputStream inputSocket = 
					new BufferedInputStream(socket.getInputStream());
			
			// ���ʌ��̍쐬�����J���ɂ�鋤�L
			String blowFishKey = CryptBlowfish.createKey();
			
			String e = readString(inputSocket);
			String N = readString(inputSocket);
			String crypedBlowFishKey = RSABig3.encrypt(blowFishKey, e, N);
			writeString(output, crypedBlowFishKey);
			
			
			// �t�@�C�����𑗐M
			byte fileNameByte[] = fileName.getBytes();
			byte fileNameByte2[] = CryptBlowfish.encrypt(blowFishKey, fileNameByte);
			// �t�@�C�����̕������i�Í�����j�𑗐M
			output.write(fileNameByte2.length);
			output.flush();
			output.write(fileNameByte2,0,fileNameByte2.length);
			output.flush();

			// �f�[�^���T�[�o�[�ɑ��鏀��
			byte[] buf = new byte[1024];
			int len=0;
			
			// �t�@�C������̃f�[�^�ǂݍ��݂ƃ\�P�b�g�ʐM�ɂ��f�[�^���M
			while((len=input.read(buf))!=-1){
				byte[] buf2 = CryptBlowfish.encrypt(blowFishKey, buf);
				output.write(buf2,0,buf2.length);
				size += buf2.length;
			}

			System.out.println(size+"�o�C�g�̓ǂݎ�聕���M����");
			output.close();
			input.close();
			socket.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String readString(BufferedInputStream input) throws Exception{
		// ���������󂯎��
		int size = input.read();
		
		// ���������̃f�[�^���󂯎��
		byte byteData[] = new byte[size];
		input.read(byteData);
		String stringData = new String(byteData);
		return stringData;
	}
	
	public void writeString(BufferedOutputStream output, String sendString) throws Exception{
		// �������𑗐M
		output.write(sendString.length());
		output.flush();
				
		// ���������̃f�[�^�𑗐M
		byte byteData[] = sendString.getBytes();
		output.write(byteData,0,sendString.length());
		output.flush();
	}
}
