package org.formula;

import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class vpncheck {

    public static boolean isVPN(String ip) {
        try {
            return checkIPAPI(ip) || checkIPWhois(ip) || checkProxyCheck(ip);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JSONObject getIPDetails(String ip) {
        try {
            URL url = new URL("http://ip-api.com/json/" + ip + "?fields=status,country,region,city,isp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            return new JSONObject(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static boolean checkIPAPI(String ip) {
        try {
            URL url = new URL("http://ip-api.com/json/" + ip + "?fields=proxy,hosting,mobile");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject obj = new JSONObject(json.toString());
            return obj.optBoolean("proxy", false) || obj.optBoolean("hosting", false) || obj.optBoolean("mobile", false);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkIPWhois(String ip) {
        try {
            URL url = new URL("https://ipwhois.app/json/" + ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject obj = new JSONObject(json.toString());
            return obj.optBoolean("is_proxy", false) || obj.optBoolean("is_tor", false) || obj.optBoolean("is_datacenter", false);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkProxyCheck(String ip) {
        try {
            URL url = new URL("http://proxycheck.io/v2/" + ip + "?vpn=1&asn=1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject obj = new JSONObject(json.toString());
            if (obj.has(ip)) {
                JSONObject data = obj.getJSONObject(ip);
                return data.optString("proxy", "no").equals("yes") || data.optString("vpn", "no").equals("yes");
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}