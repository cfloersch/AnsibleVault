/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault.cmds;

import xpertss.ansible.vault.Vault;
import xpertss.ansible.vault.VaultFile;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;


public class DecryptCommand implements Consumer<Path> {

   private final Vault vault;

   private DecryptCommand(Vault vault)
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
            VaultFile vaultFile = VaultFile.read(file);
            byte[] plaintext = vault.read(vaultFile);
            try (OutputStream out = Files.newOutputStream(file)) {
               out.write(plaintext);
            }
         }
      } catch(Exception e) {
         System.out.format("  Skipping - %s%n", e.getMessage());
      }
      System.out.println();
   }

   public static DecryptCommand create()
   {
      Console console = System.console();
      if(console == null) throw new IllegalArgumentException("Must be run from the console!");
      return new DecryptCommand(new Vault(console.readPassword("Enter vault password: ")));
   }
   
}
