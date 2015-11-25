package com.dudu.navi.entity;

import com.dudu.navi.vauleObject.NaviDriveMode;

/**
 * Created by pc on 2015/11/14.
 */
public class Navigation {


    private Point destination;

    private NaviDriveMode driveMode;

    public Navigation(Point destination,NaviDriveMode driveMode){

        this.driveMode = driveMode;
        this.destination = destination;
    }

    public Point getDestination() {
        return destination;
    }

    public NaviDriveMode getDriveMode() {
        return driveMode;
    }
}


