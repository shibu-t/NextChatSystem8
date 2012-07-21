public class CryptBlowfish {
    // 暗号化（文字列を暗号化し暗文に変換）
	public static byte[] encrypt(String key, byte[] data) throws Exception {
		javax.crypto.spec.SecretKeySpec sksSpec = 
    		new javax.crypto.spec.SecretKeySpec(key.getBytes(), "Blowfish");
    	javax.crypto.Cipher cipher = 
    		javax.crypto.Cipher.getInstance("Blowfish");
       	cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, sksSpec);
    	byte[] encrypted = cipher.doFinal(data);
   		return encrypted;
	}

    // 復号（暗号化された文字列を復号し平文へ変換）
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
			// とりあえず大文字アルファベットのみにて
			char c1 = (char)(int)(Math.random()*26 + 65);
			key += c1;
		}
		return key;
	}
	
}
