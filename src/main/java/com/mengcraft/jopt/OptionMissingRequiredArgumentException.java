package com.mengcraft.jopt;

import com.mengcraft.jopt.OptionException;
import com.mengcraft.jopt.OptionSpec;
import java.util.Arrays;
import java.util.Collection;

class OptionMissingRequiredArgumentException extends OptionException {
   private static final long serialVersionUID = -1L;

   OptionMissingRequiredArgumentException(OptionSpec option) {
      super((Collection)Arrays.asList(new OptionSpec[]{option}));
   }

   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
