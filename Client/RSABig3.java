// RSABig3.java
import java.math.BigInteger;
import java.util.Random;
import java.io.*;

public class RSABig3 {

    final int SIZE = 128;
    private BigInteger d, e, N;
    
 
    // 公開鍵のeを返答
    public String getPKe() {
		return ""+e;
    }

    // 公開鍵のNを返答
    public String getPKN() {
		return ""+N;
    }

    // 公開鍵（e, N）および秘密鍵（d）の作成
    public void createKey() {
		BigInteger p, q, pp;
		BigInteger ONE = BigInteger.valueOf(1);
		// 秘密鍵のpとqを作成
		p = new BigInteger(SIZE, 10, new Random());
		do
    	{
      	    q = new BigInteger(SIZE, 10, new Random());
    	}
    	while (q.compareTo(p) == 0);

		// Nを計算
		N = p.multiply(q);

		// φを計算: pp = (p-1)(q-1)
		BigInteger left = p.subtract(ONE);
		BigInteger right = q.subtract(ONE);
		pp = left.multiply(right);

		// dを計算
		d = null;
		while(d==null) {
	    	d = new BigInteger(pp.bitLength(), new Random());
	    	if( (d.compareTo(pp) != -1) || (d.gcd(pp).compareTo(ONE) != 0) ) {
				d = null;
	    	}
		}
	
		// eを計算：e = d mod pp  の逆元の計算
		e = d.modInverse(pp);        
    }
    
    // 数値の暗号化メソッド
    public static String encrypt(String org, String es, String Ns) {
		BigInteger e = new BigInteger(es);
		BigInteger N = new BigInteger(Ns);
		BigInteger mes = changeStringToBigInteger(org);
    	BigInteger ret = mes.modPow(e, N);
		return ret.toString(); 
    }

    // 数値の復号化メソッド
    public String decrypt(String cry) {
		BigInteger mes = new BigInteger(cry);
    	BigInteger ret = mes.modPow(d, N);
		String retS = changeBigIntegerToString(ret);
    	return retS; 
    }

    // アルファベットなどの文字列を数字(BigInteger)に変換
    public static BigInteger changeStringToBigInteger(String s) {
        byte b[] = s.getBytes();
		return new BigInteger(b);
    }

    // 数字(BigInteger)をアルファベットなどの文字列に変換
    public static String changeBigIntegerToString(BigInteger bi) {
		byte b[] = bi.toByteArray();
		return new String(b);
    }
 }