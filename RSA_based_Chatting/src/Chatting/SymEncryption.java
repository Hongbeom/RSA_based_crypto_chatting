package Chatting;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class SymEncryption {
	private KeyGenerator keyGen;
	
	public Key AESkeygeneration() {			// This method generate Symmetric key and returns it
		try {
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		Key key = keyGen.generateKey();
		return key;
	}
	
	public byte[] AESkeyEncrpytion(byte[] plaintext, Key key) { // This method carries out AES encryption using byte[] of plaintext and symmetric key and returns encrypted byte[]
		Cipher cipher;
		byte[] cipherbyte = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			try {
				cipherbyte = cipher.doFinal(plaintext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return cipherbyte;
	}
		
	public byte[] AESdecryption(byte[] cipherbyte, SecretKey key) {	// This method carries out AES decryption of encrypted byte[] using symmetric key
		Cipher cipher2;
		byte[] decryptbyte = null;
		try {
			cipher2 = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher2.init(Cipher.DECRYPT_MODE, key);
			decryptbyte = cipher2.doFinal(cipherbyte);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {

			e.printStackTrace();
		} catch (InvalidKeyException e) {

			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {

			e.printStackTrace();
		} catch (BadPaddingException e) {

			e.printStackTrace();
		}
		
		
		return decryptbyte;
	}
	
	
	}

		

