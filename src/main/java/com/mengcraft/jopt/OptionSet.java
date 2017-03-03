package com.mengcraft.jopt;

import com.mengcraft.jopt.AbstractOptionSpec;
import com.mengcraft.jopt.MultipleArgumentsForOptionException;
import com.mengcraft.jopt.OptionSpec;
import com.mengcraft.jopt.internal.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class OptionSet {
   private final List detectedSpecs = new ArrayList();
   private final Map detectedOptions = new HashMap();
   private final Map optionsToArguments = new IdentityHashMap();
   private final Map recognizedSpecs;
   private final Map defaultValues;

   OptionSet(Map recognizedSpecs) {
      this.defaultValues = defaultValues(recognizedSpecs);
      this.recognizedSpecs = recognizedSpecs;
   }

   public boolean hasOptions() {
      return this.detectedOptions.size() != 1 || !((AbstractOptionSpec)this.detectedOptions.values().iterator().next()).representsNonOptions();
   }

   public boolean has(String option) {
      return this.detectedOptions.containsKey(option);
   }

   public boolean has(OptionSpec option) {
      return this.optionsToArguments.containsKey(option);
   }

   public boolean hasArgument(String option) {
      AbstractOptionSpec spec = (AbstractOptionSpec)this.detectedOptions.get(option);
      return spec != null && this.hasArgument((OptionSpec)spec);
   }

   public boolean hasArgument(OptionSpec option) {
      Objects.ensureNotNull(option);
      List values = (List)this.optionsToArguments.get(option);
      return values != null && !values.isEmpty();
   }

   public Object valueOf(String option) {
      Objects.ensureNotNull(option);
      AbstractOptionSpec spec = (AbstractOptionSpec)this.detectedOptions.get(option);
      if(spec == null) {
         List defaults = this.defaultValuesFor(option);
         return defaults.isEmpty()?null:defaults.get(0);
      } else {
         return this.valueOf((OptionSpec)spec);
      }
   }

   public Object valueOf(OptionSpec option) {
      Objects.ensureNotNull(option);
      List values = this.valuesOf(option);
      switch(values.size()) {
      case 0:
         return null;
      case 1:
         return values.get(0);
      default:
         throw new MultipleArgumentsForOptionException(option);
      }
   }

   public List<Object> valuesOf(String option) {
      Objects.ensureNotNull(option);
      AbstractOptionSpec spec = (AbstractOptionSpec)this.detectedOptions.get(option);
      return spec == null?this.defaultValuesFor(option):this.valuesOf(spec);
   }

   public List valuesOf(OptionSpec option) {
      Objects.ensureNotNull(option);
      List values = (List)this.optionsToArguments.get(option);
      if(values != null && !values.isEmpty()) {
         AbstractOptionSpec spec = (AbstractOptionSpec)option;
         ArrayList convertedValues = new ArrayList();
         Iterator var6 = values.iterator();

         while(var6.hasNext()) {
            String each = (String)var6.next();
            convertedValues.add(spec.convert(each));
         }

         return Collections.unmodifiableList(convertedValues);
      } else {
         return this.defaultValueFor(option);
      }
   }

   public List specs() {
      List specs = this.detectedSpecs;
      specs.remove(this.detectedOptions.get("[arguments]"));
      return Collections.unmodifiableList(specs);
   }

   public Map asMap() {
      HashMap map = new HashMap();
      Iterator var3 = this.recognizedSpecs.values().iterator();

      while(var3.hasNext()) {
         AbstractOptionSpec spec = (AbstractOptionSpec)var3.next();
         if(!spec.representsNonOptions()) {
            map.put(spec, this.valuesOf((OptionSpec)spec));
         }
      }

      return Collections.unmodifiableMap(map);
   }

   public List nonOptionArguments() {
      return Collections.unmodifiableList(this.valuesOf((OptionSpec)this.detectedOptions.get("[arguments]")));
   }

   void add(AbstractOptionSpec spec) {
      this.addWithArgument(spec, (String)null);
   }

   void addWithArgument(AbstractOptionSpec spec, String argument) {
      this.detectedSpecs.add(spec);
      Iterator var4 = spec.options().iterator();

      while(var4.hasNext()) {
         String optionArguments = (String)var4.next();
         this.detectedOptions.put(optionArguments, spec);
      }

      Object optionArguments1 = (List)this.optionsToArguments.get(spec);
      if(optionArguments1 == null) {
         optionArguments1 = new ArrayList();
         this.optionsToArguments.put(spec, optionArguments1);
      }

      if(argument != null) {
         ((List)optionArguments1).add(argument);
      }

   }

   public boolean equals(Object that) {
      if(this == that) {
         return true;
      } else if(that != null && this.getClass().equals(that.getClass())) {
         OptionSet other = (OptionSet)that;
         HashMap thisOptionsToArguments = new HashMap(this.optionsToArguments);
         HashMap otherOptionsToArguments = new HashMap(other.optionsToArguments);
         return this.detectedOptions.equals(other.detectedOptions) && thisOptionsToArguments.equals(otherOptionsToArguments);
      } else {
         return false;
      }
   }

   public int hashCode() {
      HashMap thisOptionsToArguments = new HashMap(this.optionsToArguments);
      return this.detectedOptions.hashCode() ^ thisOptionsToArguments.hashCode();
   }

   private List defaultValuesFor(String option) {
      return this.defaultValues.containsKey(option)?(List)this.defaultValues.get(option):Collections.emptyList();
   }

   private List defaultValueFor(OptionSpec option) {
      return this.defaultValuesFor((String)option.options().iterator().next());
   }

   private static Map defaultValues(Map recognizedSpecs) {
      HashMap defaults = new HashMap();
      Iterator var3 = recognizedSpecs.entrySet().iterator();

      while(var3.hasNext()) {
         Entry each = (Entry)var3.next();
         defaults.put((String)each.getKey(), ((AbstractOptionSpec)each.getValue()).defaultValues());
      }

      return defaults;
   }
}
