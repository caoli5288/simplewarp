package com.mengcraft.jopt;

import com.mengcraft.jopt.OptionException;
import java.util.Collections;

class IllegalOptionSpecificationException extends OptionException {
   private static final long serialVersionUID = -1L;

   IllegalOptionSpecificationException(String option) {
      super(Collections.singletonList(option));
   }

   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
