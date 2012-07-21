public class CryptBlowfish {
    // �Í����i��������Í������Õ��ɕϊ��j
	public static byte[] encrypt(String key, byte[] data) throws Exception {
		javax.crypto.spec.SecretKeySpec sksSpec = 
    		new javax.crypto.spec.SecretKeySpec(key.getBytes(), "Blowfish");
    	javax.crypto.Cipher cipher = 
    		javax.crypto.Cipher.getInstance("Blowfish");
       	cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, sksSpec);
    	byte[] encrypted = cipher.doFinal(data);
   		return encrypted;
	}

    // �����i�Í������ꂽ������𕜍��������֕ϊ��j
	public static byte[] decrypt(String key, byte[] encrypted) throws Exception {
		javax.crypto.spec.SecretKeySpec sksSpec = 
        	new javax.crypto.spec.SecretKeySpec(key.getBytes(), "Blowfish");
    	javax.crypto.Cipher cipher = 
        	javax.crypto.Cipher.getInstance("Blowfish");
    	cipher.init(javax.crypto.Cipher.DECRYPT_MODE, sksSpec);
    	byte[] decrypted = cipher.doFinal(encrypted);
    	return decrypted;
	}
	
	public static String createKey() {
		String key = "";
		//int num = (int)(Math.random()*10+10);
		int num = 8;
		for(int i=0; i<num; i++) {
			// �Ƃ肠�����啶���A���t�@�x�b�g�݂̂ɂ�
			char c1 = (char)(int)(Math.random()*26 + 65);
			key += c1;
		}
		return key;
	}
	
}
