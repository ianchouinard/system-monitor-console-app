package com.ianchouinard.cpuusagestats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        CpuUsage cpuUsage = new CpuUsage();
        cpuUsage.startMonitoringSystem();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            System.out.println("Starting program. Will display results every 5 seconds.");
            System.out.println("Press enter to stop monitoring and see totals.");
            String s = null;

            try {
                s = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(s.length() == 0){
                cpuUsage.stopMonitoringSystem();
                cpuUsage.getStatOutliers();
                System.out.println("Exiting...");
                System.exit(0);
            }
        }
    }
}
