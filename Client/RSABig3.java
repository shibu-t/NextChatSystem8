// RSABig3.java
import java.math.BigInteger;
import java.util.Random;
import java.io.*;

public class RSABig3 {

    final int SIZE = 128;
    private BigInteger d, e, N;
    
 
    // ���J����e��ԓ�
    public String getPKe() {
		return ""+e;
    }

    // ���J����N��ԓ�
    public String getPKN() {
		return ""+N;
    }

    // ���J���ie, N�j����є閧���id�j�̍쐬
    public void createKey() {
		BigInteger p, q, pp;
		BigInteger ONE = BigInteger.valueOf(1);
		// �閧����p��q���쐬
		p = new BigInteger(SIZE, 10, new Random());
		do
    	{
      	    q = new BigInteger(SIZE, 10, new Random());
    	}
    	while (q.compareTo(p) == 0);

		// N���v�Z
		N = p.multiply(q);

		// �ӂ��v�Z: pp = (p-1)(q-1)
		BigInteger left = p.subtract(ONE);
		BigInteger right = q.subtract(ONE);
		pp = left.multiply(right);

		// d���v�Z
		d = null;
		while(d==null) {
	    	d = new BigInteger(pp.bitLength(), new Random());
	    	if( (d.compareTo(pp) != -1) || (d.gcd(pp).compareTo(ONE) != 0) ) {
				d = null;
	    	}
		}
	
		// e���v�Z�Fe = d mod pp  �̋t���̌v�Z
		e = d.modInverse(pp);        
    }
    
    // ���l�̈Í������\�b�h
    public static String encrypt(String org, String es, String Ns) {
		BigInteger e = new BigInteger(es);
		BigInteger N = new BigInteger(Ns);
		BigInteger mes = changeStringToBigInteger(org);
    	BigInteger ret = mes.modPow(e, N);
		return ret.toString(); 
    }

    // ���l�̕��������\�b�h
    public String decrypt(String cry) {
		BigInteger mes = new BigInteger(cry);
    	BigInteger ret = mes.modPow(d, N);
		String retS = changeBigIntegerToString(ret);
    	return retS; 
    }

    // �A���t�@�x�b�g�Ȃǂ̕�����𐔎�(BigInteger)�ɕϊ�
    public static BigInteger changeStringToBigInteger(String s) {
        byte b[] = s.getBytes();
		return new BigInteger(b);
    }

    // ����(BigInteger)���A���t�@�x�b�g�Ȃǂ̕�����ɕϊ�
    public static String changeBigIntegerToString(BigInteger bi) {
		byte b[] = bi.toByteArray();
		return new String(b);
    }
 }