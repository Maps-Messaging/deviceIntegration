package io.mapsmessaging.server.i2c.devices.sensors;

import com.pi4j.io.i2c.I2C;
import io.mapsmessaging.server.i2c.I2CDevice;
import java.io.IOException;

public class BMP280Sensor extends I2CDevice {

  public static final byte CONVERT_D1_OSR_256 = (byte) 0x40;
  public static final byte CONVERT_D1_OSR_512 = (byte) 0x42;
  public static final byte CONVERT_D1_OSR_1024 = (byte) 0x44;
  public static final byte CONVERT_D1_OSR_2048 = (byte) 0x46;
  public static final byte CONVERT_D1_OSR_4096 = (byte) 0x48;
  public static final byte CONVERT_D2_OSR_256 = (byte) 0x50;
  public static final byte CONVERT_D2_OSR_512 = (byte) 0x52;
  public static final byte CONVERT_D2_OSR_1024 = (byte) 0x54;
  public static final byte CONVERT_D2_OSR_2048 = (byte) 0x56;
  public static final byte CONVERT_D2_OSR_4096 = (byte) 0x58;

  public static final byte PROM_READ_SEQUENCE = (byte) 0xA0;

  public static final byte ADC_READ = (byte) 0x00;
  private static final short sReset = 0x1E;
  private final int[] prom;
  private long C1; // C1
  private long C2; // C2
  private long C3; // C3
  private long C4; // C4
  private long C5; // C5
  private long C6; // C6
  private int CRC;
  private long SENS_T1;
  private long OFF_T1;
  private long TCS;
  private long TCO;
  private int D1; // 24 bit unsigned int
  private int D2; // 24 bit unsigned int
  private float temperature;
  private float pressure;


  public BMP280Sensor(I2C device) throws IOException {
    super(device);
    prom = new int[8];
    initialise();
    scanForChange();
  }

  private  byte crc4(int[] prom) {
    int cnt; // simple counter
    int n_rem; // crc reminder
    int crc_read; // original value of the crc
    byte n_bit;
    n_rem = 0x00;
    crc_read = prom[7]; // save read CRC
    prom[7] &= 0xFF00; // CRC byte is replaced by 0
    for (cnt = 0; cnt < 16; cnt++) { // operation is performed on bytes
// choose LSB or MSB
      if (cnt % 2 == 1) {
        n_rem ^= prom[cnt >> 1] & 0x00FF;
      } else {
        n_rem ^= prom[cnt >> 1] >> 8;
      }
      for (n_bit = 8; n_bit > 0; n_bit--) {
        if ((n_rem & 0x8000) == 0x8000) {
          n_rem = (n_rem << 1) ^ 0x3000;
        } else {
          n_rem <<= 1;
        }
      }
    }
    n_rem = 0x000F & (n_rem >> 12); // final 4-bit reminder is CRC code
    prom[7] = crc_read; // restore the crc_read to its original place
    return (byte) (n_rem ^ 0x00);
  }


  public String getRegisters() {
    StringBuilder sb = new StringBuilder("\n");
    for (int x = 0; x < prom.length; x++) {
      sb.append("Prom[" + x + "] = " + prom[x] + "\n");
    }
    return sb.toString();
  }

  protected void read(byte command, int length, byte[] values) throws IOException {
    readRegister(command,  values, 0, length);
  }

  private void conversion() {
    try {
      byte[] readBuffer = new byte[3];
      write(CONVERT_D2_OSR_4096);
      delay(10);
      read(ADC_READ, 3, readBuffer);
      D2 = ((readBuffer[0] & 0xFF) << 16) | ((readBuffer[1] & 0xFF) << 8) | ((readBuffer[2] & 0xFF));
      write(CONVERT_D1_OSR_4096);
      delay(10);
      read(ADC_READ, 3, readBuffer);
      D1 = ((readBuffer[0] & 0xFF) << 16) | ((readBuffer[1] & 0xFF) << 8) | ((readBuffer[2] & 0xFF));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public float getTemperature() {
    return temperature;
  }

  public float getPressure() {
    return pressure;
  }

  public void initialise() throws IOException {
    write((byte) sReset);
    delay(1000);
    byte[] readBuffer = new byte[2];
    for (int i = 0; i < 8; i++) {
      read((byte) (PROM_READ_SEQUENCE + i * 2), 2, readBuffer);
      prom[i] = ((readBuffer[0] & 0xFF) << 8) | ((readBuffer[1] & 0xFF));
    }
    C1 = prom[1];
    C2 = prom[2];
    C3 = prom[3];
    C4 = prom[4];
    C5 = prom[5];
    C6 = prom[6];
    CRC = prom[7] & 0x0F;
    byte crc4 = crc4(prom);
    if (crc4 != CRC) {
      System.err.println(this.getClass().getSimpleName() + " PROM CRC mismatch, " + CRC + " (read) != " + crc4 + " (calculated).");
    }

    SENS_T1 = C1 * (1 << 15) /* 2^15 */;
    OFF_T1 = C2 * (1 << 16) /* 2^16 */;
    TCS = C3 / (1 << 8)  /* 2^8 */;
    TCO = C4 / (1 << 7)  /* 2^7 */;
  }

  public void scanForChange() {
    conversion();
    long dT = D2 - (C5 << 8);
    long t = dT * C6;
    float temp = (t >> 23);
    temp += 2000;
    temp = temp / 100.0f;

    long OFF = OFF_T1 + dT * TCO;
    long SENS = SENS_T1 + dT * TCS;
    float p = (((float) (D1 * SENS / 2097152 /* 2^21 */ - OFF) / 0x8000 /* 2^15 */) / 100.0f);

    if (p != pressure || temp != temperature) {
      pressure = p;
      temperature = temp;
    }
  }
}