/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault;

import xpertss.ansible.vault.cmds.CreateCommand;
import xpertss.ansible.vault.cmds.DecryptCommand;
import xpertss.ansible.vault.cmds.EditCommand;
import xpertss.ansible.vault.cmds.EncryptCommand;
import xpertss.ansible.vault.cmds.RekeyCommand;
import xpertss.ansible.vault.cmds.ViewCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public enum Command {

   create("Create a new encrypted vault file using the editor") {
      public void execute(Path ... files) throws IOException
      {
         if(files.length > 1) throw new IOException("unexpected file arguments found");
         CreateCommand cmd = CreateCommand.create();
         cmd.accept(files[0]);
      }
   },


   edit("Edit an existing encrypted file in the editor") {
      public void execute(Path ... files) throws IOException
      {
         if(files.length > 1) throw new IOException("unexpected file arguments found");
         EditCommand cmd = EditCommand.create();
         cmd.accept(files[0]);
      }
   },


   rekey("Rekey one or more existing encrypted files") {
      public void execute(Path ... files) throws IOException
      {
         RekeyCommand cmd = RekeyCommand.create();
         for(Path file : files) cmd.accept(file);
      }
   },


   encrypt("Encrypt one or more existing unencrypted files") {
      public void execute(Path ... files) throws IOException
      {
         EncryptCommand cmd = EncryptCommand.create();
         for(Path file : files) cmd.accept(file);
      }
   },


   decrypt("Decrypt one or more existing encrypted files") {
      public void execute(Path ... files) throws IOException
      {
         DecryptCommand cmd = DecryptCommand.create();
         for(Path file : files) cmd.accept(file);
      }
   },


   view("View in stdout the contents of one or more encrypted files") {
      public void execute(Path ... files) throws IOException
      {
         ViewCommand cmd = ViewCommand.create();
         for(Path file : files) cmd.accept(file);
      }
   };


   private String desc;

   private Command(String desc)
   {
      this.desc = desc;
   }

   public String getDescription()
   {
      return desc;
   }

   public abstract void execute(Path ... files) throws IOException;


}
