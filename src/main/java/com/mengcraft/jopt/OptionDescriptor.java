package com.mengcraft.jopt;

import java.util.List;

public interface OptionDescriptor {
   List options();

   String description();

   List defaultValues();

   boolean isRequired();

   boolean acceptsArguments();

   boolean requiresArgument();

   String argumentDescription();

   String argumentTypeIndicator();

   boolean representsNonOptions();
}
