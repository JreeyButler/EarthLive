/*
 * Copyright (c) [2019] [Dipper]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dipper.earthlive.model;

import java.util.List;

/**
 * @author Dipper
 * @date 2019/6/16
 */
public class VersionInfo {

    /**
     * outputType : {"type":"APK"}
     * apkData : {"type":"MAIN","splits":[],"versionCode":1,"versionName":"1.0","enabled":true,"outputFile":"app-debug.apk","fullName":"debug","baseName":"debug"}
     * path : app-debug.apk
     * properties : {}
     */

    private OutputTypeBean outputType;
    private ApkDataBean apkData;
    private String path;
    private PropertiesBean properties;

    public OutputTypeBean getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputTypeBean outputType) {
        this.outputType = outputType;
    }

    public ApkDataBean getApkData() {
        return apkData;
    }

    public void setApkData(ApkDataBean apkData) {
        this.apkData = apkData;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PropertiesBean getProperties() {
        return properties;
    }

    public void setProperties(PropertiesBean properties) {
        this.properties = properties;
    }

    public static class OutputTypeBean {
        /**
         * type : APK
         */

        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "OutputTypeBean{" +
                    "type='" + type + '\'' +
                    '}';
        }
    }

    public static class ApkDataBean {
        /**
         * type : MAIN
         * splits : []
         * versionCode : 1
         * versionName : 1.0
         * enabled : true
         * outputFile : app-debug.apk
         * fullName : debug
         * baseName : debug
         */

        private String type;
        private int versionCode;
        private String versionName;
        private boolean enabled;
        private String outputFile;
        private String fullName;
        private String baseName;
        private List<?> splits;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getOutputFile() {
            return outputFile;
        }

        public void setOutputFile(String outputFile) {
            this.outputFile = outputFile;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getBaseName() {
            return baseName;
        }

        public void setBaseName(String baseName) {
            this.baseName = baseName;
        }

        public List<?> getSplits() {
            return splits;
        }

        public void setSplits(List<?> splits) {
            this.splits = splits;
        }

        @Override
        public String toString() {
            return "ApkDataBean{" +
                    "type='" + type + '\'' +
                    ", versionCode=" + versionCode +
                    ", versionName='" + versionName + '\'' +
                    ", enabled=" + enabled +
                    ", outputFile='" + outputFile + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", baseName='" + baseName + '\'' +
                    ", splits=" + splits +
                    '}';
        }
    }

    public static class PropertiesBean {
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "outputType=" + outputType +
                ", apkData=" + apkData +
                ", path='" + path + '\'' +
                ", properties=" + properties +
                '}';
    }
}
