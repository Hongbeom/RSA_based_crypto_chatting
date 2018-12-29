package Chatting;

import java.security.*;

public class RSASignature {
	
	public byte[] generationRSASignature(byte[] file, PrivateKey prik){  // This method create signature of file using private key and byte[] of file and returns the signature of file
		byte[] signatureBytes = null;
		Signature sig1;
		try {
			sig1 = Signature.getInstance("SHA512WithRSA");
			sig1.initSign(prik);
			sig1.update(file);
			signatureBytes = sig1.sign(); 
				
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return signatureBytes;
		
	}
	

	public boolean verifyRSASignature(byte[] file, byte[] signature, PublicKey pbk){ // this method verify that file has been corrupted or changed using byte[] of file and opponent public key. returns boolean value
		Signature sig2 ;
		boolean returns = false;

		
		try {
			sig2 = Signature.getInstance("SHA512WithRSA");
			sig2.initVerify(pbk);
			sig2.update(file);
			returns = sig2.verify(signature);

		    
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returns;
		
	}
	
	
  
}

        
  

    	
    

