package com.example.android.thingsadcexample;

import android.util.IntProperty;

import nz.geek.android.things.drivers.lcd.I2cSerialCharLcd;

public class BarGraph extends IntProperty<I2cSerialCharLcd> {

  private static final char BAR_0 = 0x00;
  private static final char BAR_1 = 0x01;
  private static final char BAR_2 = 0x02;
  private static final char BAR_3 = 0x03;
  private static final char BAR_4 = 0x04;
  private static final char BAR_5 = 0x05;

  private int value;
  private final int line;

  public BarGraph(int line) {
    super("LCD Bargraph");
    this.line = line;
  }

  private String createBarGraphString(int width, int value) {
    StringBuilder sb = new StringBuilder(width);
    int numFullCharacters = value / 5;
    int remainder = value % 5;
    int numEmptyCharacters = width - (numFullCharacters + (remainder == 0 ? 0 : 1));

    for (int i = 0; i < numFullCharacters; i++) {
      sb.append(BAR_5);
    }

    switch(remainder) {
      case 4:
        sb.append(BAR_4);
        break;
      case 3:
        sb.append(BAR_3);
        break;
      case 2:
        sb.append(BAR_2);
        break;
      case 1:
        sb.append(BAR_1);
        break;
    }

    for (int i = 0; i < numEmptyCharacters; i++) {
      sb.append(BAR_0);
    }

    return sb.toString();
  }

  private void updateBarGraph(I2cSerialCharLcd lcd, int value) {
    this.value = value;
    lcd.print(line, createBarGraphString(lcd.getWidth(), value));
  }

  @Override
  public void setValue(I2cSerialCharLcd i2cSerialCharLcd, int i) {
    updateBarGraph(i2cSerialCharLcd, i);
  }

  @Override
  public Integer get(I2cSerialCharLcd i2cSerialCharLcd) {
    return value;
  }
}
