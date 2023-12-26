/*Copyright ©2020 TommyLemon(https://github.com/TommyLemon/UIAuto)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package uiauto;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RomUtil {

    private static final String TAG = "RomUtil";

    /**
     * 获取 emui 版本号
     */
    public static double getEmuiVersion() {
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            String version = emuiVersion == null ? null : emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.valueOf(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4.0;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (Exception ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    public static boolean checkIsHuaweiRom() {
        return Build.MANUFACTURER.contains("HUAWEI");
    }

    public static boolean checkIsMiuiRom() {
        return ! TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static boolean checkIsMeizuRom() {
        String systemProperty = getSystemProperty("ro.build.display.id");
        if (TextUtils.isEmpty(systemProperty)) {
            return false;
        }
        return systemProperty.contains("flyme") || systemProperty.toLowerCase().contains("flyme");
    }

    public static boolean checkIs360Rom() {
        return Build.MANUFACTURER.contains("QiKU") || Build.MANUFACTURER.contains("360");
    }

    public static boolean checkIsOppoRom() {
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo");
    }

    public static boolean checkIsVivoRom() {
        return Build.MANUFACTURER.contains("VIVO") || Build.MANUFACTURER.contains("vivo");
    }

}
