/*
 * Copyright (c) 2010-2019, sikuli.org, sikulix.com - MIT license
 */

package org.sikuli.android;

import org.sikuli.basics.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Image;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Sikulix;
import org.sikuli.script.support.RunTime;

import javax.swing.*;
import java.io.File;

/**
 * Created by RaiMan on 12.07.16.
 * <p>
 * Test for the basic ADB based features
 */
public class ADBTest {

  private static int lvl = 3;

  private static void log(int level, String message, Object... args) {
    Debug.logx(level, "ADBTest: " + message, args);
  }

  private static void logp(String message, Object... args) {
    System.out.println(String.format(message, args));
  }

  private static boolean runTests = true;


  public static void main(String[] args) throws FindFailed {

    ADBScreen aScr = startTest();

    if (aScr.isValid()) {
      if (runTests) {
        //basicTest(aScr);
        ideTest(aScr);
      }
      ADBScreen.stop();
      System.exit(0);
    } else {
      System.exit(1);
    }

    // ********* playground
  }

  private static ADBScreen startTest() {
    Debug.on(3);
    ADBScreen adbs = new ADBScreen();
    if (adbs.isValid()) {
      log(lvl, "Device found: %s", adbs.getDeviceDescription());
      adbs.wakeUp(2);
      adbs.wait(1f);
      if (!adbs.getDevice().isDisplayOn()) {
        log(-1, "WakeUp did not work");
      } else {
        adbs.aKey(ADBDevice.KEY_HOME);
        adbs.wait(1f);
      }
    }
    return adbs;
  }

  private static void basicTest(ADBScreen adbs) throws FindFailed {
    log(lvl, "**************** running basic test");
    adbs.aSwipeLeft();
    adbs.aSwipeRight();
    adbs.wait(1f);
    ScreenImage sIMg = adbs.userCapture("Android");
    sIMg.getFile(RunTime.get().fSikulixStore.getAbsolutePath(), "android");
    if (Debug.getDebugLevel() > 2) {
      saveShot();
    }
    adbs.aTap(new Image(sIMg));
  }

  private static void saveShot() {
    if (Debug.getDebugLevel() < 3) {
      return;
    }
    File fShot = new File(RunTime.get().fSikulixStore.getAbsolutePath(), "lastScreenShot.png");
    File fShotAndroid = new File(RunTime.get().fSikulixStore.getAbsolutePath(), "lastScreenAndroid.png");
    if (fShotAndroid.exists()) {
      fShotAndroid.delete();
    }
    if (fShot.exists()) {
      fShot.renameTo(fShotAndroid);
    }
  }

  public static void ideTest(ADBScreen aScr) {
    String title = "Android Support - Testing device";
    Sikulix.popup("Take care\n\nthat device is on and unlocked\n\nbefore clicking ok", title);
    aScr.wakeUp(2);
    aScr.aKey(ADBDevice.KEY_HOME);
    boolean cancelled = true;
    if (Sikulix.popAsk("Now the device should show the HOME screen.\n" +
            "\nclick YES to proceed watching the test on the device" +
            "\nclick NO to end the test now", title)) {
      cancelled = false;
      aScr.aSwipeLeft();
      aScr.aSwipeRight();
      aScr.wait(1f);
      if (Sikulix.popAsk("You should have seen a swipe left and a swipe right.\n" +
              "\nclick YES to capture an icon from homescreen and then aTap it" +
              "\nclick NO to end the test now", title)) {
        int debugLevel = Debug.getDebugLevel();
        Debug.on(3);
        ScreenImage sIMg = aScr.userCapture("AndroidTest");
        sIMg.getFile(RunTime.get().fSikulixStore.getAbsolutePath(), "android");
        saveShot();
        Debug.on(debugLevel);
        try {
          aScr.aTap(new Image(sIMg));
          Sikulix.popup("The image was found on the device's current screen" +
                  "\nand should have been tapped.\n" +
                  "\nIf you think it worked, you can now try\n" +
                  "to capture needed images from the device - be aware:\n" +
                  "\nYou have to come back here and click Default!", title);
        } catch (FindFailed findFailed) {
          Sikulix.popError("Sorry, the image you captured was\nnot found on the device's current screen", title);
          cancelled = true;
        }
      } else {
        cancelled = true;
      }
    }
    if (cancelled) {
      if (Sikulix.popAsk("You have cancelled or the image was not found.\n" +
              "\nclick YES to produce some output for debugging" +
              "\n... which may take a while !!" +
              "\nclick NO to simply leave", title)) {
        int debugLevel = Debug.getDebugLevel();
        Debug.on(3);
        ADBScreen.stop();
        ADBScreen adbScreen = ADBScreen.start();
        adbScreen.getDevice().printDump();
        ADBScreen.stop();
        Debug.on(debugLevel);
      }
    }
  }

  private Object androidSupport(Object ide) {
    int WARNING_CANCEL = 2;
    int WARNING_ACCEPTED = 1;
    int WARNING_DO_NOTHING = 0;
    ADBScreen aScr = new ADBScreen();
    String title = "Android Support - !!EXPERIMENTAL!!";
    if (aScr.isValid()) {
      String warn = "Device found: " + aScr.getDeviceDescription() + "\n\n" +
              "click Check: a short test is run with the device\n" +
              "click Default...: set device as default screen for capture\n" +
              "click Cancel: capture is reset to local screen\n" +
              "\nBE PREPARED: Feature is experimental - no guarantee ;-)";
      String[] options = new String[3];
      options[WARNING_DO_NOTHING] = "Check";
      options[WARNING_ACCEPTED] = "Default Android";
      options[WARNING_CANCEL] = "Cancel";
      int ret = JOptionPane.showOptionDialog((JFrame) ide, warn, title, 0, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
      if (ret == WARNING_CANCEL || ret == JOptionPane.CLOSED_OPTION) {
        return null;
      }
      if (ret == WARNING_DO_NOTHING) {
        Thread test = new Thread() {
          @Override
          public void run() {
            androidSupportTest(aScr);
          }
        };
        test.start();
      } else if (ret == WARNING_ACCEPTED) {
        return aScr;
      }
    } else if (!ADBClient.isAdbAvailable) {
      Sikulix.popError("Package adb seems not to be available.\nIt must be installed for Android support.", title);
    } else {
      Sikulix.popError("No android device attached", title);
    }
    return null;
  }

  private void androidSupportTest(ADBScreen aScr) {
    ADBTest.ideTest(aScr);
    ADBScreen.stop();
  }
}
