package com.mengcraft.jopt;

import com.mengcraft.jopt.ArgumentAcceptingOptionSpec;
import com.mengcraft.jopt.NoArgumentOptionSpec;
import com.mengcraft.jopt.OptionParser;
import com.mengcraft.jopt.OptionSpec;
import com.mengcraft.jopt.OptionalArgumentOptionSpec;
import com.mengcraft.jopt.RequiredArgumentOptionSpec;
import com.mengcraft.jopt.UnconfiguredOptionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OptionSpecBuilder extends NoArgumentOptionSpec {
   private final OptionParser parser;

   OptionSpecBuilder(OptionParser parser, List options, String description) {
      super(options, description);
      this.parser = parser;
      this.attachToParser();
   }

   private void attachToParser() {
      this.parser.recognize(this);
   }

   public ArgumentAcceptingOptionSpec withRequiredArg() {
      RequiredArgumentOptionSpec newSpec = new RequiredArgumentOptionSpec(this.options(), this.description());
      this.parser.recognize(newSpec);
      return newSpec;
   }

   public ArgumentAcceptingOptionSpec withOptionalArg() {
      OptionalArgumentOptionSpec newSpec = new OptionalArgumentOptionSpec(this.options(), this.description());
      this.parser.recognize(newSpec);
      return newSpec;
   }

   public OptionSpecBuilder requiredIf(String dependent, String... otherDependents) {
      List dependents = this.validatedDependents(dependent, otherDependents);
      Iterator var5 = dependents.iterator();

      while(var5.hasNext()) {
         String each = (String)var5.next();
         this.parser.requiredIf(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredIf(OptionSpec dependent, OptionSpec... otherDependents) {
      this.parser.requiredIf(this.options(), dependent);
      OptionSpec[] var6 = otherDependents;
      int var5 = otherDependents.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         OptionSpec each = var6[var4];
         this.parser.requiredIf(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredUnless(String dependent, String... otherDependents) {
      List dependents = this.validatedDependents(dependent, otherDependents);
      Iterator var5 = dependents.iterator();

      while(var5.hasNext()) {
         String each = (String)var5.next();
         this.parser.requiredUnless(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredUnless(OptionSpec dependent, OptionSpec... otherDependents) {
      this.parser.requiredUnless(this.options(), dependent);
      OptionSpec[] var6 = otherDependents;
      int var5 = otherDependents.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         OptionSpec each = var6[var4];
         this.parser.requiredUnless(this.options(), each);
      }

      return this;
   }

   private List validatedDependents(String dependent, String... otherDependents) {
      ArrayList dependents = new ArrayList();
      dependents.add(dependent);
      Collections.addAll(dependents, otherDependents);
      Iterator var5 = dependents.iterator();

      while(var5.hasNext()) {
         String each = (String)var5.next();
         if(!this.parser.isRecognized(each)) {
            throw new UnconfiguredOptionException(each);
         }
      }

      return dependents;
   }
}
