package com.mengcraft.jopt.internal;

import com.mengcraft.jopt.ValueConverter;
import com.mengcraft.jopt.internal.Reflection;
import java.lang.reflect.Method;

class MethodInvokingValueConverter implements ValueConverter {
   private final Method method;
   private final Class clazz;

   MethodInvokingValueConverter(Method method, Class clazz) {
      this.method = method;
      this.clazz = clazz;
   }

   public Object convert(String value) {
      return this.clazz.cast(Reflection.invoke(this.method, new Object[]{value}));
   }

   public Class valueType() {
      return this.clazz;
   }

   public String valuePattern() {
      return null;
   }
}
