package com.mengcraft.jopt.util;

import com.mengcraft.jopt.ValueConversionException;
import com.mengcraft.jopt.ValueConverter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class DateConverter implements ValueConverter {
   private final DateFormat formatter;

   public DateConverter(DateFormat formatter) {
      if(formatter == null) {
         throw new NullPointerException("illegal null formatter");
      } else {
         this.formatter = formatter;
      }
   }

   public static DateConverter datePattern(String pattern) {
      SimpleDateFormat formatter = new SimpleDateFormat(pattern);
      formatter.setLenient(false);
      return new DateConverter(formatter);
   }

   public Date convert(String value) {
      ParsePosition position = new ParsePosition(0);
      Date date = this.formatter.parse(value, position);
      if(position.getIndex() != value.length()) {
         throw new ValueConversionException(this.message(value));
      } else {
         return date;
      }
   }

   public Class valueType() {
      return Date.class;
   }

   public String valuePattern() {
      return this.formatter instanceof SimpleDateFormat?((SimpleDateFormat)this.formatter).toPattern():"";
   }

   private String message(String value) {
      ResourceBundle bundle = ResourceBundle.getBundle("joptsimple.ExceptionMessages");
      String key;
      Object[] arguments;
      if(this.formatter instanceof SimpleDateFormat) {
         key = this.getClass().getName() + ".with.pattern.message";
         arguments = new Object[]{value, ((SimpleDateFormat)this.formatter).toPattern()};
      } else {
         key = this.getClass().getName() + ".without.pattern.message";
         arguments = new Object[]{value};
      }

      String template = bundle.getString(key);
      return (new MessageFormat(template)).format(arguments);
   }
}
