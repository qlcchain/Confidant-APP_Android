package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JGetDiskDetailInfoRsp extends BaseEntity {


    /**
     * timestamp : 1550224667
     * params : {"Action":"GetDiskDetailInfo","RetCode":0,"Slot":0,"Status":2,"Name":"/dev/sda","Device":"WDC WD10EZEX-08WN4A0","Serial":"WD-WCC6Y4UV92L8","Firmware":"02.01A02","FormFactor":"3.5 inches","LUWWNDeviceId":"5 0014ee 265bdf6ba","ModelFamily":"Western Digital Blue","Capacity":"1,000,204,886,016 bytes [1.00 TB]","SectorSizes":"512 bytes logical, 4096 bytes physical","RotationRate":"7200 rpm","ATAVersion":"ACS-3 T13/2161-D revision 3b","SATAVersion":"SATA 3.1, 6.0 Gb/s","SMARTsupport":"Available - device has SMART capability"}
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
         * Action : GetDiskDetailInfo
         * RetCode : 0
         * Slot : 0
         * Status : 2
         * Name : /dev/sda
         * Device : WDC WD10EZEX-08WN4A0
         * Serial : WD-WCC6Y4UV92L8
         * Firmware : 02.01A02
         * FormFactor : 3.5 inches
         * LUWWNDeviceId : 5 0014ee 265bdf6ba
         * ModelFamily : Western Digital Blue
         * Capacity : 1,000,204,886,016 bytes [1.00 TB]
         * SectorSizes : 512 bytes logical, 4096 bytes physical
         * RotationRate : 7200 rpm
         * ATAVersion : ACS-3 T13/2161-D revision 3b
         * SATAVersion : SATA 3.1, 6.0 Gb/s
         * SMARTsupport : Available - device has SMART capability
         */

        private String Action;
        private int RetCode;
        private int Slot;
        private int Status;
        private String Name;
        private String Device;
        private String Serial;
        private String Firmware;
        private String FormFactor;
        private String LUWWNDeviceId;
        private String ModelFamily;
        private String Capacity;
        private String SectorSizes;
        private String RotationRate;
        private String ATAVersion;
        private String SATAVersion;
        private String SMARTsupport;

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

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
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

        public String getFirmware() {
            return Firmware;
        }

        public void setFirmware(String Firmware) {
            this.Firmware = Firmware;
        }

        public String getFormFactor() {
            return FormFactor;
        }

        public void setFormFactor(String FormFactor) {
            this.FormFactor = FormFactor;
        }

        public String getLUWWNDeviceId() {
            return LUWWNDeviceId;
        }

        public void setLUWWNDeviceId(String LUWWNDeviceId) {
            this.LUWWNDeviceId = LUWWNDeviceId;
        }

        public String getModelFamily() {
            return ModelFamily;
        }

        public void setModelFamily(String ModelFamily) {
            this.ModelFamily = ModelFamily;
        }

        public String getCapacity() {
            return Capacity;
        }

        public void setCapacity(String Capacity) {
            this.Capacity = Capacity;
        }

        public String getSectorSizes() {
            return SectorSizes;
        }

        public void setSectorSizes(String SectorSizes) {
            this.SectorSizes = SectorSizes;
        }

        public String getRotationRate() {
            return RotationRate;
        }

        public void setRotationRate(String RotationRate) {
            this.RotationRate = RotationRate;
        }

        public String getATAVersion() {
            return ATAVersion;
        }

        public void setATAVersion(String ATAVersion) {
            this.ATAVersion = ATAVersion;
        }

        public String getSATAVersion() {
            return SATAVersion;
        }

        public void setSATAVersion(String SATAVersion) {
            this.SATAVersion = SATAVersion;
        }

        public String getSMARTsupport() {
            return SMARTsupport;
        }

        public void setSMARTsupport(String SMARTsupport) {
            this.SMARTsupport = SMARTsupport;
        }
    }
}
