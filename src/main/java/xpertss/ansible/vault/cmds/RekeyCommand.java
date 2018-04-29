/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault.cmds;

import xpertss.ansible.vault.Vault;
import xpertss.ansible.vault.VaultFile;

import java.io.Console;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;

public class RekeyCommand implements Consumer<Path> {

   private final Vault oVault;
   private final Vault nVault;

   private RekeyCommand(Vault oVault, Vault nVault)
   {
      this.oVault = oVault;
      this.nVault = nVault;
   }


   @Override
   public void accept(Path file)
   {
      System.out.format("%s%n", file.toString());
      try {
         if(!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            throw new RuntimeException("file not found");
         } else {
            VaultFile oVaultFile = VaultFile.read(file);
            byte[] plaintext = oVault.read(oVaultFile);
            VaultFile nVaultFile = nVault.write(plaintext);
            try(OutputStream out = Files.newOutputStream(file)) {
               out.write(nVaultFile.getEncoded());
            }
         }
      } catch(Exception e) {
         System.out.format("  Skipping - %s%n", e.getMessage());
      }
      System.out.println();
   }

   public static RekeyCommand create()
   {
      Console console = System.console();
      if(console == null) throw new IllegalArgumentException("Must be run from the console!");
      return new RekeyCommand(new Vault(console.readPassword("Enter OLD vault password: ")),
                                 new Vault(console.readPassword("Enter NEW vault password: ")));
   }


}
