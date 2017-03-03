package com.mengcraft.jopt.internal;

import com.mengcraft.jopt.ValueConverter;
import com.mengcraft.jopt.internal.Reflection;
import java.lang.reflect.Constructor;

class ConstructorInvokingValueConverter implements ValueConverter {
   private final Constructor ctor;

   ConstructorInvokingValueConverter(Constructor ctor) {
      this.ctor = ctor;
   }

   public Object convert(String value) {
      return Reflection.instantiate(this.ctor, new Object[]{value});
   }

   public Class valueType() {
      return this.ctor.getDeclaringClass();
   }

   public String valuePattern() {
      return null;
   }
}
