package com.mengcraft.jopt;

import com.mengcraft.jopt.AbstractOptionSpec;
import com.mengcraft.jopt.AlternativeLongOptionSpec;
import com.mengcraft.jopt.ArgumentList;
import com.mengcraft.jopt.BuiltinHelpFormatter;
import com.mengcraft.jopt.HelpFormatter;
import com.mengcraft.jopt.MissingRequiredOptionsException;
import com.mengcraft.jopt.NonOptionArgumentSpec;
import com.mengcraft.jopt.OptionDeclarer;
import com.mengcraft.jopt.OptionException;
import com.mengcraft.jopt.OptionParserState;
import com.mengcraft.jopt.OptionSet;
import com.mengcraft.jopt.OptionSpec;
import com.mengcraft.jopt.OptionSpecBuilder;
import com.mengcraft.jopt.OptionSpecTokenizer;
import com.mengcraft.jopt.ParserRules;
import com.mengcraft.jopt.UnconfiguredOptionException;
import com.mengcraft.jopt.internal.AbbreviationMap;
import com.mengcraft.jopt.util.KeyValuePair;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class OptionParser implements OptionDeclarer {
   private final AbbreviationMap recognizedOptions;
   private final List trainingOrder;
   private final Map requiredIf;
   private final Map requiredUnless;
   private OptionParserState state;
   private boolean posixlyCorrect;
   private boolean allowsUnrecognizedOptions;
   private HelpFormatter helpFormatter;

   public OptionParser() {
      this.helpFormatter = new BuiltinHelpFormatter();
      this.recognizedOptions = new AbbreviationMap();
      this.trainingOrder = new ArrayList();
      this.requiredIf = new HashMap();
      this.requiredUnless = new HashMap();
      this.state = OptionParserState.moreOptions(false);
      this.recognize(new NonOptionArgumentSpec());
   }

   public OptionParser(String optionSpecification) {
      this();
      (new OptionSpecTokenizer(optionSpecification)).configure(this);
   }

   public OptionSpecBuilder accepts(String option) {
      return this.acceptsAll(Collections.singletonList(option));
   }

   public OptionSpecBuilder accepts(String option, String description) {
      return this.acceptsAll(Collections.singletonList(option), description);
   }

   public OptionSpecBuilder acceptsAll(List options) {
      return this.acceptsAll(options, "");
   }

   public OptionSpecBuilder acceptsAll(List options, String description) {
      if(options.isEmpty()) {
         throw new IllegalArgumentException("need at least one option");
      } else {
         ParserRules.ensureLegalOptions(options);
         return new OptionSpecBuilder(this, options, description);
      }
   }

   public NonOptionArgumentSpec nonOptions() {
      NonOptionArgumentSpec spec = new NonOptionArgumentSpec();
      this.recognize(spec);
      return spec;
   }

   public NonOptionArgumentSpec nonOptions(String description) {
      NonOptionArgumentSpec spec = new NonOptionArgumentSpec(description);
      this.recognize(spec);
      return spec;
   }

   public void posixlyCorrect(boolean setting) {
      this.posixlyCorrect = setting;
      this.state = OptionParserState.moreOptions(setting);
   }

   boolean posixlyCorrect() {
      return this.posixlyCorrect;
   }

   public void allowsUnrecognizedOptions() {
      this.allowsUnrecognizedOptions = true;
   }

   boolean doesAllowsUnrecognizedOptions() {
      return this.allowsUnrecognizedOptions;
   }

   public void recognizeAlternativeLongOptions(boolean recognize) {
      if(recognize) {
         this.recognize(new AlternativeLongOptionSpec());
      } else {
         this.recognizedOptions.remove(String.valueOf("W"));
      }

   }

   void recognize(AbstractOptionSpec spec) {
      this.recognizedOptions.putAll(spec.options(), spec);
      this.trainingOrder.add(spec);
   }

   public void printHelpOn(OutputStream sink) throws IOException {
      this.printHelpOn((Writer)(new OutputStreamWriter(sink)));
   }

   public void printHelpOn(Writer sink) throws IOException {
      sink.write(this.helpFormatter.format(this.recognizedOptions.toJavaUtilMap()));
      sink.flush();
   }

   public void formatHelpWith(HelpFormatter formatter) {
      if(formatter == null) {
         throw new NullPointerException();
      } else {
         this.helpFormatter = formatter;
      }
   }

   public Map recognizedOptions() {
      LinkedHashMap options = new LinkedHashMap();
      Iterator var3 = this.trainingOrder.iterator();

      while(var3.hasNext()) {
         OptionSpec spec = (OptionSpec)var3.next();
         Iterator var5 = spec.options().iterator();

         while(var5.hasNext()) {
            String option = (String)var5.next();
            options.put(option, spec);
         }
      }

      return options;
   }

   public OptionSet parse(String... arguments) {
      ArgumentList argumentList = new ArgumentList(arguments);
      OptionSet detected = new OptionSet(this.recognizedOptions.toJavaUtilMap());
      detected.add((AbstractOptionSpec)this.recognizedOptions.get("[arguments]"));

      while(argumentList.hasMore()) {
         this.state.handleArgument(this, argumentList, detected);
      }

      this.reset();
      this.ensureRequiredOptions(detected);
      return detected;
   }

   private void ensureRequiredOptions(OptionSet options) {
      List missingRequiredOptions = this.missingRequiredOptions(options);
      boolean helpOptionPresent = this.isHelpOptionPresent(options);
      if(!missingRequiredOptions.isEmpty() && !helpOptionPresent) {
         throw new MissingRequiredOptionsException(missingRequiredOptions);
      }
   }

   private List missingRequiredOptions(OptionSet options) {
      ArrayList missingRequiredOptions = new ArrayList();
      Iterator var4 = this.recognizedOptions.toJavaUtilMap().values().iterator();

      while(var4.hasNext()) {
         AbstractOptionSpec eachEntry = (AbstractOptionSpec)var4.next();
         if(eachEntry.isRequired() && !options.has((OptionSpec)eachEntry)) {
            missingRequiredOptions.add(eachEntry);
         }
      }

      var4 = this.requiredIf.entrySet().iterator();

      AbstractOptionSpec required;
      Entry eachEntry1;
      while(var4.hasNext()) {
         eachEntry1 = (Entry)var4.next();
         required = this.specFor((String)((List)eachEntry1.getKey()).iterator().next());
         if(this.optionsHasAnyOf(options, (Collection)eachEntry1.getValue()) && !options.has((OptionSpec)required)) {
            missingRequiredOptions.add(required);
         }
      }

      var4 = this.requiredUnless.entrySet().iterator();

      while(var4.hasNext()) {
         eachEntry1 = (Entry)var4.next();
         required = this.specFor((String)((List)eachEntry1.getKey()).iterator().next());
         if(!this.optionsHasAnyOf(options, (Collection)eachEntry1.getValue()) && !options.has((OptionSpec)required)) {
            missingRequiredOptions.add(required);
         }
      }

      return missingRequiredOptions;
   }

   private boolean optionsHasAnyOf(OptionSet options, Collection specs) {
      Iterator var4 = specs.iterator();

      while(var4.hasNext()) {
         OptionSpec each = (OptionSpec)var4.next();
         if(options.has(each)) {
            return true;
         }
      }

      return false;
   }

   private boolean isHelpOptionPresent(OptionSet options) {
      boolean helpOptionPresent = false;
      Iterator var4 = this.recognizedOptions.toJavaUtilMap().values().iterator();

      while(var4.hasNext()) {
         AbstractOptionSpec each = (AbstractOptionSpec)var4.next();
         if(each.isForHelp() && options.has((OptionSpec)each)) {
            helpOptionPresent = true;
            break;
         }
      }

      return helpOptionPresent;
   }

   void handleLongOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
      KeyValuePair optionAndArgument = parseLongOptionWithArgument(candidate);
      if(!this.isRecognized(optionAndArgument.key)) {
         throw OptionException.unrecognizedOption(optionAndArgument.key);
      } else {
         AbstractOptionSpec optionSpec = this.specFor(optionAndArgument.key);
         optionSpec.handleOption(this, arguments, detected, optionAndArgument.value);
      }
   }

   void handleShortOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
      KeyValuePair optionAndArgument = parseShortOptionWithArgument(candidate);
      if(this.isRecognized(optionAndArgument.key)) {
         this.specFor(optionAndArgument.key).handleOption(this, arguments, detected, optionAndArgument.value);
      } else {
         this.handleShortOptionCluster(candidate, arguments, detected);
      }

   }

   private void handleShortOptionCluster(String candidate, ArgumentList arguments, OptionSet detected) {
      char[] options = extractShortOptionsFrom(candidate);
      this.validateOptionCharacters(options);

      for(int i = 0; i < options.length; ++i) {
         AbstractOptionSpec optionSpec = this.specFor(options[i]);
         if(optionSpec.acceptsArguments() && options.length > i + 1) {
            String detectedArgument = String.valueOf(options, i + 1, options.length - 1 - i);
            optionSpec.handleOption(this, arguments, detected, detectedArgument);
            break;
         }

         optionSpec.handleOption(this, arguments, detected, (String)null);
      }

   }

   void handleNonOptionArgument(String candidate, ArgumentList arguments, OptionSet detectedOptions) {
      this.specFor("[arguments]").handleOption(this, arguments, detectedOptions, candidate);
   }

   void noMoreOptions() {
      this.state = OptionParserState.noMoreOptions();
   }

   boolean looksLikeAnOption(String argument) {
      return ParserRules.isShortOptionToken(argument) || ParserRules.isLongOptionToken(argument);
   }

   boolean isRecognized(String option) {
      return this.recognizedOptions.contains(option);
   }

   void requiredIf(List precedentSynonyms, String required) {
      this.requiredIf(precedentSynonyms, (OptionSpec)this.specFor(required));
   }

   void requiredIf(List precedentSynonyms, OptionSpec required) {
      this.putRequiredOption(precedentSynonyms, required, this.requiredIf);
   }

   void requiredUnless(List precedentSynonyms, String required) {
      this.requiredUnless(precedentSynonyms, (OptionSpec)this.specFor(required));
   }

   void requiredUnless(List precedentSynonyms, OptionSpec required) {
      this.putRequiredOption(precedentSynonyms, required, this.requiredUnless);
   }

   private void putRequiredOption(List precedentSynonyms, OptionSpec required, Map target) {
      Iterator var5 = precedentSynonyms.iterator();

      while(var5.hasNext()) {
         String associated = (String)var5.next();
         AbstractOptionSpec spec = this.specFor(associated);
         if(spec == null) {
            throw new UnconfiguredOptionException(precedentSynonyms);
         }
      }

      Object associated1 = (Set)target.get(precedentSynonyms);
      if(associated1 == null) {
         associated1 = new HashSet();
         target.put(precedentSynonyms, associated1);
      }

      ((Set)associated1).add(required);
   }

   private AbstractOptionSpec specFor(char option) {
      return this.specFor(String.valueOf(option));
   }

   private AbstractOptionSpec specFor(String option) {
      return (AbstractOptionSpec)this.recognizedOptions.get(option);
   }

   private void reset() {
      this.state = OptionParserState.moreOptions(this.posixlyCorrect);
   }

   private static char[] extractShortOptionsFrom(String argument) {
      char[] options = new char[argument.length() - 1];
      argument.getChars(1, argument.length(), options, 0);
      return options;
   }

   private void validateOptionCharacters(char[] options) {
      char[] var5 = options;
      int var4 = options.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         char each = var5[var3];
         String option = String.valueOf(each);
         if(!this.isRecognized(option)) {
            throw OptionException.unrecognizedOption(option);
         }

         if(this.specFor(option).acceptsArguments()) {
            return;
         }
      }

   }

   private static KeyValuePair parseLongOptionWithArgument(String argument) {
      return KeyValuePair.valueOf(argument.substring(2));
   }

   private static KeyValuePair parseShortOptionWithArgument(String argument) {
      return KeyValuePair.valueOf(argument.substring(1));
   }
}
