package com.dudu.navi.entity;

import com.dudu.navi.vauleObject.NaviDriveMode;
import com.dudu.navi.vauleObject.NavigationType;

/**
 * Created by pc on 2015/11/14.
 */
public class Navigation {

    private Point destination;

    private NaviDriveMode driveMode;

    private NavigationType type;

    public Navigation(Point destination,NaviDriveMode driveMode,NavigationType type){

        this.driveMode = driveMode;
        this.destination = destination;
        this.type = type;
    }

    public Point getDestination() {
        return destination;
    }

    public NaviDriveMode getDriveMode() {
        return driveMode;
    }

    public NavigationType getType(){
        return type;
    }
}


