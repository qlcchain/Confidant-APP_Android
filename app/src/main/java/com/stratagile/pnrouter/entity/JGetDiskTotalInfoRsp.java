package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JGetDiskTotalInfoRsp extends BaseEntity {


    /**
     * timestamp : 1550200940
     * params : {"Action":"GetDiskTotalInfo","RetCode":0,"Mode":2,"Count":2,"UsedCapacity":"756M","TotalCapacity":"1.7G","Info":[{"Slot":0,"Status":2,"PowerOn":41,"Temperature":37,"Capacity":"1.5G","Device":"WDC WD10EZEX-08WN4A0","Serial":"WD-WCC6Y4UV92L8"},{"Slot":1,"Status":1}]}
     */

    private int timestampX;
    private ParamsBean params;

    public int getTimestampX() {
        return timestampX;
    }

    public void setTimestampX(int timestampX) {
        this.timestampX = timestampX;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * Action : GetDiskTotalInfo
         * RetCode : 0
         * Mode : 2
         * Count : 2
         * UsedCapacity : 756M
         * TotalCapacity : 1.7G
         * Info : [{"Slot":0,"Status":2,"PowerOn":41,"Temperature":37,"Capacity":"1.5G","Device":"WDC WD10EZEX-08WN4A0","Serial":"WD-WCC6Y4UV92L8"},{"Slot":1,"Status":1}]
         */

        private String Action;
        private int RetCode;
        private int Mode;
        private int Count;
        private String UsedCapacity;
        private String TotalCapacity;
        private List<InfoBean> Info;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public int getRetCode() {
            return RetCode;
        }

        public void setRetCode(int RetCode) {
            this.RetCode = RetCode;
        }

        public int getMode() {
            return Mode;
        }

        public void setMode(int Mode) {
            this.Mode = Mode;
        }

        public int getCount() {
            return Count;
        }

        public void setCount(int Count) {
            this.Count = Count;
        }

        public String getUsedCapacity() {
            return UsedCapacity;
        }

        public void setUsedCapacity(String UsedCapacity) {
            this.UsedCapacity = UsedCapacity;
        }

        public String getTotalCapacity() {
            return TotalCapacity;
        }

        public void setTotalCapacity(String TotalCapacity) {
            this.TotalCapacity = TotalCapacity;
        }

        public List<InfoBean> getInfo() {
            return Info;
        }

        public void setInfo(List<InfoBean> Info) {
            this.Info = Info;
        }

        public static class InfoBean {
            /**
             * Slot : 0
             * Status : 2
             * PowerOn : 41
             * Temperature : 37
             * Capacity : 1.5G
             * Device : WDC WD10EZEX-08WN4A0
             * Serial : WD-WCC6Y4UV92L8
             */

            private int Slot;
            private int Status;
            private int PowerOn;
            private int Temperature;
            private String Capacity;
            private String Device;
            private String Serial;

            public int getSlot() {
                return Slot;
            }

            public void setSlot(int Slot) {
                this.Slot = Slot;
            }

            public int getStatus() {
                return Status;
            }

            public void setStatus(int Status) {
                this.Status = Status;
            }

            public int getPowerOn() {
                return PowerOn;
            }

            public void setPowerOn(int PowerOn) {
                this.PowerOn = PowerOn;
            }

            public int getTemperature() {
                return Temperature;
            }

            public void setTemperature(int Temperature) {
                this.Temperature = Temperature;
            }

            public String getCapacity() {
                return Capacity;
            }

            public void setCapacity(String Capacity) {
                this.Capacity = Capacity;
            }

            public String getDevice() {
                return Device;
            }

            public void setDevice(String Device) {
                this.Device = Device;
            }

            public String getSerial() {
                return Serial;
            }

            public void setSerial(String Serial) {
                this.Serial = Serial;
            }
        }
    }
}
