package Chatting;

import java.io.Serializable;

public class EncryptedSymKey implements Serializable{  // This class was created to send RSA encrypted symmetric keys.
	private byte[] encrpytedSymKey;		// this variable is used for carry encrypted symmetric key
	
	public EncryptedSymKey(byte[] encryptedSymKey) { // When generate object of this class, you need byte[] of encrypted symmetric key.
		this.encrpytedSymKey=encryptedSymKey;
		
	}

	public byte[] getEncrpytedSymKey() {
		return encrpytedSymKey;
	}

	public void setEncrpytedSymKey(byte[] encrpytedSymKey) {
		this.encrpytedSymKey = encrpytedSymKey;
	}
}
