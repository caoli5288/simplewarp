package com.mengcraft.jopt;

import com.mengcraft.jopt.OptionException;
import java.util.Collection;
import java.util.List;

class MissingRequiredOptionsException extends OptionException {
   private static final long serialVersionUID = -1L;

   protected MissingRequiredOptionsException(List missingRequiredOptions) {
      super((Collection)missingRequiredOptions);
   }

   Object[] messageArguments() {
      return new Object[]{this.multipleOptionString()};
   }
}
