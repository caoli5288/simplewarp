package com.mengcraft.jopt;

import com.mengcraft.jopt.ArgumentAcceptingOptionSpec;
import com.mengcraft.jopt.ArgumentList;
import com.mengcraft.jopt.OptionMissingRequiredArgumentException;
import com.mengcraft.jopt.OptionParser;
import com.mengcraft.jopt.OptionSet;
import java.util.List;

class RequiredArgumentOptionSpec extends ArgumentAcceptingOptionSpec {
   RequiredArgumentOptionSpec(String option) {
      super(option, true);
   }

   RequiredArgumentOptionSpec(List options, String description) {
      super(options, true, description);
   }

   protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
      if(!arguments.hasMore()) {
         throw new OptionMissingRequiredArgumentException(this);
      } else {
         this.addArguments(detectedOptions, arguments.next());
      }
   }
}
