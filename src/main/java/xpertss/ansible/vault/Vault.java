/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault;

import xpertss.lang.Characters;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;


public final class Vault {

   private final SecureRandom random = new SecureRandom();
   private final char[] passwd;

   public Vault(char[] passwd)
   {
      this.passwd = Characters.clone(passwd);
      Arrays.fill(passwd, '\0');
   }

   public VaultFile write(byte[] plaintext) throws Exception
   {
      byte[] salt = generateSalt();
      byte[] pbeKey = generatePBEKey(salt);
      byte[] ciphertext = crypt(pbeKey, plaintext);
      byte[] digest = digest(pbeKey, ciphertext);
      return new VaultFile(salt, digest, ciphertext);
   }

   public byte[] read(VaultFile file) throws Exception
   {
      byte[] salt = file.getSalt();
      byte[] pbeKey = generatePBEKey(salt);
      byte[] hmac = digest(pbeKey, file.getCiphertext());
      if(!Arrays.equals(hmac, file.getHmac()))
         throw new IllegalArgumentException("hmac verification failed");
      return decrypt(pbeKey, file.getCiphertext());
   }


   private byte[] digest(byte[] pbeKey, byte[] ciphertext) throws Exception
   {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(generateHmacKey(pbeKey));
      return mac.doFinal(ciphertext);
   }

   private byte[] decrypt(byte[] pbeKey, byte[] ciphertext) throws Exception
   {
      SecretKey key = createAESKey(pbeKey);
      Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding", "BC");
      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(pbeKey, 64, 16));
      return cipher.doFinal(ciphertext);
   }

   private byte[] crypt(byte[] pbeKey, byte[] plaintext) throws Exception
   {
      SecretKey key = createAESKey(pbeKey);
      Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding", "BC");
      cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(pbeKey, 64, 16));
      return cipher.doFinal(plaintext);
   }

   // AES block size is 128 regardless of key size.

   private byte[] generateSalt()
   {
      byte[] salt = new byte[16];
      random.nextBytes(salt);
      return salt;
   }

   private byte[] generatePBEKey(byte[] salt) throws Exception
   {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256", "BC");
      KeySpec keyspec = new PBEKeySpec(passwd, salt, 10000, 640);
      return factory.generateSecret(keyspec).getEncoded();
   }

   private SecretKey createAESKey(byte[] pbeKey) throws Exception
   {
      SecretKeySpec keySpec = new SecretKeySpec(pbeKey, 0, 32, "AES");
      SecretKeyFactory factory = SecretKeyFactory.getInstance("AES", "BC");
      return factory.generateSecret(keySpec);
   }

   private SecretKey generateHmacKey(byte[] pbeKey) throws Exception
   {
      return new SecretKeySpec(pbeKey, 32, 32, "HmacSHA256");
   }

}
