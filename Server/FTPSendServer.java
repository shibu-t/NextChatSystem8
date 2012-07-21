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
				// サーバーソケットの作成
				serverSocket = new ServerSocket(i);
				port = serverSocket.getLocalPort();
				break;
			} catch(Exception e) {
				// e.printStackTrace();
				System.out.println("Port:"+i+"は既に使用されているようです");
			}
		}
	}
	
	public void run() {
		try {
			// 接続タイムアウトを設定（10秒に指定）
			serverSocket.setSoTimeout(10000); 
			
			System.out.println("クライアントから" + port + "で接続待機");
			// クライアントからの接続待機
			socket = serverSocket.accept();
			System.out.println(socket.getInetAddress() + "から接続受けつけ");

			// 通信処理開始
			if(sendFile()) { // 受信完了
				master.successSendFile();
			} else { // 受信失敗
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
		
		
			//ファイルを取り込む準備
			file = new File(fileName);
			FileInputStream input = new FileInputStream(file);

			// 出力用ストリームの準備
			BufferedOutputStream output = 
				new BufferedOutputStream(socket.getOutputStream());
			// 入力ストリームの準備
			BufferedInputStream inputSocket = 
					new BufferedInputStream(socket.getInputStream());
			
			// 共通鍵の作成＆公開鍵による共有
			String blowFishKey = CryptBlowfish.createKey();
			
			String e = readString(inputSocket);
			String N = readString(inputSocket);
			String crypedBlowFishKey = RSABig3.encrypt(blowFishKey, e, N);
			writeString(output, crypedBlowFishKey);
			
			
			// ファイル名を送信
			byte fileNameByte[] = fileName.getBytes();
			byte fileNameByte2[] = CryptBlowfish.encrypt(blowFishKey, fileNameByte);
			// ファイル名の文字数（暗号化後）を送信
			output.write(fileNameByte2.length);
			output.flush();
			output.write(fileNameByte2,0,fileNameByte2.length);
			output.flush();

			// データをサーバーに送る準備
			byte[] buf = new byte[1024];
			int len=0;
			
			// ファイルからのデータ読み込みとソケット通信によるデータ送信
			while((len=input.read(buf))!=-1){
				byte[] buf2 = CryptBlowfish.encrypt(blowFishKey, buf);
				output.write(buf2,0,buf2.length);
				size += buf2.length;
			}

			System.out.println(size+"バイトの読み取り＆送信完了");
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
		// 文字数を受け取る
		int size = input.read();
		
		// 文字数分のデータを受け取る
		byte byteData[] = new byte[size];
		input.read(byteData);
		String stringData = new String(byteData);
		return stringData;
	}
	
	public void writeString(BufferedOutputStream output, String sendString) throws Exception{
		// 文字数を送信
		output.write(sendString.length());
		output.flush();
				
		// 文字数分のデータを送信
		byte byteData[] = sendString.getBytes();
		output.write(byteData,0,sendString.length());
		output.flush();
	}
}
