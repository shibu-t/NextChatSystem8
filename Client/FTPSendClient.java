import java.net.*;
import java.io.*;

public class FTPSendClient {

	public static void fileSend(String fileName, String host, int port) {
		File file = null; 
		int size = 0;
		try {
			System.out.println("���M����t�@�C�����F"+fileName);
			//�t�@�C������荞�ޏ���
			file = new File(fileName);
			FileInputStream input = new FileInputStream(file);

			// �\�P�b�g�𐶐�
			Socket socket = new Socket(host, port);

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
				
			
			
			System.out.println("key:"+blowFishKey);
			
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
				System.out.println("checker:"+buf2.length);
			}

			System.out.println(size+"�o�C�g�̓ǂݎ�聕���M����");
			output.close();
			input.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
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