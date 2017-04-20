package com.ibm.ws.monitor.internal;

import com.ibm.websphere.monitor.meters.StatisticsMeter;

public class HardCodedPrototype {

    static boolean probesEnabled = false;

    public static void main(String[] args) {
        StatisticsMeter disabledProbeMeter = new StatisticsMeter();
        StatisticsMeter enabledProbeMeter = new StatisticsMeter();

        // Warm up
        method1(30);

        // Roughly 25! total method calls (15511210043330985984000000)
        for (int i = 0; i < 1000; i++) {
            long startTime = System.currentTimeMillis();
            method1(25);
            disabledProbeMeter.addDataPoint(System.currentTimeMillis() - startTime);
        }
        System.out.println("Disabled probes == " + disabledProbeMeter);

        probesEnabled = true;
        for (int i = 0; i < 1000; i++) {
            long startTime = System.currentTimeMillis();
            method1(25);
            enabledProbeMeter.addDataPoint(System.currentTimeMillis() - startTime);
        }

        System.out.println("Enabled probes  == " + enabledProbeMeter);
    }

    static void method1(int count) {
        for (int i = count - 1; i > 0; i--) {
            method2(i);
        }
    }

    static void method2(int count) {
        method1(count);
    }
}
