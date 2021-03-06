package com.mengcraft.jopt;

import com.mengcraft.jopt.AbstractOptionSpec;
import com.mengcraft.jopt.ArgumentList;
import com.mengcraft.jopt.OptionParser;
import com.mengcraft.jopt.OptionSet;
import com.mengcraft.jopt.ValueConverter;
import com.mengcraft.jopt.internal.Reflection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NonOptionArgumentSpec extends AbstractOptionSpec {
   static final String NAME = "[arguments]";
   private ValueConverter converter;
   private String argumentDescription;

   NonOptionArgumentSpec() {
      this("");
   }

   NonOptionArgumentSpec(String description) {
      super(Arrays.asList(new String[]{"[arguments]"}), description);
      this.argumentDescription = "";
   }

   public NonOptionArgumentSpec ofType(Class argumentType) {
      this.converter = Reflection.findConverter(argumentType);
      return this;
   }

   public final NonOptionArgumentSpec withValuesConvertedBy(ValueConverter aConverter) {
      if(aConverter == null) {
         throw new NullPointerException("illegal null converter");
      } else {
         this.converter = aConverter;
         return this;
      }
   }

   public NonOptionArgumentSpec describedAs(String description) {
      this.argumentDescription = description;
      return this;
   }

   protected final Object convert(String argument) {
      return this.convertWith(this.converter, argument);
   }

   void handleOption(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions, String detectedArgument) {
      detectedOptions.addWithArgument(this, detectedArgument);
   }

   public List defaultValues() {
      return Collections.emptyList();
   }

   public boolean isRequired() {
      return false;
   }

   public boolean acceptsArguments() {
      return false;
   }

   public boolean requiresArgument() {
      return false;
   }

   public String argumentDescription() {
      return this.argumentDescription;
   }

   public String argumentTypeIndicator() {
      return this.argumentTypeIndicatorFrom(this.converter);
   }

   public boolean representsNonOptions() {
      return true;
   }
}
