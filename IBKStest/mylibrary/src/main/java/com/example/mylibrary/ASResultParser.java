package com.example.mylibrary;

import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.accent_systems.ibks_sdk.utils.ASUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gabriel on 21/04/2016.
 */
public class ASResultParser {

    public static boolean isIBeacon(ScanResult result) {
        if (result.getScanRecord() != null && result.getScanRecord().getBytes().length > 9) {
            if (byteArrayToHex(result.getScanRecord().getBytes()).substring(6, 12).equals("1bff4c") || byteArrayToHex(result.getScanRecord().getBytes()).substring(6, 12).equals("1aff4c")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEddystoneUID(ScanResult result) {
        if (result.getScanRecord() != null && result.getScanRecord().getBytes().length > 9) {
            if (byteArrayToHex(result.getScanRecord().getBytes()).substring(18, 24).equals("aafe00")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEddystoneURL(ScanResult result) {
        if (result.getScanRecord() != null && result.getScanRecord().getBytes().length > 9) {
            if (byteArrayToHex(result.getScanRecord().getBytes()).substring(18, 24).equals("aafe10")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEddystoneTLM(ScanResult result) {
        if (result.getScanRecord() != null && result.getScanRecord().getBytes().length > 9) {
            if (byteArrayToHex(result.getScanRecord().getBytes()).substring(18, 24).equals("aafe20")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEddystoneEID(ScanResult result) {
        if (result.getScanRecord() != null && result.getScanRecord().getBytes().length > 9) {
            if (byteArrayToHex(result.getScanRecord().getBytes()).substring(18, 24).equals("aafe30")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDFU(ScanResult result) {
        if (result.getScanRecord() != null && result.getScanRecord().getBytes().length > 9) {
            if (byteArrayToHex(result.getScanRecord().getBytes()).substring(0, 4).equals("0909")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDeviceConnectable(ScanResult result) {
        if (result.getScanRecord() != null && result.getScanRecord().getBytes().length > 9) {
            if (byteArrayToHex(result.getScanRecord().getBytes()).substring(0, 18).equals("0201061107d881c91a")) {
                return true;
            }
        }
        return false;
    }

    public static int getAdvertisingType(ScanResult result) {
        if (result.getScanRecord() != null && result.getScanRecord().getBytes().length > 9) {
            if (isIBeacon(result)) {
                return ASUtils.TYPE_IBEACON;
            } else if (isEddystoneUID(result)) {
                return ASUtils.TYPE_EDDYSTONE_UID;
            } else if (isEddystoneURL(result)) {
                return ASUtils.TYPE_EDDYSTONE_URL;
            } else if (isEddystoneTLM(result)) {
                return ASUtils.TYPE_EDDYSTONE_TLM;
            } else if (isEddystoneEID(result)) {
                return ASUtils.TYPE_EDDYSTONE_EID;
            } else if (isDeviceConnectable(result)) {
                return ASUtils.TYPE_DEVICE_CONNECTABLE;
            } else {
                return ASUtils.TYPE_UNKNOWN;
            }
        } else {
            return ASUtils.ERROR_INCORRECT_SCAN_RESULT;
        }
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byte nByte = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
            data[i / 2] = nByte;
        }
        return data;
    }

    public static String StringHexToAscii(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String StringAsciiToHex(String rawIn) {
        /**
         * convert ascii string to HEX string
         */
        String rawBin = "";
        try {
            char[] chars = rawIn.toCharArray(); //convert string to individual chars
            for (int j = 0; j < chars.length; j++) {
                rawBin += Integer.toHexString(chars[j]);  //convert char to hex value
            }
        } catch (NumberFormatException e) {
            return rawIn;
        }
        return rawBin;
    }

    private static String stringPadLeft(int nbytes, String original) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() + original.length() < (nbytes * 2)) {
            sb.append("0");
        }
        sb.append(original);
        String paddedString = sb.toString();
        return paddedString;
    }

    public static byte[] intToHexByteArray(int nbytes, int val) {
        String hexstring = Integer.toHexString(val);
        if (val < 0) {
            int dif = hexstring.length() - (nbytes * 2);
            hexstring = hexstring.substring(dif);
        } else if (hexstring.length() > (nbytes * 2)) {
            Log.i("ASResultParser", "intToHexByteArray - value is greater than nbytes");
            return null;
        }

        String hex = stringPadLeft(nbytes, hexstring);
        byte b[] = hexStringToByteArray(hex);
        return b;
    }

    public static byte[] ConcatByteArrays(byte[] a, byte[] b) {
        byte[] cmd = new byte[a.length + b.length];
        System.arraycopy(a, 0, cmd, 0, a.length);
        System.arraycopy(b, 0, cmd, a.length, b.length);
        return cmd;
    }

    public static String ParseUrlToSend(String str) {
        String clean_url = "";

        clean_url = StringAsciiToHex(str);

        if (clean_url.contains("687474703a2f2f7777772e")) {
            clean_url = clean_url.replace("687474703a2f2f7777772e", "00");
        }
        if (clean_url.contains("68747470733a2f2f7777772e")) {
            clean_url = clean_url.replace("68747470733a2f2f7777772e", "01");
        }
        if (clean_url.contains("687474703a2f2f")) {
            clean_url = clean_url.replace("687474703a2f2f", "02");
        }
        if (clean_url.contains("68747470733a2f2f")) {
            clean_url = clean_url.replace("68747470733a2f2f", "03");
        }


        if (clean_url.contains("2e636f6d2f")) {
            clean_url = clean_url.replace("2e636f6d2f", "00");
        }
        if (clean_url.contains("2e6f72672f")) {
            clean_url = clean_url.replace("2e6f72672f", "01");
        }
        if (clean_url.contains("2e6564752f")) {
            clean_url = clean_url.replace("2e6564752f", "02");
        }
        if (clean_url.contains("2e6e65742f")) {
            clean_url = clean_url.replace("2e6e65742f", "03");
        }
        if (clean_url.contains("2e696e666f2f")) {
            clean_url = clean_url.replace("2e696e666f2f", "04");
        }
        if (clean_url.contains("2e62697a2f")) {
            clean_url = clean_url.replace("2e62697a2f", "05");
        }
        if (clean_url.contains("2e676f762f")) {
            clean_url = clean_url.replace("2e676f762f", "06");
        }
        if (clean_url.contains("2e636f6d")) {
            clean_url = clean_url.replace("2e636f6d", "07");
        }
        if (clean_url.contains("2e6f7267")) {
            clean_url = clean_url.replace("2e6f7267", "08");
        }
        if (clean_url.contains("2e656475")) {
            clean_url = clean_url.replace("2e656475", "09");
        }
        if (clean_url.contains("2e6e6574")) {
            clean_url = clean_url.replace("2e6e6574", "0a");
        }
        if (clean_url.contains("2e696e666f")) {
            clean_url = clean_url.replace("2e696e666f", "0b");
        }
        if (clean_url.contains("2e62697a")) {
            clean_url = clean_url.replace("2e62697a", "0c");
        }
        if (clean_url.contains("2e676f76")) {
            clean_url = clean_url.replace("2e676f76", "0d");
        }

        return clean_url;
    }

    public static String ParseUrlRx(String url) {

        String aux = "";
        String urlshort = "";
        String type = "";
        String encod = "";

        aux = url.substring(0, 2);

        if (aux.equals("00"))
            type = "http://www.";
        else if (aux.equals("01"))
            type = "https://www.";
        else if (aux.equals("02"))
            type = "http://";
        else if (aux.equals("03"))
            type = "https://";

        urlshort = url.substring(2, url.length());

        int len = urlshort.length() - 2;
        String encode = urlshort.substring(len, urlshort.length());

        String urlencode = "";
        for (int i = 0; i < urlshort.length(); i += 2) {
            encod = "";
            encode = urlshort.substring(i, i + 2);
            if (encode.contains("00")) {
                encod = ".com/";
            } else if (encode.contains("01")) {
                encod = ".org/";
            } else if (encode.contains("02")) {
                encod = ".edu/";
            } else if (encode.contains("03")) {
                encod = ".net/";
            } else if (encode.contains("04")) {
                encod = ".info/";
            } else if (encode.contains("05")) {
                encod = ".biz/";
            } else if (encode.contains("06")) {
                encod = ".gov/";
            } else if (encode.contains("07")) {
                encod = ".com";
            } else if (encode.contains("08")) {
                encod = ".org";
            } else if (encode.contains("09")) {
                encod = ".edu";
            } else if (encode.contains("0A")) {
                encod = ".net";
            } else if (encode.contains("0B")) {
                encod = ".info";
            } else if (encode.contains("0C")) {
                encod = ".biz";
            } else if (encode.contains("0D")) {
                encod = ".gov";
            }

            if (encod.equals(""))
                urlencode += ASResultParser.StringHexToAscii(urlshort.substring(i, i + 2));
            else
                urlencode += encod;
        }

        String urlstr = "";

        urlstr = type + urlencode;

        return urlstr;

    }

    public static JSONObject getDataFromAdvertising(ScanResult result) {

        JSONObject advData = new JSONObject();
        int intVal, advtxpower;
        /*JSONObject advertisedId = new JSONObject()
                .put("type", "EDDYSTONE")
                .put("id", ASUtils.base64Encode(ASResultParser.hexStringToByteArray(IDEID)));*/

        try {
            if(result != null) {
                String packet = ASResultParser.byteArrayToHex(result.getScanRecord().getBytes());
                switch (ASResultParser.getAdvertisingType(result)) {
                    case ASUtils.TYPE_IBEACON:
                        advData.put("FrameType", ASUtils.TYPE_IBEACON);
                        intVal = Integer.parseInt(packet.substring(58,60), 16);

                        if((intVal & 128) == 128)
                            advtxpower = -1 * ((intVal ^ 255) + 1);
                        else
                            advtxpower = intVal;
                        advData.put("AdvTxPower", advtxpower );
                        advData.put("UUID", packet.substring(18, 50));
                        advData.put("Major", packet.substring(50, 54));
                        advData.put("Minor", packet.substring(54, 58));

                        break;
                    case ASUtils.TYPE_EDDYSTONE_UID:
                        advData.put("FrameType", ASUtils.TYPE_EDDYSTONE_UID);
                        intVal = Integer.parseInt(packet.substring(24,26), 16);

                        if((intVal & 128) == 128)
                            advtxpower = -1 * ((intVal ^ 255) + 1);
                        else
                            advtxpower = intVal;
                        advData.put("AdvTxPower", advtxpower );
                        advData.put("Namespace", packet.substring(26, 46));
                        advData.put("Instance", packet.substring(46, 58));

                        break;
                    case ASUtils.TYPE_EDDYSTONE_URL:
                        advData.put("FrameType", ASUtils.TYPE_EDDYSTONE_URL);
                        intVal = Integer.parseInt(packet.substring(24,26), 16);

                        if((intVal & 128) == 128)
                            advtxpower = -1 * ((intVal ^ 255) + 1);
                        else
                            advtxpower = intVal;
                        advData.put("AdvTxPower", advtxpower );
                        int len = ((Integer.parseInt(packet.substring(14,16),16))-5)*2;
                        String url = packet.substring(26, 26+len);
                        advData.put("Url",ParseUrlRx(url));
                        break;
                    case ASUtils.TYPE_EDDYSTONE_TLM:
                        advData.put("FrameType", ASUtils.TYPE_EDDYSTONE_TLM);
                        int version = Integer.parseInt(packet.substring(24,26), 16);
                        advData.put("Version",version );
                       if(version == 0){
                           float temp = (float)Long.parseLong(packet.substring(30, 32), 16)+((float)Long.parseLong(packet.substring(32, 34), 16))/256.0f;
                           long time_s = Long.parseLong(packet.substring(42, 50), 16)/10;

                           advData.put("Vbatt",Long.parseLong(packet.substring(26, 30), 16) );
                           advData.put("Temp",String.format("%.2f", temp));
                           advData.put("AdvCount",Long.parseLong(packet.substring(34, 42), 16));
                           advData.put("TimeUp",Long.toString(time_s));

                       }
                       else{
                           advData.put("EncryptedTLMData",packet.substring(26, 50));
                           advData.put("Salt",packet.substring(50, 54));
                           advData.put("IntegrityCheck",packet.substring(54, 58));
                       }

                        break;
                    case ASUtils.TYPE_EDDYSTONE_EID:
                        advData.put("FrameType", ASUtils.TYPE_EDDYSTONE_EID);
                        intVal = Integer.parseInt(packet.substring(24,26), 16);

                        if((intVal & 128) == 128)
                            advtxpower = -1 * ((intVal ^ 255) + 1);
                        else
                            advtxpower = intVal;
                        advData.put("AdvTxPower", advtxpower );
                        advData.put("EID",packet.substring(26,42));
                        break;
                    case ASUtils.TYPE_DEVICE_CONNECTABLE:
                        advData.put("FrameType", ASUtils.TYPE_DEVICE_CONNECTABLE);
                        break;
                    case ASUtils.TYPE_UNKNOWN:
                        advData.put("FrameType", ASUtils.TYPE_UNKNOWN);
                        break;
                    default:

                        break;
                }
            }
        } catch (JSONException e) {

            advData = null;
            return advData;
        }
        return advData;
    }
}
