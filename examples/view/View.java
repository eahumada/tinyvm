
import tinyvm.rcx.*;

/**
 * This program can be used to test each sensor and motor
 * independently.<br>
 * Button functionality:<br>
 * <ul>
 * <li> VIEW:<br>
 *   Select a device.
 * <li> PRGM:<br>
 *   Sensors: switch type of input (0 = raw, 1 = pct, 2 = boolean).<br>
 *   Motors : select power.
 * <li> RUN:<br>
 *   Sensors: activate (1) or passivate (0).<br>
 *   Motors : forward (1), backward (2), or stop (0).
 * </ul>
 */
public class View implements Segment
{
  static Button[] iButtons = new Button[] { Button.VIEW, Button.PRGM, Button.RUN };
  static Object[] iDevices = new Object[] { Sensor.S1, Sensor.S2, Sensor.S3,
				     Motor.A, Motor.B, Motor.C };
  static int iViewSegment[][] = 
  { 
     { SENSOR_1_VIEW, SENSOR_2_VIEW, SENSOR_3_VIEW,
       MOTOR_A_VIEW, MOTOR_B_VIEW, MOTOR_C_VIEW },
     { SENSOR_1_ACTIVE, SENSOR_2_ACTIVE, SENSOR_3_ACTIVE,
       MOTOR_A_FWD, MOTOR_B_FWD, MOTOR_C_FWD },
     { SENSOR_1_ACTIVE, SENSOR_2_ACTIVE, SENSOR_3_ACTIVE,
       MOTOR_A_REV, MOTOR_B_REV, MOTOR_C_REV }
  };
                         
  static int iRunState[] = new int[6];
  static int iRunMode[] = new int[6];
  static int iCurrentDevice;
  
  static void runPressed()
  {
    Object pDevice = iDevices[iCurrentDevice];
    int pTop = (pDevice instanceof Sensor) ? 2 : 3;
    iRunState[iCurrentDevice]++;
    if (iRunState[iCurrentDevice] >= pTop)
      iRunState[iCurrentDevice] = 0;
  }

  static void viewPressed()
  {
    iCurrentDevice++;
    if (iCurrentDevice >= iDevices.length)
      iCurrentDevice = 0;
  }

  static void prgmPressed()
  {
    Object pDevice = iDevices[iCurrentDevice];
    int pTop = (pDevice instanceof Sensor) ? 3 : 8;
    iRunMode[iCurrentDevice]++;
    if (iRunMode[iCurrentDevice] >= pTop)
      iRunMode[iCurrentDevice] = 0;
  }

  static void updateRCX()
  {
    Object pDevice = iDevices[iCurrentDevice];
    int pRunState = iRunState[iCurrentDevice];
    int pRunMode = iRunMode[iCurrentDevice];
    LCD.clear();
    LCD.setSegment (iViewSegment[pRunState][iCurrentDevice]);
    updateValue();
    if (pDevice instanceof Sensor)
    {
      Sensor s = (Sensor) pDevice;
      if (pRunState == 0)
        s.passivate();
      else if (pRunState == 1)
        s.activate();
      LCD.showProgramNumber (pRunMode);
    }
    else
    { 
      Motor m = (Motor) pDevice;
      m.setPower (pRunMode);
      if (pRunState == 0)
        m.stop();
      else if (pRunState == 1)
        m.forward();
      else if (pRunState == 2)
        m.backward();
      LCD.showNumber (pRunMode);
      LCD.showProgramNumber (pRunState);
    }
  }

  static void updateValue()
  {
    Object pDevice = iDevices[iCurrentDevice];
    int pRunMode = iRunMode[iCurrentDevice];
    if (pDevice instanceof Sensor)
    {
      int value = 0;
      Sensor s = (Sensor) pDevice;
      if (pRunMode == 0)
        value = s.readRawValue();
      else if (pRunMode == 1)
        value = s.readPercentage();
      else if (pRunMode == 2)
        value = s.readBooleanValue() ? 1 : 0;
      LCD.showNumber (value);
    }
  }

  public static void main (String[] arg)
  {
    updateRCX();
    for (;;)
    {
      for (int i = 0; i < 3; i++)
      {
        Button b = iButtons[i];
        if (b.isPressed())
	{
          while (b.isPressed()) { }
          if (b == Button.VIEW)
            viewPressed();
          else if (b == Button.PRGM)
            prgmPressed();
          else if (b == Button.RUN)
            runPressed();
          updateRCX();
	}
      }
      updateValue();
    }
  }
}
