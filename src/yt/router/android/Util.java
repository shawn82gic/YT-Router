package yt.router.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;

public class Util {
    static final String MORPHE = "app.morphe.android.youtube";
    static final String CHROME = "com.android.chrome";
    static final String OFFICIAL = "com.google.android.youtube";
    static final String YT_TEST = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    static final String WEB_TEST = "https://www.example.com";

    static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences("p", Context.MODE_PRIVATE);
    }

    static List<ResolveInfo> handlers(Context c, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        List<ResolveInfo> raw = c.getPackageManager().queryIntentActivities(i, 0x20000);
        List<ResolveInfo> out = new ArrayList<ResolveInfo>();
        List<String> seen = new ArrayList<String>();
        for (ResolveInfo r : raw) {
            String p = r.activityInfo.packageName;
            if (p.equals(c.getPackageName()) || seen.contains(p)) continue;
            seen.add(p);
            out.add(r);
        }
        return out;
    }

    static List<String> pkgs(List<ResolveInfo> l) {
        List<String> o = new ArrayList<String>();
        for (ResolveInfo r : l) o.add(r.activityInfo.packageName);
        return o;
    }

    static List<String> browserPkgs(Context c) {
        List<String> l = pkgs(handlers(c, WEB_TEST));
        l.remove(MORPHE);
        return l;
    }

    // order: Morphe, other third-party apps, official YouTube last
    static List<ResolveInfo> ytApps(Context c) {
        List<String> br = browserPkgs(c);
        List<ResolveInfo> o = new ArrayList<ResolveInfo>();
        ResolveInfo morphe = null, official = null;
        for (ResolveInfo r : handlers(c, YT_TEST)) {
            String p = r.activityInfo.packageName;
            if (br.contains(p)) continue;
            if (p.equals(MORPHE)) morphe = r;
            else if (p.equals(OFFICIAL)) official = r;
            else o.add(r);
        }
        if (morphe != null) o.add(0, morphe);
        if (official != null) o.add(official);
        return o;
    }

    static boolean officialInstalled(Context c) {
        return pkgs(handlers(c, YT_TEST)).contains(OFFICIAL);
    }

    // saved choice if still installed; otherwise Morphe > other third-party > official YouTube
    static String ytChoice(Context c) {
        String s = prefs(c).getString("yt", null);
        List<String> apps = pkgs(ytApps(c));
        if (s != null && apps.contains(s)) return s;
        return apps.isEmpty() ? null : apps.get(0);
    }

    // ordered list: chosen browser first, then Chrome, then the rest
    static List<String> browserOrder(Context c) {
        List<String> all = browserPkgs(c);
        List<String> o = new ArrayList<String>();
        String s = prefs(c).getString("browser", "auto");
        if (!s.equals("auto") && all.contains(s)) o.add(s);
        if (all.contains(CHROME) && !o.contains(CHROME)) o.add(CHROME);
        for (String p : all) if (!o.contains(p)) o.add(p);
        return o;
    }

    // Android 12+: is official YouTube allowed to grab verified links? null = unknown
    static Boolean officialLinksAllowed(Context c) {
        if (Build.VERSION.SDK_INT < 31) return null;
        try {
            Object dvm = c.getSystemService("domain_verification");
            if (dvm == null) return null;
            Object st = dvm.getClass().getMethod("getDomainVerificationUserState", String.class)
                    .invoke(dvm, OFFICIAL);
            if (st == null) return null;
            return (Boolean) st.getClass().getMethod("isLinkHandlingAllowed").invoke(st);
        } catch (Throwable t) {
            return null;
        }
    }
}
