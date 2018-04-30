package xpertss.ansible.vault;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import xpertss.util.Platform;

import java.nio.file.Path;
import java.security.Security;
import java.util.Arrays;

public class Main {


   public static void main(String[] args) throws Exception
   {
      if(args.length < 2) usage(null);
      try {
         Command cmd = Command.valueOf(args[0]);
         Security.addProvider(new BouncyCastleProvider());
         try {
            cmd.execute(process(Arrays.copyOfRange(args, 1, args.length)));
         } catch(Exception e) {
            usage(e.getMessage());
         }
      } catch(IllegalArgumentException e) {
         usage(null);
      }
      System.exit(0);
   }


   private static Path[] process(String ... files)
   {
      // TODO Enhance this to handle wildcard patterns
      Path working = Platform.workingDir();
      return Arrays.stream(files).map(file -> working.resolve(file)).toArray(Path[]::new);
   }

   private static void usage(String msg)
   {
      if(msg != null) System.out.format("%s%n%n", msg);
      System.out.println("ansible-vaule <action> <file1> [file2] ... [fileN]");
      System.out.println("Actions");
      for(Command cmd : Command.values()) {
         System.out.format("  %-8s - %s%n", cmd.name(), cmd.getDescription());
      }
      System.exit(1);
   }


   
}