package org.scf4a;

public class EventWrite {

    public enum TYPE {
        Data,
        Ack
    }

    public static class Data2Write {
        public TYPE type;
        public byte[] data;

        public Data2Write(final byte[] data, final TYPE type) {
            this.type = type;
            this.data = data;
        }
    }

    public static class CancelWrite {
    }

    public static class L0WriteDone {
    }

    public static class L0WriteFail {
    }

    public static class L1WriteDone {
    }

    public static class L1WriteFail {
    }

    public static class L2WriteDone {
        public TYPE type;

        public L2WriteDone(final TYPE type) {
            this.type = type;
        }
    }

    public static class L2WriteFail {
    }
}
