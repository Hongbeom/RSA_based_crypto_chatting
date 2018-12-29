package Chatting;

import java.security.*;
import javax.crypto.*;
import java.util.*;

public class RSAEncryption {
	private KeyPairGenerator generator;
	private KeyPair keyPair;
	byte[] pubk;
	byte[] prik;
	
	public KeyPair RSAkeygenerate() {  // This method create RSA Keypair(public key, private key)
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generator.generateKeyPair();
	}
	
	public byte[] RSAencryption(byte[] plaintext, PublicKey cpKey) { // This method carries out RSA encryption of byte[] using opponent public key and returns encrypted byte[]
		Cipher cipher;
		byte[] b0 = null;
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, cpKey);
	        b0 = cipher.doFinal(plaintext);
		} catch (Exception e) {
			e.printStackTrace();
		}
     
		return b0;
	}
	
	public byte[] RSAdecryption(byte[] ciphertext, PrivateKey myprik) {	// This method carries out RSA decryption of encrypted byte[] using my private key and returns decrypted byte[]
			Cipher deccipher;
			byte[] b1 = null;
			try {
				deccipher = Cipher.getInstance("RSA");
				deccipher.init(Cipher.DECRYPT_MODE, myprik);
		        b1 = deccipher.doFinal(ciphertext);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      
		
		return b1;
		
	}
	
   
}
