package com.mengcraft.jopt;

import com.mengcraft.jopt.AbstractOptionSpec;
import com.mengcraft.jopt.ArgumentList;
import com.mengcraft.jopt.OptionParser;
import com.mengcraft.jopt.OptionSet;
import java.util.Collections;
import java.util.List;

class NoArgumentOptionSpec extends AbstractOptionSpec {
   NoArgumentOptionSpec(String option) {
      this(Collections.singletonList(option), "");
   }

   NoArgumentOptionSpec(List options, String description) {
      super(options, description);
   }

   void handleOption(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions, String detectedArgument) {
      detectedOptions.add(this);
   }

   public boolean acceptsArguments() {
      return false;
   }

   public boolean requiresArgument() {
      return false;
   }

   public boolean isRequired() {
      return false;
   }

   public String argumentDescription() {
      return "";
   }

   public String argumentTypeIndicator() {
      return "";
   }

   protected Void convert(String argument) {
      return null;
   }

   public List defaultValues() {
      return Collections.emptyList();
   }
}
