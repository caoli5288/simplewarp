package com.mengcraft.jopt;

import com.mengcraft.jopt.OptionSet;
import java.util.List;

public interface OptionSpec {
   List values(OptionSet var1);

   Object value(OptionSet var1);

   List options();

   boolean isForHelp();
}
