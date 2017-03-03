package com.mengcraft.jopt;

import com.mengcraft.jopt.OptionException;
import com.mengcraft.jopt.OptionSpec;
import java.util.Collection;
import java.util.Collections;

class MultipleArgumentsForOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   MultipleArgumentsForOptionException(OptionSpec options) {
      super((Collection)Collections.singleton(options));
   }

   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
