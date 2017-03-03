package com.mengcraft.jopt;

import com.mengcraft.jopt.ArgumentAcceptingOptionSpec;
import com.mengcraft.jopt.ArgumentList;
import com.mengcraft.jopt.OptionParser;
import com.mengcraft.jopt.OptionSet;
import java.util.List;

class OptionalArgumentOptionSpec extends ArgumentAcceptingOptionSpec {
   OptionalArgumentOptionSpec(String option) {
      super(option, false);
   }

   OptionalArgumentOptionSpec(List options, String description) {
      super(options, false, description);
   }

   protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
      if(arguments.hasMore()) {
         String nextArgument = arguments.peek();
         if(!parser.looksLikeAnOption(nextArgument)) {
            this.handleOptionArgument(parser, detectedOptions, arguments);
         } else if(this.isArgumentOfNumberType() && this.canConvertArgument(nextArgument)) {
            this.addArguments(detectedOptions, arguments.next());
         } else {
            detectedOptions.add(this);
         }
      } else {
         detectedOptions.add(this);
      }

   }

   private void handleOptionArgument(OptionParser parser, OptionSet detectedOptions, ArgumentList arguments) {
      if(parser.posixlyCorrect()) {
         detectedOptions.add(this);
         parser.noMoreOptions();
      } else {
         this.addArguments(detectedOptions, arguments.next());
      }

   }
}
