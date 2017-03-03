package com.mengcraft.jopt;

import com.mengcraft.jopt.OptionException;
import java.util.Collections;
import java.util.List;

class UnconfiguredOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   UnconfiguredOptionException(String option) {
      this(Collections.singletonList(option));
   }

   UnconfiguredOptionException(List options) {
      super(options);
   }

   Object[] messageArguments() {
      return new Object[]{this.multipleOptionString()};
   }
}
