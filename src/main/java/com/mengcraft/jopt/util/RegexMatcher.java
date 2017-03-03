package com.mengcraft.jopt.util;

import com.mengcraft.jopt.ValueConversionException;
import com.mengcraft.jopt.ValueConverter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegexMatcher implements ValueConverter {
   private final Pattern pattern;

   public RegexMatcher(String pattern, int flags) {
      this.pattern = Pattern.compile(pattern, flags);
   }

   public static ValueConverter regex(String pattern) {
      return new RegexMatcher(pattern, 0);
   }

   public String convert(String value) {
      if(!this.pattern.matcher(value).matches()) {
         this.raiseValueConversionFailure(value);
      }

      return value;
   }

   public Class valueType() {
      return String.class;
   }

   public String valuePattern() {
      return this.pattern.pattern();
   }

   private void raiseValueConversionFailure(String value) {
      ResourceBundle bundle = ResourceBundle.getBundle("joptsimple.ExceptionMessages");
      String template = bundle.getString(this.getClass().getName() + ".message");
      String message = (new MessageFormat(template)).format(new Object[]{value, this.pattern.pattern()});
      throw new ValueConversionException(message);
   }
}
