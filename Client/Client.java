import java.io.*;   
import java.net.*;

public class Client {
    Socket sok = null;              // 接続用Socket
    BufferedReader in = null;       // 入力用ストリーム
    OutputStreamWriter out = null;  // 出力用ストリーム

    public Client() {
    }

    public void connectServer(String host, int port) throws Exception {
	sok = new Socket(host, port);
	// ソケットから入出力ストリームを得る
        in = new BufferedReader(
                new InputStreamReader(sok.getInputStream()));
        out = new OutputStreamWriter(sok.getOutputStream());
    }

    // メッセージ送信 
    public void write(String Message) throws Exception{
        out.write(Message + "\n");
	out.flush();
    }

    // メッセージ受信 
    public String read() throws Exception{
        String readString = null;
        readString = in.readLine();
        return readString;
    }

    // 接続を切断する
    public void close() throws Exception{
        sok.close();    // ソケットを閉じる
        sok = null;
    }
}
