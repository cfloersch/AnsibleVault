/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault;



import xpertss.ansible.vault.utils.Lines;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static xpertss.ansible.vault.utils.BinAscii.hexlify;
import static xpertss.ansible.vault.utils.BinAscii.unhexlify;
import static xpertss.io.Charsets.US_ASCII;

// Each line is 80 chars long at most
public final class VaultFile {

   private static final String HEADER = "$ANSIBLE_VAULT;1.1;AES256";
   private static final byte NEWLINE = (byte) 0x0A;

   private byte[] salt;
   private byte[] hmac;
   private byte[] ciphertext;

   VaultFile(byte[] salt, byte[] hmac, byte[] ciphertext)
   {
      this.salt = salt;
      this.hmac = hmac;
      this.ciphertext = ciphertext;
   }

   public byte[] getSalt()
   {
      return salt;
   }

   public byte[] getHmac()
   {
      return hmac;
   }

   public byte[] getCiphertext()
   {
      return ciphertext;
   }

   public byte[] getEncoded()
   {
      return toString().getBytes(US_ASCII);
   }

   public String toString()
   {
      StringBuilder content = new StringBuilder();
      content.append(hexlify(salt)).append("\n");
      content.append(hexlify(hmac)).append("\n");
      content.append(hexlify(ciphertext));
      byte[] rawbytes = content.toString().getBytes(US_ASCII);

      StringBuilder builder = new StringBuilder();
      builder.append(HEADER).append("\n");
      for(String line : Lines.partition(hexlify(rawbytes), 80)) {
         builder.append(line).append("\n");
      }
      return builder.toString();
   }


   public static VaultFile read(Path path) throws Exception
   {
      try (BufferedReader br = Files.newBufferedReader(path, US_ASCII)) {
         String header = br.readLine();
         if(!HEADER.equals(header)) throw new IllegalArgumentException("Not an ansible vault encoded file");
         String[] parts = Lines.parse(new String(unhexlify(Lines.flatten(br)), US_ASCII));
         if(parts.length != 3) throw new IllegalArgumentException("Corrupt ansible vault encoded file");
         return new VaultFile(unhexlify(parts[0]), unhexlify(parts[1]), unhexlify(parts[2]));
      }
   }

}
