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

    public static class Video {
        private int state;

        public int getState() {
            return state;
        }

        public Video(final int state) {
            this.state = state;
        }
    }

    public static class Weather {
        private String weather;

        private String temperature;

        public String getWeather() {
            return weather;
        }

        public String getTemperature() {
            return temperature;
        }

        public Weather(final String weather, final String temperature) {
            this.weather = weather;
            this.temperature = temperature;
        }
    }

}
