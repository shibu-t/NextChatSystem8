import java.net.*;
import java.io.*;

public class FTPReceiveServer extends Thread{
	private Channel master;
	private ServerSocket serverSocket;
	private Socket socket;
	private int port = -1;
	private int startPort = 28100;
	private int endPort = 30000;
	
	public FTPReceiveServer(Channel c) {
		master = c;
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
			serverSocket.setSoTimeout(1000); 
		
			System.out.println("クライアントから" + port + "で接続待機");
			// クライアントからの接続待機
			socket = serverSocket.accept();
			System.out.println(socket.getInetAddress() + "から接続受けつけ");

			// 通信処理開始
			if(receiveFile()) { // 受信完了
				master.successReceiveFile();
			} else { // 受信失敗
				master.failReceiveFile();
			}
			
			serverSocket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			master.failReceiveFile();
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

	public boolean receiveFile() {
		File file = null; 
		int size = 0;
		try {
			// 入力ストリームの準備
			BufferedInputStream input = 
					new BufferedInputStream(socket.getInputStream());
			// 出力用ストリームの準備
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
			
			// ファイル名の文字数を受け取る
			int fileNameSize = input.read();
			
			// ファイル名を受け取る
			byte fileNameByte[] = new byte[fileNameSize];
			input.read(fileNameByte);
			byte fileNameByte2[] = CryptBlowfish.decrypt(blowFishKey, fileNameByte);
			String fileName = new String(fileNameByte2);
			System.out.println("書き込むファイル名："+fileName);
			
			// ファイル名からファイル作成の準備
			file = new File(fileName);
			FileOutputStream output = new FileOutputStream(file);
			
			//データをサーバーから受け取る準備
			byte[] buf = new byte[1032]; // 1024バイトを暗号化した時のサイズ
			int len =0;
			
			// ソケット通信によるデータの受け取りとファイルへの書き込み
			while((len=input.read(buf))!=-1){
				byte[] buf2 = CryptBlowfish.decrypt(blowFishKey, buf);
				output.write(buf2,0,buf2.length);
				System.out.println("受信量："+len);
				size += len;
				if(input.available() < 1032) {
					System.out.println("遅延行為");
					Thread.sleep(10);
				}
			}
			
			System.out.println(size+"バイトの受信＆書き込み完了");
			output.close();
		z	input.close();
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
