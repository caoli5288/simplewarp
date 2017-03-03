package com.mengcraft.jopt;

import com.mengcraft.jopt.ArgumentAcceptingOptionSpec;
import com.mengcraft.jopt.ArgumentList;
import com.mengcraft.jopt.OptionMissingRequiredArgumentException;
import com.mengcraft.jopt.OptionParser;
import com.mengcraft.jopt.OptionSet;
import java.util.Collections;

class AlternativeLongOptionSpec extends ArgumentAcceptingOptionSpec {
   AlternativeLongOptionSpec() {
      super(Collections.singletonList("W"), true, "Alternative form of long options");
      this.describedAs("opt=value");
   }

   protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
      if(!arguments.hasMore()) {
         throw new OptionMissingRequiredArgumentException(this);
      } else {
         arguments.treatNextAsLongOption();
      }
   }
}
