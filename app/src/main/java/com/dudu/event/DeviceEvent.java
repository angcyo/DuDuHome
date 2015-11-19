package com.dudu.event;

public class DeviceEvent {
    public static final int ON = 1;
    public static final int OFF = 0;

    public static class Screen {
        private int state;

        public int getState() {
            return state;
        }

        public Screen(final int state) {

            this.state = state;
        }
    }

    public static class GPS {
        private int state;

        public int getState() {
            return state;
        }

        public GPS(final int state) {

            this.state = state;
        }
    }

}
