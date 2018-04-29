/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault.cmds;

import xpertss.ansible.vault.Vault;
import xpertss.ansible.vault.VaultFile;
import xpertss.io.IOUtils;

import java.io.Console;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;

public class EncryptCommand implements Consumer<Path> {

   private final Vault vault;

   private EncryptCommand(Vault vault)
   {
      this.vault = vault;
   }


   @Override
   public void accept(Path file)
   {
      System.out.format("%s%n", file.toString());
      try {
         if(!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            throw new RuntimeException("file not found");
         } else {
            try (InputStream in = Files.newInputStream(file)) {
               byte[] plaintext = IOUtils.getBytes(in);
               VaultFile vaultFile = vault.write(plaintext);
               try(OutputStream out = Files.newOutputStream(file)) {
                  out.write(vaultFile.getEncoded());
               }
            }
         }
      } catch(Exception e) {
         System.out.format("  Skipping - %s%n", e.getMessage());
      }
      System.out.println();
   }

   public static EncryptCommand create()
   {
      Console console = System.console();
      if(console == null) throw new IllegalArgumentException("Must be run from the console!");
      return new EncryptCommand(new Vault(console.readPassword("Enter vault password: ")));
   }


}
