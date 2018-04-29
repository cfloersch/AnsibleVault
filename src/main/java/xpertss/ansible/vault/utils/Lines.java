/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault.utils;

import xpertss.lang.Numbers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Lines {

   public static String[] parse(String str) throws IOException
   {
      try (BufferedReader br = new BufferedReader(new StringReader(str))) {
         return br.lines().toArray(String[]::new);
      }
   }

   public static String flatten(BufferedReader br) throws IOException
   {
      StringBuilder builder = new StringBuilder();
      String line = null;
      while((line = br.readLine()) != null) {
         builder.append(line);
      }
      return builder.toString();
   }

   public static Iterable<String> partition(String input, int length)
   {
      Numbers.gt(0, length, "length");
      List<String> lines = new ArrayList<>();
      while(input.length() > length) {
         lines.add(input.substring(0, length));
         input = input.substring(length);
      }
      lines.add(input);
      return lines;
   }
}
