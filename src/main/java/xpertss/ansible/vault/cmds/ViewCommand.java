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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;

import static xpertss.io.Charsets.*;

public final class ViewCommand implements Consumer<Path> {

   private final Vault vault;

   private ViewCommand(Vault vault)
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
            System.out.println(new String(vault.read(vaultFile), UTF_8));
         }
      } catch(Exception e) {
         System.out.format("  Skipping - %s%n", e.getMessage());
      }
      System.out.println();
   }

   public static ViewCommand create()
   {
      Console console = System.console();
      if(console == null) throw new IllegalArgumentException("Must be run from the console!");
      return new ViewCommand(new Vault(console.readPassword("Enter vault password: ")));
   }

}
