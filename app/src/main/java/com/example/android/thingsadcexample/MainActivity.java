package com.example.android.thingsadcexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import java.util.Locale;

import nz.geek.android.things.drivers.adc.I2cAdc;
import nz.geek.android.things.drivers.lcd.I2cSerialCharLcd;

public class MainActivity extends Activity {

  private static final int LCD_WIDTH = 20;
  private static final int LCD_HEIGHT = 4;

  Handler handler = new Handler();
  Runnable runnable = new UpdateLcdRunner();

  I2cSerialCharLcd lcd;
  I2cAdc adc;
  BarGraph barGraph1 = new BarGraph(3);
  BarGraph barGraph2 = new BarGraph(4);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    createLcd();
    initBarGraph();
    createAdc();
    handler.post(runnable);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(runnable);
    if (lcd != null) {
      lcd.disconnect();
    }
    if (adc != null) {
      adc.stopConversions();
      adc.close();
    }
  }

  private void createLcd() {
    I2cSerialCharLcd.I2cSerialCharLcdBuilder builder = I2cSerialCharLcd.builder(LCD_WIDTH, LCD_HEIGHT);
    builder.rs(0).rw(1).e(2).bl(3).data(4, 5, 6, 7).address(7);
    lcd = builder.build();
    lcd.connect();
    lcd.enableBackLight(true);
  }

  private void createAdc() {
    I2cAdc.I2cAdcBuilder builder = I2cAdc.builder();
    adc = builder.address(0).fourSingleEnded().withConversionRate(100).build();
    adc.startConversions();
  }

  /**
   * load bar graph characters into LCD CGRAM
   */
  private void initBarGraph() {
    lcd.setCgRam(0x00, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
    lcd.setCgRam(0x08, new byte[]{0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x00});
    lcd.setCgRam(0x10, new byte[]{0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x00});
    lcd.setCgRam(0x18, new byte[]{0x1C, 0x1C, 0x1C, 0x1C, 0x1C, 0x1C, 0x1C, 0x00});
    lcd.setCgRam(0x20, new byte[]{0x1E, 0x1E, 0x1E, 0x1E, 0x1E, 0x1E, 0x1E, 0x00});
    lcd.setCgRam(0x28, new byte[]{0x1F, 0x1F, 0x1F, 0x1F, 0x1F, 0x1F, 0x1F, 0x00});
  }

  /**
   * runner to periodically read the adc and update the LCD display
   */
  private class UpdateLcdRunner implements Runnable {

    /**
     * convert the given ADC value [0:255] to a bargraph value [0:(LCD_WIDTH*5)]
     * @param value ADC value
     * @return bargraph value
     */
    private int valueToBargraph(int value) {
      return (int) ((((float) LCD_WIDTH * 5f) / 255f) * (float) value);
    }

    @Override
    public void run() {
      int value = adc.readChannel(0);
      int bargraphValue = valueToBargraph(value);
      lcd.print(1, String.format(Locale.UK, "        AIN0: %-5d", value));
      barGraph1.setValue(lcd, bargraphValue);

      value = adc.readChannel(1);
      bargraphValue = valueToBargraph(value);

      lcd.print(2, String.format(Locale.UK, "        AIN1: %-5d", value));
      barGraph2.setValue(lcd, bargraphValue);

      handler.postDelayed(this, 200);
    }
  }
}
