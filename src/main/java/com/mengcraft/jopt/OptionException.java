package com.mengcraft.jopt;

import com.mengcraft.jopt.OptionSpec;
import com.mengcraft.jopt.UnrecognizedOptionException;
import com.mengcraft.jopt.internal.Strings;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class OptionException extends RuntimeException {
   private static final long serialVersionUID = -1L;
   private final List options = new ArrayList();

   protected OptionException(List options) {
      this.options.addAll(options);
   }

   protected OptionException(Collection options) {
      this.options.addAll(this.specsToStrings(options));
   }

   protected OptionException(Collection options, Throwable cause) {
      super(cause);
      this.options.addAll(this.specsToStrings(options));
   }

   private List specsToStrings(Collection options) {
      ArrayList strings = new ArrayList();
      Iterator var4 = options.iterator();

      while(var4.hasNext()) {
         OptionSpec each = (OptionSpec)var4.next();
         strings.add(this.specToString(each));
      }

      return strings;
   }

   private String specToString(OptionSpec option) {
      return Strings.join((List)(new ArrayList(option.options())), "/");
   }

   public List options() {
      return Collections.unmodifiableList(this.options);
   }

   protected final String singleOptionString() {
      return this.singleOptionString((String)this.options.get(0));
   }

   protected final String singleOptionString(String option) {
      return option;
   }

   protected final String multipleOptionString() {
      StringBuilder buffer = new StringBuilder("[");
      Iterator iter = this.options.iterator();

      while(iter.hasNext()) {
         buffer.append(this.singleOptionString((String)iter.next()));
         if(iter.hasNext()) {
            buffer.append(", ");
         }
      }

      buffer.append(']');
      return buffer.toString();
   }

   static OptionException unrecognizedOption(String option) {
      return new UnrecognizedOptionException(option);
   }

   public final String getMessage() {
      return this.localizedMessage(Locale.getDefault());
   }

   final String localizedMessage(Locale locale) {
      return this.formattedMessage(locale);
   }

   private String formattedMessage(Locale locale) {
      ResourceBundle bundle = ResourceBundle.getBundle("joptsimple.ExceptionMessages", locale);
      String template = bundle.getString(this.getClass().getName() + ".message");
      MessageFormat format = new MessageFormat(template);
      format.setLocale(locale);
      return format.format(this.messageArguments());
   }

   abstract Object[] messageArguments();
}
