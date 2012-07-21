import java.net.*;
import java.io.*;

public class FTPReceiveClient {


	public static boolean fileReceive(String host, int port) {
		File file = null; 
		int size = 0;
		Socket socket = null;
		try {
		
			socket = new Socket(host,port);
			System.out.println("�ڑ�����");
		
			// ���̓X�g���[���̏���
			BufferedInputStream input = 
					new BufferedInputStream(socket.getInputStream());
			// �o�͗p�X�g���[���̏���
			BufferedOutputStream outputSocket = 
				new BufferedOutputStream(socket.getOutputStream());
			
			
			RSABig3 rsa = new RSABig3();
			rsa.createKey();
			String e = rsa.getPKe();
			String N = rsa.getPKN();
			
			writeString(outputSocket, e);
			writeString(outputSocket, N);
			String blowFishKey = rsa.decrypt( readString(input) );
			System.out.println("blowFishKey:"+blowFishKey);
			
			// �t�@�C�����̕��������󂯎��
			int fileNameSize = input.read();
			
			// �t�@�C�������󂯎��
			byte fileNameByte[] = new byte[fileNameSize];
			input.read(fileNameByte);
			byte fileNameByte2[] = CryptBlowfish.decrypt(blowFishKey, fileNameByte);
			String fileName = new String(fileNameByte2);
			System.out.println("�������ރt�@�C�����F"+fileName);
			
			// �t�@�C��������t�@�C���쐬�̏���
			file = new File(fileName);
			FileOutputStream output = new FileOutputStream(file);
			
			//�f�[�^���T�[�o�[����󂯎�鏀��
			byte[] buf = new byte[1032]; // 1024�o�C�g���Í����������̃T�C�Y
			int len =0;
			
			// �\�P�b�g�ʐM�ɂ��f�[�^�̎󂯎��ƃt�@�C���ւ̏�������
			while((len=input.read(buf))!=-1){
				byte[] buf2 = CryptBlowfish.decrypt(blowFishKey, buf);
				output.write(buf2,0,buf2.length);
				size += len;
				if(input.available() < 1032) {
					System.out.println("�x���s��");
					Thread.sleep(10);
				}
			}
			
			System.out.println(size+"�o�C�g�̎�M���������݊���");
			output.close();
			input.close();
			socket.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}
	
	}
	
	
	public static String readString(BufferedInputStream input) throws Exception{
		// ���������󂯎��
		int size = input.read();
		
		// ���������̃f�[�^���󂯎��
		byte byteData[] = new byte[size];
		input.read(byteData);
		String stringData = new String(byteData);
		return stringData;
	}
	
	public static void writeString(BufferedOutputStream output, String sendString) throws Exception{
		// �������𑗐M
		output.write(sendString.length());
		output.flush();
				
		// ���������̃f�[�^�𑗐M
		byte byteData[] = sendString.getBytes();
		output.write(byteData,0,sendString.length());
		output.flush();
	}


}