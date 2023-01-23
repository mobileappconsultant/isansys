package com.isansys.patientgateway;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AES
{
	public static final byte[] 	default_key_256 	= {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32};
    public static final byte[] 	default_init_vector	= {5,6,8,32,47,83,4,6,62,35,48,5,96,4,54,13};
    public static final int 	aesDataChunkSize 	= 16;    
    
    public static final byte[] 	default_key_128		= {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};

    private static final String cipherTransformationECB = "AES/ECB/ZeroBytePadding";
    private static final String cipherTransformationCBF = "AES/CFB/ZeroBytePadding";
    private static final String cipherTransformationCBC = "AES/CBC/ZeroBytePadding";
    private static final String aesEncryptionAlgorithm = "AES";

    public static byte[] decrypt(byte[] cipherText, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        SecretKeySpec sks = new SecretKeySpec(key, aesEncryptionAlgorithm);

        byte[] padded_plaintext = new byte[cipherText.length];

        Cipher cipher = Cipher.getInstance(cipherTransformationECB);
        cipher.init(Cipher.DECRYPT_MODE, sks);

        byte [] unpadded_plaintext =  cipher.doFinal(cipherText);

        /* We're using zero byte padding, so trailing zeros will be dropped even if they were valid data.
         * So we copy the decoded plaintext to a byte array of the same size as the ciphertext to re-append the zeros.
         */
        System.arraycopy(unpadded_plaintext, 0, padded_plaintext, 0, unpadded_plaintext.length);

        return padded_plaintext;
    }

    public static byte[] encrypt(byte[] plainText, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
    	SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        
    	Cipher cipher = Cipher.getInstance(cipherTransformationECB);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        return cipher.doFinal(plainText);
    }

    public static byte[] decryptCBF(byte[] cipherText, byte[] key, byte[] IV) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        SecretKeySpec sks = new SecretKeySpec(key, aesEncryptionAlgorithm);

        byte[] padded_plaintext = new byte[cipherText.length];

        Cipher cipher = Cipher.getInstance(cipherTransformationCBF);
        cipher.init(Cipher.DECRYPT_MODE, sks,new IvParameterSpec(IV));
        cipher.update(cipherText);

        byte [] unpadded_plaintext =  cipher.doFinal();

        /* We're using zero byte padding, so trailing zeros will be dropped even if they were valid data.
        * So we copy the decoded plaintext to a byte array of the same size as the ciphertext to re-append the zeros.
        */
        System.arraycopy(unpadded_plaintext, 0, padded_plaintext, 0, unpadded_plaintext.length);

        return padded_plaintext;
    }

    public static byte[] encryptCBF(byte[] plainText, byte[] key, byte[] IV) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
    	SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        
    	Cipher cipher = Cipher.getInstance(cipherTransformationCBF);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV));

        return cipher.doFinal(plainText);
    }

    public static byte[] encryptCBC(byte[] plainText, byte[] key, byte[] IV) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);

        Cipher cipher = Cipher.getInstance(cipherTransformationCBC);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV));

        return cipher.doFinal(plainText);
    }

    //  Following used for over the wire encryption to/from WebServices
    
    // We currently only use the one format of encryption, but may be extended in the future to include Public/Private keys and compression
    public enum OverTheWireEncryptionType 
    {
    	none,
    	CBS_WITH_PKCS5PADDING,
    }
    
    private static final byte[] key_Array = {(byte)0x61 ,(byte)0x12, (byte)0x16, (byte)0x65, (byte)0x18, (byte)0xAC, (byte)0x79, (byte)0x97,
    	(byte)0x1D, (byte)0x21, (byte)0x1F, (byte)0x66, (byte)0xEE, (byte)0xED, (byte)0xC9, (byte)0x24,
    	(byte)0x1C, (byte)0x02, (byte)0x32, (byte)0x6B, (byte)0xE8, (byte)0x58, (byte)0x0E, (byte)0xE7, 
    	(byte)0xDE, (byte)0x86, (byte)0x16, (byte)0x93, (byte)0x44, (byte)0x92, (byte)0x18, (byte)0x32 };
    

    public static String encryptCBSWithPadding(String strToEncrypt) throws NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException
    {       
        Cipher _Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");        
        byte[] ivArr = { 1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7 };
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivArr);
        
        Key SecretKey = new SecretKeySpec(key_Array, "AES");    
        _Cipher.init(Cipher.ENCRYPT_MODE, SecretKey, ivParameterSpec);

        return Base64.encodeToString(_Cipher.doFinal(strToEncrypt.getBytes()), Base64.DEFAULT);    
    }

    public static String decryptCBSWithPadding(String EncryptedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher _Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");            
        byte[] ivArr = { 1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7 };
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivArr);

        Key SecretKey = new SecretKeySpec(key_Array, "AES");
        _Cipher.init(Cipher.DECRYPT_MODE, SecretKey, ivParameterSpec);

        byte[] DecodedMessage = Base64.decode(EncryptedMessage, Base64.DEFAULT);
        return new String(_Cipher.doFinal(DecodedMessage));
    }
}