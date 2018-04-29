/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault.cmds;

import xpertss.ansible.vault.Vault;
import xpertss.ansible.vault.VaultFile;
import xpertss.ansible.vault.ui.EditorDialog;

import javax.swing.*;
import java.awt.*;
import java.io.Console;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;

import static xpertss.io.Charsets.UTF_8;

public class CreateCommand implements Consumer<Path> {

   private final Vault vault;

   private CreateCommand(Vault vault)
   {
      this.vault = vault;
   }

   @Override
   public void accept(Path file)
   {
      if(Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
         throw new RuntimeException(file.toString() + " already exists!");
      } else {
         System.out.format("%s%n", file.toString());
         try {
            EditorDialog dialog = new EditorDialog(null);
            if(dialog.display() == JOptionPane.OK_OPTION) {
               String plaintext = dialog.getText();

               VaultFile nVaultFile = vault.write(plaintext.getBytes(UTF_8));
               try(OutputStream out = Files.newOutputStream(file)) {
                  out.write(nVaultFile.getEncoded());
               }
               System.out.println("  Created!");
            } else {
               System.out.println("  Canceled!");
            }
         } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
         }
      }
   }

   public static CreateCommand create()
   {
      Console console = System.console();
      if(console == null) throw new IllegalArgumentException("Must be run from the console!");
      return new CreateCommand(new Vault(console.readPassword("Enter vault password: ")));
   }


}
