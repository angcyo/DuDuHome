package org.scf4a;

public class EventRead {
    public static class L0ReadDone {
        private byte[] data;

        public L0ReadDone(final byte[] data) {

            this.data = data;
        }

        public byte[] getData() {
            return data;
        }
    }

    public static class L1ReadDone {
        private byte[] data;

        public L1ReadDone(final byte[] data) {

            this.data = data;
        }

        public byte[] getData() {
            return data;
        }
    }
}
