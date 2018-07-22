package com.ianchouinard.cpuusagestats;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.*;

public class CpuUsage {

    private OperatingSystemMXBean system;
    private Timer timer;
    private StatsModel stats;
    private List<StatsModel> history;

    CpuUsage() {
        this.system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.stats = new StatsModel();
        this.timer = new Timer();
        this.history = new ArrayList<StatsModel>();
    }

    public void startMonitoringSystem() {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                querySystem();
            }

        }, 5000, 5000);
    }

    public void stopMonitoringSystem() {
        timer.cancel();
        timer.purge();
    }

    public void getStatOutliers() {
        OutlierModel output = new OutlierModel();

        for (StatsModel stat : history) {
            output.maxCpu = Math.max(stat.cpuLoad, output.maxCpu);
            if (output.minCpu > 0) {
                output.minCpu = Math.min(stat.cpuLoad, output.minCpu);
            } else {
                output.minCpu = stat.cpuLoad;
            }

            output.maxFreeMemoryGB = Math.max(stat.totalFreeMemoryGB, output.maxFreeMemoryGB);
            if (output.minFreeMemoryGB > 0) {
                output.minFreeMemoryGB = Math.min(stat.totalFreeMemoryGB, output.minFreeMemoryGB);
            } else {
                output.minFreeMemoryGB = stat.totalFreeMemoryGB;
            }

            output.maxUsedMemoryGB = (stat.totalPhysicalMemoryGB - output.minFreeMemoryGB);
            output.minUsedMemoryGB = (stat.totalPhysicalMemoryGB - output.maxFreeMemoryGB);
        }

        System.out.println("Highest CPU Load Recorded (%): " + toPercent(output.maxCpu));
        System.out.println("Lowest CPU Load Recorded (%): " + toPercent(output.minCpu));
        System.out.println("Highest Used Memory Recorded (GB): " + output.maxUsedMemoryGB);
        System.out.println("Lowest Used Memory Recorded (GB): " + output.minUsedMemoryGB);
    }

    private void querySystem() {
        stats.totalPhysicalMemory = system.getTotalPhysicalMemorySize();
        stats.totalFreeMemory = system.getFreePhysicalMemorySize();
        stats.totalPhysicalMemoryGB = byteToGB(stats.totalPhysicalMemory);
        stats.totalFreeMemoryGB = byteToGB(stats.totalFreeMemory);
        stats.cpuLoad = system.getSystemCpuLoad();
        stats.architecture = system.getArch();
        stats.operatingSystem = system.getName();
        stats.operatingSystemVersion = system.getVersion();
        stats.timestamp = new SimpleDateFormat("HH.mm.ss").format(new Date());

        printStatus();
        updateHistory();
    }

    private void updateHistory() {
        history.add(stats);
        stats = new StatsModel();
    }

    private void printStatus() {
        System.out.println("CPU Load (%): " + toPercent(stats.cpuLoad));
        System.out.println("Total Memory (GB): " + stats.totalPhysicalMemoryGB);
        System.out.println("Total Free Memory (GB): " + stats.totalFreeMemoryGB);
        System.out.println("Total Used Memory (GB): " + (stats.totalPhysicalMemoryGB - stats.totalFreeMemoryGB));
        System.out.println("Time: " + stats.timestamp);
        System.out.println("-------------------------");
    }

    private static long byteToGB(long byteValue) {
        // Convert to double to we can round uo to get the correct GB value.
        double converted = (byteValue / (double)1073741824);
        return (long)Math.ceil(converted);
    }

    private static float toPercent(double decimalValue) {
        double converted = decimalValue * 100;
        return (float) converted;
    }

}
