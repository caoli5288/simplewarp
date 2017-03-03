package com.mengcraft.jopt;

import com.mengcraft.jopt.IllegalOptionSpecificationException;
import java.util.Iterator;
import java.util.List;

final class ParserRules {
   static final char HYPHEN_CHAR = '-';
   static final String HYPHEN = String.valueOf('-');
   static final String DOUBLE_HYPHEN = "--";
   static final String OPTION_TERMINATOR = "--";
   static final String RESERVED_FOR_EXTENSIONS = "W";

   private ParserRules() {
      throw new UnsupportedOperationException();
   }

   static boolean isShortOptionToken(String argument) {
      return argument.startsWith(HYPHEN) && !HYPHEN.equals(argument) && !isLongOptionToken(argument);
   }

   static boolean isLongOptionToken(String argument) {
      return argument.startsWith("--") && !isOptionTerminator(argument);
   }

   static boolean isOptionTerminator(String argument) {
      return "--".equals(argument);
   }

   static void ensureLegalOption(String option) {
      if(option.startsWith(HYPHEN)) {
         throw new IllegalOptionSpecificationException(String.valueOf(option));
      } else {
         for(int i = 0; i < option.length(); ++i) {
            ensureLegalOptionCharacter(option.charAt(i));
         }

      }
   }

   static void ensureLegalOptions(List options) {
      Iterator var2 = options.iterator();

      while(var2.hasNext()) {
         String each = (String)var2.next();
         ensureLegalOption(each);
      }

   }

   private static void ensureLegalOptionCharacter(char option) {
      if(!Character.isLetterOrDigit(option) && !isAllowedPunctuation(option)) {
         throw new IllegalOptionSpecificationException(String.valueOf(option));
      }
   }

   private static boolean isAllowedPunctuation(char option) {
      String allowedPunctuation = "?._-";
      return allowedPunctuation.indexOf(option) != -1;
   }
}
