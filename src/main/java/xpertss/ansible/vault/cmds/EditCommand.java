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

public class EditCommand implements Consumer<Path> {

   private final Vault vault;

   private EditCommand(Vault vault)
   {
      this.vault = vault;
   }

   @Override
   public void accept(Path file)
   {
      if(!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
         throw new RuntimeException("No such file " + file.toString());
      } else {
         System.out.format("%s%n", file.toString());
         try {
            VaultFile oVaultFile = VaultFile.read(file);
            String originaltext = new String(vault.read(oVaultFile), UTF_8);
            EditorDialog dialog = new EditorDialog(originaltext);
            if(dialog.display() == JOptionPane.OK_OPTION) {
               String plaintext = dialog.getText();

               VaultFile nVaultFile = vault.write(plaintext.getBytes(UTF_8));
               try(OutputStream out = Files.newOutputStream(file)) {
                  out.write(nVaultFile.getEncoded());
               }
               System.out.println("  Updated!");
            } else {
               System.out.println("  Canceled!");
            }
         } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
         }
      }
   }

   public static EditCommand create()
   {
      Console console = System.console();
      if(console == null) throw new IllegalArgumentException("Must be run from the console!");
      return new EditCommand(new Vault(console.readPassword("Enter vault password: ")));
   }

}
