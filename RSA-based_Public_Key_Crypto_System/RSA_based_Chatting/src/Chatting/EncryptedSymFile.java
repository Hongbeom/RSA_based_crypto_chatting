package Chatting;

import java.io.Serializable;

public class EncryptedSymFile implements Serializable{	// This class was created to transfer files with AES encoded byte arrays.
	private byte[] encryptedFile;		// this variable is used for carry encrypted byte[] of file
	
	public EncryptedSymFile(byte[] encryptedFile) {	// When generate object of this class, you need byte[] of encrypted file.
		this.encryptedFile=encryptedFile;
		
	}

	public byte[] getEncryptedFile() {
		return encryptedFile;
	}

	public void setEncryptedFile(byte[] encryptedFile) {
		this.encryptedFile = encryptedFile;
	}
	

}
