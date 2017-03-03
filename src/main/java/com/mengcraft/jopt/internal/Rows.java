package com.mengcraft.jopt.internal;

import com.mengcraft.jopt.internal.Columns;
import com.mengcraft.jopt.internal.Row;
import com.mengcraft.jopt.internal.Strings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rows {
   private final int overallWidth;
   private final int columnSeparatorWidth;
   private final List rows = new ArrayList();
   private int widthOfWidestOption;
   private int widthOfWidestDescription;

   public Rows(int overallWidth, int columnSeparatorWidth) {
      this.overallWidth = overallWidth;
      this.columnSeparatorWidth = columnSeparatorWidth;
   }

   public void add(String option, String description) {
      this.add(new Row(option, description));
   }

   private void add(Row row) {
      this.rows.add(row);
      this.widthOfWidestOption = Math.max(this.widthOfWidestOption, row.option.length());
      this.widthOfWidestDescription = Math.max(this.widthOfWidestDescription, row.description.length());
   }

   private void reset() {
      this.rows.clear();
      this.widthOfWidestOption = 0;
      this.widthOfWidestDescription = 0;
   }

   public void fitToWidth() {
      Columns columns = new Columns(this.optionWidth(), this.descriptionWidth());
      ArrayList fitted = new ArrayList();
      Iterator var4 = this.rows.iterator();

      Row each;
      while(var4.hasNext()) {
         each = (Row)var4.next();
         fitted.addAll(columns.fit(each));
      }

      this.reset();
      var4 = fitted.iterator();

      while(var4.hasNext()) {
         each = (Row)var4.next();
         this.add(each);
      }

   }

   public String render() {
      StringBuilder buffer = new StringBuilder();
      Iterator var3 = this.rows.iterator();

      while(var3.hasNext()) {
         Row each = (Row)var3.next();
         this.pad(buffer, each.option, this.optionWidth()).append(Strings.repeat(' ', this.columnSeparatorWidth));
         this.pad(buffer, each.description, this.descriptionWidth()).append(Strings.LINE_SEPARATOR);
      }

      return buffer.toString();
   }

   private int optionWidth() {
      return Math.min((this.overallWidth - this.columnSeparatorWidth) / 2, this.widthOfWidestOption);
   }

   private int descriptionWidth() {
      return Math.min((this.overallWidth - this.columnSeparatorWidth) / 2, this.widthOfWidestDescription);
   }

   private StringBuilder pad(StringBuilder buffer, String s, int length) {
      buffer.append(s).append(Strings.repeat(' ', length - s.length()));
      return buffer;
   }
}
