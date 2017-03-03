package com.mengcraft.jopt;

import com.mengcraft.jopt.NonOptionArgumentSpec;
import com.mengcraft.jopt.OptionSpecBuilder;
import java.util.List;

public interface OptionDeclarer {
   OptionSpecBuilder accepts(String var1);

   OptionSpecBuilder accepts(String var1, String var2);

   OptionSpecBuilder acceptsAll(List var1);

   OptionSpecBuilder acceptsAll(List var1, String var2);

   NonOptionArgumentSpec nonOptions();

   NonOptionArgumentSpec nonOptions(String var1);

   void posixlyCorrect(boolean var1);

   void allowsUnrecognizedOptions();

   void recognizeAlternativeLongOptions(boolean var1);
}
